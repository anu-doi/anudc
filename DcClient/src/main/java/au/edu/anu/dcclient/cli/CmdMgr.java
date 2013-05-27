/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.dcclient.cli;

import static java.text.MessageFormat.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.dcclient.DcAuthenticator;
import au.edu.anu.dcclient.TaskSummary;
import au.edu.anu.dcclient.collection.CollectionInfo;
import au.edu.anu.dcclient.stopwatch.StopWatch;
import au.edu.anu.dcclient.tasks.CreateCollectionTask;
import au.edu.anu.dcclient.tasks.EnumDirTask;
import au.edu.anu.dcclient.tasks.FileTaskInfo;
import au.edu.anu.dcclient.tasks.UploadFilesetTask;

/**
 * Executes bag-related tasks based on the specified command line arguments. The following tasks are supported:
 * <ul>
 * <li>Creates/updates a record on DataCommons and uploads files specified in a parameter file against it replacing any
 * existing files on Data Commons.</li>
 * <li>Downloads a bag from Data Commons along with the files it contains.</li>
 * <li>Packages files into a bag making them ready for upload.</li>
 * <li>Uploads a bag to Data Commons.</li>
 * </ul>
 */
public class CmdMgr {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdMgr.class);
	private Options OPTIONS = new Options();

	private int exitCode = 0;
	private TaskSummary summary = new TaskSummary();

	/**
	 * Constructor which takes the command line parameters as a String array.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public CmdMgr(String[] args) {
		setupOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmdLine = parser.parse(OPTIONS, args);

			if (cmdLine.hasOption('h')) {
				// If the command line contains help.
				dispHelp(OPTIONS);
			} else if (!cmdLine.hasOption("u")) {
				dispHelp(OPTIONS);
			} else if (cmdLine.hasOption('d')) {
				// Download.
				setCredentials(cmdLine);
				download(cmdLine);
			} else if (cmdLine.hasOption('l')) {
				// Upload Bag.
				setCredentials(cmdLine);
				upload(cmdLine);
			} else if (cmdLine.hasOption('c')) {
				// Parameter file.
				setCredentials(cmdLine);
				processParamFile(cmdLine);
			}
		} catch (ParseException e) {
			dispHelp(OPTIONS);
		}
	}

	private void setupOptions() {
		Option paramFile = new Option("c", "param-file", true, "Parameter file containing record information.");
		Option download = new Option("d", "download", true, "Pid of the item whose bag to download.");
		Option upload = new Option("l", "upload", true, "Pid of the item whose bag to upload.");
		
		Option username = new Option("u", "username", true, "Username to be used for logging into ANU Data Commons");
		Option password = new Option("p", "password", true, "Password to be used for logging into ANU Data Commons.");
		
		Option help = new Option("h", "help", false, "Display help");

		OPTIONS.addOption(paramFile);
		OPTIONS.addOption(download);
		OPTIONS.addOption(upload);
		OPTIONS.addOption(username);
		OPTIONS.addOption(password);
		OPTIONS.addOption(help);
	}

	/**
	 * Extracts credentials provided in the command line and sets them for requests sent to Data Commons.
	 * 
	 * @param cmdLine
	 *            Parsed command line as CommandLine
	 */
	private void setCredentials(CommandLine cmdLine) {
		String username = null;
		String password = null;

		File credsFile = new File(cmdLine.getOptionValue('u'));
		if (credsFile.exists()) {
			try {
				PropertiesFile credsProps = new PropertiesFile(credsFile);
				username = credsProps.getProperty("username");
				password = credsProps.getProperty("password");
			} catch (IOException e) {
				System.out.println(format("Unable to read credentials file at {0} . Check permissions and try again.",
						credsFile.getAbsolutePath()));
			}
		} else {
			username = cmdLine.getOptionValue('u');
			password = cmdLine.getOptionValue('p');
			if (password == null) {
				password = new String(System.console().readPassword("Password: "));
			}
		}

		LOGGER.trace("Setting username {} and password **** for all requests to ANU Data Commons", username);
		Authenticator.setDefault(new DcAuthenticator(username, password));
	}

	/**
	 * Returns the exit code set representing the status of tasks performed.
	 * 
	 * @return 0 if successful, 1 if error
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * Print the help for options with the specified command line syntax.
	 * 
	 * @param options
	 *            Options object containing valid command line switches and help text.
	 */
	private void dispHelp(Options options) {
		HelpFormatter hf = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out);
		hf.printHelp("DcClient", options, true);
		writer.flush();
		writer = null;
	}

	/**
	 * Processes a parameter file and sends the required requests to Data Commons.
	 * 
	 * @param cmdLine
	 *            Parsed command line
	 */
	private void processParamFile(CommandLine cmdLine) {
		// Create FedoraObject and then upload files.
		File paramFile = new File(cmdLine.getOptionValue('c'));

		try {
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			// Read collection details.
			System.out.println("Reading values from collection file...");
			CollectionInfo ci = new CollectionInfo(paramFile);
			String pid = ci.getPid();
			if (pid == null) {
				// Pid doesn't exist in collection file. Create an object.
				CreateCollectionTask createCollTask = new CreateCollectionTask(ci);
				createCollTask.execute();
				pid = createCollTask.get();
				System.out.println("Created collection with Pid: " + pid);
				summary.put("Pid", pid + " (new)");
			} else {
				System.out.println("Pid already exists for this collection: " + pid);
				summary.put("Pid", pid + " (existing)");
			}
			summary.put("Parameter file", paramFile.getAbsolutePath());

			// If local directory containing data files specified in parameter file, upload them.
			if (ci.getFilesDir() != null) {
				EnumDirTask enumDirTask = new EnumDirTask(ci.getFilesDir(), true) {
					private int fileCount = 0;
					
					@Override
					protected void process(List<File> chunks) {
						super.process(chunks);
						fileCount += chunks.size();
						System.out.println(Config.NEWLINE);
						System.out.println(format("\rFiles to upload: {0}", fileCount));
					}
				};
				enumDirTask.execute();
				
				UploadFilesetTask ulTask = new UploadFilesetTask(pid, enumDirTask.get());
				ulTask.addPropertyChangeListener(new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals("progress")) {
							System.out.println(format("\r{0}%", evt.getNewValue()));
						}
					}
				});
				System.out.println(Config.NEWLINE);
				ulTask.execute();
				Map<File, FileTaskInfo> uploadResults = ulTask.get();
				
				// Count success.
				for (Entry<File, FileTaskInfo> entry : uploadResults.entrySet()) {
					if (entry.getValue().getStatus() == FileTaskInfo.Status.FAILED) {
						summary.put(entry.getKey().getAbsolutePath(), "Failed to Upload");
					}
				}
			}
			stopWatch.end();
			summary.put("Total Time Taken", stopWatch.getFriendlyElapsed());
			summary.put("Started", new Date(stopWatch.getStartTimeInMs()).toString());
			summary.put("Ended", new Date(stopWatch.getEndTimeInMs()).toString());

			summary.display();
			exitCode = 0;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			System.out.println("Unable to complete operation.");
			exitCode = 1;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.out.println("Unable to complete operation.");
			exitCode = 1;
		}
	}

	/**
	 * Uploads a bag to Data Commons.
	 * 
	 * @param cmdLine
	 *            Parsed command line
	 */
	private void upload(CommandLine cmdLine) {
		// TODO Implement
		
//		String pid = cmdLine.getOptionValue('l');
//		DcBag bag = new DcBag(Global.getLocalBagStoreAsFile(), pid, LoadOption.BY_FILES);
//		StopWatch stopWatch = new StopWatch();
//		try {
//			stopWatch.start();
//			if (cmdLine.hasOption('i')) {
//				setDataSource(bag);
//				bag.save();
//			}
//			VerifyBagTask verifyTask = new VerifyBagTask(bag);
//			verifyTask.addProgressListener(new ConsoleProgressListener());
//			SimpleResult result;
//			// Verify current bag.
//			result = verifyTask.call();
//			if (result.isSuccess()) {
//				System.out.println("Verification complete. Bag is valid. Uploading bag...");
//
//				// Upload the bag.
//				UploadBagTask uploadTask = new UploadBagTask(bag, Global.getBagUploadUri());
//				uploadTask.addProgressListener(new ConsoleProgressListener());
//				ClientResponse resp = uploadTask.call();
//				if (resp.getStatus() == HttpStatus.SC_OK) {
//					System.out.println("Bag uploaded successfully.");
//				} else {
//					System.out.println("Bag could not be uploaded. HTTP Status code: " + resp.getStatus());
//					exitCode = 1;
//				}
//			} else {
//				System.out.println("Verification failed. Bag is invalid.");
//				exitCode = 1;
//				throw new Exception("Bag verification failed.");
//			}
//
//			stopWatch.end();
//
//		} catch (Exception e) {
//			LOGGER.error("Unable to upload bag.", e);
//			System.out.println("Unable to upload bag.");
//		}
	}

	/**
	 * Downloads a file from Data Commons.
	 * 
	 * @param cmdLine
	 *            Parsed command line
	 */
	private void download(CommandLine cmdLine) {
		// TODO Implement
//		String pid = cmdLine.getOptionValue('d');
//		DcBag bag = null;
//
//		System.out.println("Getting bag information...");
//		GetBagSummaryTask getInfoTask = new GetBagSummaryTask(Global.getBagUploadUri(), pid);
//		getInfoTask.addProgressListener(new ConsoleProgressListener());
//		try {
//			ClientResponse resp = getInfoTask.call();
//			System.out.println("Bag information received.");
//			if (resp.getStatus() == HttpStatus.SC_NOT_FOUND) {
//				// Bag for this pid not on server, create an empty local bag.
//				System.out.println("No bag for this collection found on server. Creating blank bag.");
//				bag = new DcBag(pid);
//				if (cmdLine.hasOption('i'))
//					setDataSource(bag);
//				File bagFile = bag.saveAs(Global.getLocalBagStoreAsFile(), pid, Format.FILESYSTEM);
//				File plDir = new File(bagFile, "data/");
//				plDir.mkdirs();
//				System.out.println("Completed");
//			} else if (resp.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
//				throw new Exception("Unauthorized to download this collection or incorrect username and/or password.");
//			} else if (resp.getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//				throw new Exception("Server error");
//			} else {
//				// Download bag.
//				DownloadBagTask dlTask = new DownloadBagTask(Global.getBagUploadUri(), pid,
//						Global.getLocalBagStoreAsFile());
//				dlTask.addProgressListener(new ConsoleProgressListener());
//				System.out.println("Downloading bag...");
//				bag = new DcBag(dlTask.call(), LoadOption.BY_MANIFESTS);
//				if (cmdLine.hasOption('i')) {
//					setDataSource(bag);
//					bag.save();
//				}
//				System.out.println("Bag downloaded.");
//			}
//		} catch (Exception e1) {
//			System.out.println("Unable to download bag from server.");
//			System.out.println(MessageFormat.format("Error: {0}", e1.getMessage()));
//			exitCode = 1;
//		}
	}

}
