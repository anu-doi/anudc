package au.edu.anu.dcclient;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.progresslistener.ConsoleProgressListener;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.BagPropsTxt;
import au.edu.anu.dcclient.collection.CollectionInfo;
import au.edu.anu.dcclient.stopwatch.StopWatch;
import au.edu.anu.dcclient.tasks.CreateCollectionTask;
import au.edu.anu.dcclient.tasks.DownloadBagTask;
import au.edu.anu.dcclient.tasks.GetInfoTask;
import au.edu.anu.dcclient.tasks.SaveBagTask;
import au.edu.anu.dcclient.tasks.UploadBagTask;
import au.edu.anu.dcclient.tasks.VerifyBagTask;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public final class CmdMgr
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdMgr.class);
	private static final Options OPTIONS = new Options();

	static
	{
		Option paramFile = new Option("c", "param-file", true, "Parameter file containing item attributes.");
		Option download = new Option("d", "download", true, "Pid of the item whose bag to download.");
		Option save = new Option("s", "save", true, "Pid of the item whose files on local drive are to be bagged.");
		Option upload = new Option("l", "upload", true, "Pid of the item whose bag to upload.");
		Option username = new Option("u", "username", true, "Username to be used for logging into ANU Data Commons");
		Option password = new Option("p", "password", true, "Password to be used for logging into ANU Data Commons.");
		Option instrument = new Option("i", "instrument", false, "Treat the data as coming from an instrument - existing data cannot be deleted in a bag.");
		Option deleteLocalBag = new Option("x", "delete", false, "Deletes the local copy of the bag after performing the action.");
		Option help = new Option("h", "help", false, "Display help");

		OPTIONS.addOption(paramFile);
		OPTIONS.addOption(download);
		OPTIONS.addOption(save);
		OPTIONS.addOption(upload);
		OPTIONS.addOption(username);
		OPTIONS.addOption(password);
		OPTIONS.addOption(instrument);
		OPTIONS.addOption(deleteLocalBag);
		OPTIONS.addOption(help);
	}

	private int exitCode = 0;
	private TaskSummary summary = new TaskSummary();

	public CmdMgr(String[] args)
	{
		CommandLineParser parser = new PosixParser();
		try
		{
			CommandLine cmdLine = parser.parse(OPTIONS, args);

			if (cmdLine.hasOption('h'))
			{
				// If the command line contains help.
				dispHelp(OPTIONS);
			}
			else if (!cmdLine.hasOption("u"))
			{
				dispHelp(OPTIONS);
			}
			else if (cmdLine.hasOption('d'))
			{
				// Download.
				setCredentials(cmdLine);
				download(cmdLine);
			}
			else if (cmdLine.hasOption('s'))
			{
				// Save Bag.
				setCredentials(cmdLine);
				saveBag(cmdLine);
			}
			else if (cmdLine.hasOption('l'))
			{
				// Upload Bag.
				setCredentials(cmdLine);
				upload(cmdLine);
			}
			else if (cmdLine.hasOption('c'))
			{
				// Parameter file.
				setCredentials(cmdLine);
				processParamFile(cmdLine);
			}
		}
		catch (ParseException e)
		{
			dispHelp(OPTIONS);
		}
	}

	private void setCredentials(CommandLine cmdLine)
	{
		String username = null;
		String password = null;
		
		File credsFile = new File(cmdLine.getOptionValue('u'));
		if (credsFile.exists())
		{
			try
			{
				PropertiesFile credsProps = new PropertiesFile(credsFile);
				username = credsProps.getProperty("username");
				password = credsProps.getProperty("password");
			}
			catch (IOException e)
			{
				System.out.println(format("Unable to read credentials file at {0} . Check permissions and try again.", credsFile.getAbsolutePath()));
			}
		}
		else
		{
			username = cmdLine.getOptionValue('u');
			password = cmdLine.getOptionValue('p');
			if (password == null)
			{
				password = new String(System.console().readPassword("Password: "));
			}
		}
		
		LOGGER.trace("Setting username {} and password **** for all requests to ANU Data Commons", username);
		Authenticator.setDefault(new DcAuthenticator(username, password));
	}

	public int getExitCode()
	{
		return exitCode;
	}

	private void dispHelp(Options options)
	{
		HelpFormatter hf = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out);
		hf.printHelp("DcClient", options, true);
		writer.flush();
		writer = null;
	}

	private void processParamFile(CommandLine cmdLine)
	{
		// Create FedoraObject and then upload files.
		File paramFile = new File(cmdLine.getOptionValue('c'));

		try
		{
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			// Read collection details.
			System.out.println("Reading values from collection file...");
			CollectionInfo ci = new CollectionInfo(paramFile);
			String pid = ci.getPid();
			if (pid == null)
			{
				// Pid doesn't exist in collection file. Create an object.
				CreateCollectionTask createCollTask = new CreateCollectionTask(ci, Global.getCreateUri());
				pid = createCollTask.call();
				System.out.println("Created collection with Pid: " + pid);
				summary.put("Pid", pid + " (new)");
			}
			else
			{
				System.out.println("Pid already exists for this collection: " + pid);
				summary.put("Pid", pid + " (existing)");
			}
			summary.put("Parameter file", paramFile.getAbsolutePath());

			// Create empty bag.
			System.out.println("Initialising bag for pid...");
			DcBag bag = new DcBag(pid);
			if (cmdLine.hasOption('i'))
				setDataSource(bag);
			File bagFile = bag.saveAs(Global.getLocalBagStoreAsFile(), pid, Format.FILESYSTEM);
			File payloadDir = new File(bagFile, "data/");
			if (payloadDir.exists())
				FileUtils.deleteDirectory(payloadDir);
			payloadDir.mkdirs();
			System.out.println("Bag initialised.");

			if (ci.getFilesDir() != null)
			{
				// Copy files.
				long sourceDirSizeInBytes = getDirSizeInBytes(ci.getFilesDir());
				long numFiles = countFilesInDir(ci.getFilesDir());
				System.out.println(MessageFormat.format("Copying {3} files ({2}) from {0} to {1}...", ci.getFilesDir().getAbsolutePath(),
						payloadDir.getAbsolutePath(), FileUtils.byteCountToDisplaySize(sourceDirSizeInBytes), numFiles));
				summary.put("Data", MessageFormat.format("{0} files, {1}.", numFiles, FileUtils.byteCountToDisplaySize(sourceDirSizeInBytes)));
				summary.put("Files Location", ci.getFilesDir().getAbsolutePath());
				FileUtils.copyDirectory(ci.getFilesDir(), payloadDir, true);
				System.out.println("Copying complete.");

				// Save bag.
				System.out.println("Saving bag...");
				bag = new DcBag(Global.getLocalBagStoreAsFile(), pid, LoadOption.BY_FILES);
				if (cmdLine.hasOption('i'))
					setDataSource(bag);
				SaveBagTask saveTask = new SaveBagTask(bag);
				saveTask.call();
				System.out.println("Bag saved. Verifying its integrity...");

				// Verify bag.
				VerifyBagTask verifyTask = new VerifyBagTask(bag);
				SimpleResult result = verifyTask.call();
				if (!result.isSuccess())
					throw new Exception("Verification failed.");

				System.out.println("Verification complete. Bag is valid.");

				// Upload Bag.
				UploadBagTask uploadTask = new UploadBagTask(bag, Global.getBagUploadUri());
				// uploadTask.addProgressListener(new ConsoleProgressListener());
				StopWatch timeEl = new StopWatch();
				timeEl.start();
				System.out.println("Uploading Bag...");
				ClientResponse resp = uploadTask.call();
				timeEl.end();
				try
				{
					if (resp.getClientResponseStatus() != Status.OK)
						throw new Exception("Unable to upload bag. " + resp.getEntity(String.class));
				}
				finally
				{
					bag.close();
				}

				System.out.println("Bag uploaded successfully.");
				System.out.println("Time: " + timeEl.getFriendlyElapsed());

				// Delete local bag if -x in command line.
				if (cmdLine.hasOption('x'))
				{
					System.out.println(MessageFormat.format("Deleting local bag stored at {0}...", bag.getFile().getAbsolutePath()));
					if (FileUtils.deleteQuietly(bagFile))
						System.out.println("Local bag deleted.");
					else
						LOGGER.warn("Unable to delete local bag file.");
				}
			}
			stopWatch.end();
			summary.put("Total Time Taken", stopWatch.getFriendlyElapsed());
			summary.put("Started", new Date(stopWatch.getStartTimeInMs()).toString());
			summary.put("Ended", new Date(stopWatch.getEndTimeInMs()).toString());

			summary.display();
			exitCode = 0;
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			System.out.println("Unable to complete operation.");
			exitCode = 1;
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			System.out.println("Unable to complete operation.");
			exitCode = 1;
		}
	}

	private void upload(CommandLine cmdLine)
	{
		String pid = cmdLine.getOptionValue('l');
		DcBag bag = new DcBag(Global.getLocalBagStoreAsFile(), pid, LoadOption.BY_FILES);
		StopWatch stopWatch = new StopWatch();
		try
		{
			stopWatch.start();
			if (cmdLine.hasOption('i'))
			{
				setDataSource(bag);
				bag.save();
			}
			VerifyBagTask verifyTask = new VerifyBagTask(bag);
			verifyTask.addProgressListener(new ConsoleProgressListener());
			SimpleResult result;
			// Verify current bag.
			result = verifyTask.call();
			if (result.isSuccess())
			{
				System.out.println("Verification complete. Bag is valid. Uploading bag...");

				// Upload the bag.
				UploadBagTask uploadTask = new UploadBagTask(bag, Global.getBagUploadUri());
				uploadTask.addProgressListener(new ConsoleProgressListener());
				ClientResponse resp = uploadTask.call();
				if (resp.getStatus() == HttpStatus.SC_OK)
				{
					System.out.println("Bag uploaded successfully.");
				}
				else
				{
					System.out.println("Bag could not be uploaded. HTTP Status code: " + resp.getStatus());
					exitCode = 1;
				}
			}
			else
			{
				System.out.println("Verification failed. Bag is invalid.");
				exitCode = 1;
				throw new Exception("Bag verification failed.");
			}

			stopWatch.end();
			
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to upload bag.", e);
			System.out.println("Unable to upload bag.");
		}
	}

	private void saveBag(CommandLine cmdLine)
	{
		// Save the bag.
		String pid = cmdLine.getOptionValue('s');
		DcBag bag = new DcBag(Global.getLocalBagStoreAsFile(), pid, LoadOption.BY_FILES);
		System.out.println("Saving bag...");
		if (cmdLine.hasOption('i'))
			setDataSource(bag);
		SaveBagTask saveTask = new SaveBagTask(bag);
		try
		{
			saveTask.call();
			System.out.println("Bag saved. Verifying its integrity...");

			VerifyBagTask verifyTask = new VerifyBagTask(bag);
			SimpleResult result = verifyTask.call();
			if (result.isSuccess())
			{
				System.out.println("Verification complete. Bag is valid.");
			}
			else
			{
				System.out.println("Verification failed. Bag is invalid.");
				exitCode = 1;
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to save bag.", e);
			System.out.println("Unable to save bag.");
			exitCode = 1;
		}
	}

	private void download(CommandLine cmdLine)
	{
		String pid = cmdLine.getOptionValue('d');
		DcBag bag = null;

		System.out.println("Getting bag information...");
		GetInfoTask getInfoTask = new GetInfoTask(Global.getBagUploadUri(), pid);
		getInfoTask.addProgressListener(new ConsoleProgressListener());
		try
		{
			ClientResponse resp = getInfoTask.call();
			System.out.println("Bag information received.");
			if (resp.getStatus() == HttpStatus.SC_NOT_FOUND)
			{
				// Bag for this pid not on server, create an empty local bag.
				System.out.println("No bag for this collection found on server. Creating blank bag.");
				bag = new DcBag(pid);
				if (cmdLine.hasOption('i'))
					setDataSource(bag);
				File bagFile = bag.saveAs(Global.getLocalBagStoreAsFile(), pid, Format.FILESYSTEM);
				File plDir = new File(bagFile, "data/");
				plDir.mkdirs();
				System.out.println("Completed");
			}
			else if (resp.getStatus() == HttpStatus.SC_UNAUTHORIZED)
			{
				throw new Exception("Unauthorized to download this collection or incorrect username and/or password.");
			}
			else if (resp.getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			{
				throw new Exception("Server error");
			}
			else
			{
				// Download bag.
				DownloadBagTask dlTask = new DownloadBagTask(Global.getBagUploadUri(), pid, Global.getLocalBagStoreAsFile());
				dlTask.addProgressListener(new ConsoleProgressListener());
				System.out.println("Downloading bag...");
				bag = new DcBag(dlTask.call(), LoadOption.BY_MANIFESTS);
				if (cmdLine.hasOption('i'))
				{
					setDataSource(bag);
					bag.save();
				}
				System.out.println("Bag downloaded.");
			}
		}
		catch (Exception e1)
		{
			System.out.println("Unable to download bag from server.");
			System.out.println(MessageFormat.format("Error: {0}", e1.getMessage()));
			exitCode = 1;
		}
	}

	private void setDataSource(DcBag dcBag)
	{
		dcBag.setBagProperty(BagPropsTxt.FIELD_DATASOURCE, BagPropsTxt.DataSource.INSTRUMENT.toString());
	}

	static long getDirSizeInBytes(File sourceDir)
	{
		long sizeInBytes = 0L;
		File[] filesInDir = sourceDir.listFiles();

		if (filesInDir != null)
		{
			for (File file : filesInDir)
			{
				if (file.isFile())
					sizeInBytes += file.length();
				else if (file.isDirectory())
					sizeInBytes += getDirSizeInBytes(file);
			}
		}
		else if (sourceDir.isFile())
			sizeInBytes += sourceDir.length();

		return sizeInBytes;
	}

	static long countFilesInDir(File sourceDir)
	{
		long numFilesInDir = 0L;
		File[] filesInDir = sourceDir.listFiles();

		if (filesInDir != null)
		{
			for (File file : filesInDir)
			{
				if (file.isFile())
					numFilesInDir++;
				else if (file.isDirectory())
					numFilesInDir += countFilesInDir(file);
			}
		}
		else if (sourceDir.isFile())
			numFilesInDir = 1;

		return numFilesInDir;
	}
}
