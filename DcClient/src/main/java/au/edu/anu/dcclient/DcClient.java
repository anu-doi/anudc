package au.edu.anu.dcclient;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.progresslistener.ConsoleProgressListener;
import gov.loc.repository.bagit.progresslistener.ProgressListenerHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagException;
import au.edu.anu.dcbag.DcBagProps;
import au.edu.anu.dcclient.collection.CollectionInfo;
import au.edu.anu.dcclient.stopwatch.StopWatch;
import au.edu.anu.dcclient.tasks.CreateCollectionTask;
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
//			int exitCode = cmdMgr(args);
//			System.exit(exitCode);
			CmdMgr cmdMgr = new CmdMgr(args);
			System.exit(cmdMgr.getExitCode());
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
}
