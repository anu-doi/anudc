package au.edu.anu.dcclient;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.progresslistener.ConsoleProgressListener;
import gov.loc.repository.bagit.progresslistener.ProgressListenerHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.awt.EventQueue;
import java.io.File;
import java.net.Authenticator;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.tasks.DownloadBagTask;
import au.edu.anu.dcclient.tasks.GetInfoTask;
import au.edu.anu.dcclient.tasks.SaveBagTask;
import au.edu.anu.dcclient.tasks.UploadBagTask;
import au.edu.anu.dcclient.tasks.VerifyBagTask;

public class DcClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	/**
	 * main
	 * 
	 * Australian National University Data Commons
	 * 
	 * The main entry point for this desktop application.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		22/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args)
	{
		// If no command line arguments specified, start GUI.
		if (args.length == 0)
		{
			startGui();
		}
		else
		{
			int exitCode = cmdMgr(args);
			System.exit(exitCode);
		}
	}

	/**
	 * startGui
	 * 
	 * Australian National University Data Commons
	 * 
	 * Starts the GUI interface of the desktop client.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 */
	private static void startGui()
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
		}
		catch (Throwable e)
		{
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MainWindow window = new MainWindow();
					window.setVisible(true);
				}
				catch (Exception e)
				{
					LOGGER.error("Unable to start " + MainWindow.class.getName(), e);
					System.exit(1);
				}
			}
		});
	}

	/**
	 * cmdMgr
	 * 
	 * Australian National University Data Commons
	 * 
	 * Parses the command line arguments specified to perform tasks.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param args
	 *            Command line arguments passed from the main method.
	 * @return Returns the exit code. 0 for a normal exit, >0 otherwise.
	 */
	private static int cmdMgr(String[] args)
	{
		int exitCode = 0;

		if (args.length < 4)
		{
			System.out.println("Performs an action on a bag.");
			System.out.println();
			System.out.println("DcClient -D|-S|-U collectionId -N username -P password");
			System.out.println(" -D\tDownloads a bag from ANU Data Commons onto the local bag store directory.");
			System.out.println(" -S\tSaves a bag. Any files added/removed/edited are acknowledged and included in their respective manifests.");
			System.out.println(" -U\t Uploads a bag to ANU Data Commons.");
		}

		// Extract credentials from command line.
		String username = "";
		String password = "";
		for (int i = 0; i < args.length; i++)
		{
			// Username
			if (args[i].equalsIgnoreCase("-n"))
				username = args[i + 1];
		}

		for (int i = 0; i < args.length; i++)
		{
			// Password
			if (args[i].equalsIgnoreCase("-p"))
				password = args[i + 1];
		}

		if (!username.equals("") && !password.equals(""))
			Authenticator.setDefault(new DcAuthenticator(username, password));

		if (args[0].toLowerCase().trim().equals("-d"))
		{
			String pid = args[1];

			System.out.println("Getting bag information...");
			GetInfoTask getInfoTask = new GetInfoTask(UriBuilder.fromUri(Global.getBagUploadUri()).path(pid).build());
			getInfoTask.addProgressListener(new ConsoleProgressListener());
			try
			{
				ClientResponse resp = getInfoTask.call();
				System.out.println("Bag information received.");
				if (resp.getStatus() == HttpStatus.SC_NOT_FOUND)
				{
					// Bag for this pid not on server, create an empty local bag.
					System.out.println("No bag for this collection found on server. Creating blank bag.");
					DcBag blankBag = new DcBag(pid);
					File bagFile = blankBag.saveAs(Global.getLocalBagStoreAsFile(), pid, Format.FILESYSTEM);
					File plDir = new File(bagFile, "data/");
					plDir.mkdirs();
					System.out.println("Completed");
				}
				else if (resp.getStatus() == HttpStatus.SC_UNAUTHORIZED)
				{
					throw new Exception("Unauthorized to download this collection or incorrect username and/or password.");
				}
				else if (resp.getStatus() == 500)
				{
					throw new Exception("Server error");
				}
				else
				{
					// Download bag.
					DownloadBagTask dlTask = new DownloadBagTask(Global.getBagUploadUri(), pid, Global.getLocalBagStoreAsFile());
					dlTask.addProgressListener(new ConsoleProgressListener());
					System.out.println("Downloading bag...");
					dlTask.call();
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
		else if (args[0].toLowerCase().trim().equals("-s"))
		{
			// Save the bag.
			String pid = args[1];
			DcBag dcBag = new DcBag(Global.getLocalBagStoreAsFile(), pid, LoadOption.BY_FILES);
			System.out.println("Saving bag...");
			SaveBagTask saveTask = new SaveBagTask(dcBag);
			try
			{
				saveTask.call();
				System.out.println("Bag saved. Verifying its integrity...");

				VerifyBagTask verifyTask = new VerifyBagTask(dcBag);
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
			}
		}
		else if (args[0].toLowerCase().trim().equals(("-u")))
		{
			String pid = args[1];
			DcBag dcBag = new DcBag(Global.getLocalBagStoreAsFile(), pid, LoadOption.BY_FILES);
			VerifyBagTask verifyTask = new VerifyBagTask(dcBag);
			verifyTask.addProgressListener(new ConsoleProgressListener());
			SimpleResult result;
			try
			{
				// Verify current bag.
				result = verifyTask.call();
				if (result.isSuccess())
				{
					System.out.println("Verification complete. Bag is valid. Uploading bag...");

					// Upload the bag.
					UploadBagTask uploadTask = new UploadBagTask(dcBag, Global.getBagUploadUri());
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

			}
			catch (Exception e)
			{
				LOGGER.error("Unable to upload bag.", e);
				System.out.println("Unable to upload bag.");
			}
		}

		return exitCode;
	}
}
