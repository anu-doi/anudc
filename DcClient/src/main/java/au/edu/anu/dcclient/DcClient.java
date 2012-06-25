package au.edu.anu.dcclient;

import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.progresslistener.ConsoleProgressListener;
import gov.loc.repository.bagit.progresslistener.ProgressListenerHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.tasks.DownloadBagTask;
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
	 * DESCRIPTION
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		22/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param args
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
	
	private static int cmdMgr(String[] args)
	{
		int exitCode = 0;
		
		if (args.length < 2)
		{
			System.out.println("Performs an action on a bag.");
			System.out.println();
			System.out.println("DcClient -D|-S|-U collectionId");
			System.out.println(" -D\tDownloads a bag from ANU Data Commons onto the local bag store directory.");
			System.out.println(" -S\tSaves a bag. Any files added/removed/edited are acknowledged and included in their respective manifests.");
			System.out.println(" -U\t Uploads a bag to ANU Data Commons.");
		}
		
		if (args[0].toLowerCase().trim().equals("-d"))
		{
			// Download bag.
			String pid = args[1];
			DownloadBagTask dlTask = new DownloadBagTask(Global.getBagUploadUri(), pid, Global.getLocalBagStoreAsFile());
			dlTask.addProgressListener(new ConsoleProgressListener());
			System.out.println("Downloading bag...");
			try
			{
				dlTask.call();
				System.out.println("Bag downloaded.");
			}
			catch (Exception e)
			{
				System.out.println("Unable to download bag from server.");
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
