package au.edu.anu.dcclient;

import java.awt.EventQueue;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry class for the DcClient application.
 */
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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
		}
		catch (Throwable e)
		{
		}
		EventQueue.invokeLater(new Runnable()
		{
			@Override
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
