package au.edu.anu.dcclient.actions;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagException;
import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.MainWindow;
import au.edu.anu.dcclient.ProgressDialog;
import au.edu.anu.dcclient.ThreadPoolManager;
import au.edu.anu.dcclient.explorer.FileExplorer;
import au.edu.anu.dcclient.tasks.DownloadBagTask;
import au.edu.anu.dcclient.tasks.GetInfoTask;
import au.edu.anu.dcclient.tasks.VerifyBagTask;

import com.sun.jersey.api.client.ClientResponse;

/**
 * This class implements an Action Listener that gets invoked when a GUI action for getting a Bag is requested. 
 */
public class GetPidBagAction extends AbstractAction implements ActionListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GetPidBagAction.class);
	private static final long serialVersionUID = 1L;

	private final FileExplorer bagExplorer;
	private final JTextComponent txtPid;
	private File localBagFile;

	/**
	 * GetPidBagAction
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for GetPidBagAction
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param txtPid
	 *            The textbox containing the pid to process.
	 * @param bagExplorer
	 *            The bag explorer control.
	 */
	public GetPidBagAction(JTextComponent txtPid, FileExplorer bagExplorer)
	{
		this.txtPid = txtPid;
		this.bagExplorer = bagExplorer;
	}

	/**
	 * actionPerformed
	 * 
	 * Australian National University Data Commons
	 * 
	 * Performs the actions required to get a bag from ANU Data Commons.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		final ProgressDialog progDialog = new ProgressDialog();

		localBagFile = DcBag.getBagFile(Global.getLocalBagStoreAsFile(), txtPid.getText());
		final ExecutorService execSvc = Executors.newSingleThreadExecutor();

		// Check if a local bag already exists.
		if (localBagFile != null && localBagFile.exists())
		{
			// Ask user to redownload or use existing bag on local drive.
			if (JOptionPane.showConfirmDialog(MainWindow.getInstance(), "Pid's bag already exists on your local drive. Redownload?", "Confirm Dialog",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
			{
				// Delete local bag, redownload.
				FileUtils.deleteQuietly(localBagFile);
				localBagFile.mkdirs();
				downloadBag(progDialog);
			}
			else
			{
				// Check if the local bag is valid.
				final DcBag localBag = new DcBag(localBagFile, LoadOption.BY_FILES);
				VerifyBagTask verifyTask = new VerifyBagTask(localBag);
				verifyTask.addProgressListener(progDialog);
				final Future<SimpleResult> verifyTaskResult = execSvc.submit(verifyTask);
				execSvc.submit(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							if (verifyTaskResult.get().isSuccess())
							{
								bagExplorer.changeDir(new File(localBagFile, "data/"));
							}
							else
							{
								// Local bag isn't valid. Redownload.
								if (JOptionPane.showConfirmDialog(MainWindow.getInstance(),
										"Local bag seems to be corrupted. The bag needs to be redownloaded.", "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.ERROR_MESSAGE) == JOptionPane.OK_OPTION)
								{
									// Delete bag on local drive, redownload.
									FileUtils.deleteQuietly(localBagFile);
									localBagFile.mkdirs();
									downloadBag(progDialog);
								}
							}
						}
						catch (InterruptedException e)
						{
							JOptionPane.showMessageDialog(MainWindow.getInstance(), "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
						}
						catch (ExecutionException e)
						{
							JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		}
		else
		{
			// Local bag doesn't exist, check if there's one to download.
			GetInfoTask getInfoTask = new GetInfoTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim());
			getInfoTask.addProgressListener(progDialog);
			final Future<ClientResponse> getInfoTaskResult = execSvc.submit(getInfoTask);
			execSvc.submit(new Runnable()
			{
				@Override
				public void run()
				{
					ClientResponse resp;
					try
					{
						resp = getInfoTaskResult.get();
						if (resp.getStatus() == HttpStatus.SC_NOT_FOUND)
						{
							if (JOptionPane.showConfirmDialog(MainWindow.getInstance(), "Bag doesn't exist in Data Commons. Would you like to create one?",
									"Bag not found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
							{
								if (localBagFile != null)
									FileUtils.deleteQuietly(localBagFile);
								DcBag dcBag = new DcBag(txtPid.getText());
								localBagFile = dcBag.saveAs(Global.getLocalBagStoreAsFile(), txtPid.getText(), Format.FILESYSTEM);
								// localBagFile.mkdirs();
								File dataDir = new File(localBagFile, "data/");
								if (dataDir.mkdir())
									bagExplorer.changeDir(dataDir);
								else
									throw new IOException("Unable to create directory in the local bags directory.");
							}
						}
						else if (resp.getStatus() == HttpStatus.SC_OK)
						{
							downloadBag(progDialog);
						}
					}
					catch (InterruptedException e)
					{
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
					}
					catch (ExecutionException e)
					{
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (DcBagException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
		}
	}

	/**
	 * Downloads a bag with the progress information being sent to the ProgressDialog.
	 * 
	 * @param progDialog
	 *            ProgressDialog to which progress information will be sent
	 */
	private void downloadBag(ProgressDialog progDialog)
	{
		// Download bag.
		ExecutorService execSvc = ThreadPoolManager.getExecSvc();
		DownloadBagTask dlBagTask = new DownloadBagTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim(), Global.getLocalBagStoreAsFile());
		dlBagTask.addProgressListener(progDialog);
		final Future<File> taskResult = execSvc.submit(dlBagTask);
		execSvc.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					File downloadedBagFile = taskResult.get();
					bagExplorer.changeDir(new File(downloadedBagFile, "data/"));
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Bag downloaded successfully.", "Bag Download", JOptionPane.INFORMATION_MESSAGE);
				}
				catch (InterruptedException e)
				{
					LOGGER.warn(e.getMessage(), e);
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
				}
				catch (ExecutionException e)
				{
					LOGGER.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to download bag.\r\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}
