package au.edu.anu.dcclient;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;
import gov.loc.repository.bagit.progresslistener.ProgressListenerHelper;
import gov.loc.repository.bagit.transfer.BagFetcher;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FetchProtocol;
import gov.loc.repository.bagit.transfer.fetch.HttpFetchProtocol;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.text.JTextComponent;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.duvanslabbert.FileExplorer;
import au.edu.anu.dcclient.tasks.DownloadBagTask;
import au.edu.anu.dcclient.tasks.GetInfoTask;
import au.edu.anu.dcclient.tasks.VerifyBagTask;

public class GetPidBagAction extends AbstractAction implements ActionListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GetPidBagAction.class);
	private static final long serialVersionUID = 1L;

	private Component parentComponent;
	private FileExplorer bagExplorer;
	private JTextComponent txtPid;
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
	 * @param parentComponent
	 *            Parent component used when displaying dialog boxes etc.
	 * @param txtPid
	 *            The textbox containing the pid to process.
	 * @param bagExplorer
	 *            The bag explorer control.
	 */
	public GetPidBagAction(Component parentComponent, JTextComponent txtPid, FileExplorer bagExplorer)
	{
		this.parentComponent = parentComponent;
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
		localBagFile = DcBag.getBagFile(Global.getLocalBagStoreAsFile(), txtPid.getText());
		final ExecutorService execSvc = Executors.newSingleThreadExecutor();
		URI pidBagUri = UriBuilder.fromUri(Global.getBagUploadUri()).path(txtPid.getText().toLowerCase().trim()).build();

		// Check if a local bag already exists.
		if (localBagFile != null && localBagFile.exists())
		{
			// Ask user to redownload or use existing bag on local drive.
			if (JOptionPane.showConfirmDialog(parentComponent, "Pid's bag already exists on your local drive. Redownload?", "Confirm Dialog",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
			{
				// Delete local bag, redownload.
				DcBag.deleteDir(localBagFile);
				localBagFile.mkdirs();
				DownloadBagTask dlBagTask = new DownloadBagTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim(),
						Global.getLocalBagStoreAsFile());
				dlBagTask.addProgressListener(new ProgressDialog(parentComponent));
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
						}
						catch (InterruptedException e)
						{
							JOptionPane.showMessageDialog(parentComponent, "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
						}
						catch (ExecutionException e)
						{
							JOptionPane.showMessageDialog(parentComponent, "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
			else
			{
				// Check if the local bag is valid.
				final DcBag localBag = new DcBag(localBagFile, LoadOption.BY_FILES);
				VerifyBagTask verifyTask = new VerifyBagTask(localBag);
				verifyTask.addProgressListener(new ProgressDialog(parentComponent));
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
								if (JOptionPane.showConfirmDialog(parentComponent, "Local bag seems to be corrupted. The bag needs to be redownloaded.",
										"Confirm Dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.OK_OPTION)
								{
									// Delete local drive, redownload.
									DcBag.deleteDir(localBagFile);
									localBagFile.mkdirs();
									DownloadBagTask dlBagTask = new DownloadBagTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim(), Global
											.getLocalBagStoreAsFile());
									dlBagTask.addProgressListener(new ProgressDialog(parentComponent));
									final Future<File> dlTaskResult = execSvc.submit(dlBagTask);
									execSvc.submit(new Runnable()
									{
										@Override
										public void run()
										{
											try
											{
												File downloadedBagFile = dlTaskResult.get();
												bagExplorer.changeDir(new File(downloadedBagFile, "data/"));
											}
											catch (InterruptedException e)
											{
												JOptionPane.showMessageDialog(parentComponent, "The bag download was cancelled", "Cancelled",
														JOptionPane.WARNING_MESSAGE);
												LOGGER.error("Bag download cancelled.", e);
											}
											catch (ExecutionException e)
											{
												JOptionPane.showMessageDialog(parentComponent, "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
												LOGGER.error("Bag download execution failed.", e);
											}
										}
									});
								}

							}
						}
						catch (InterruptedException e)
						{
							JOptionPane.showMessageDialog(parentComponent, "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
						}
						catch (ExecutionException e)
						{
							JOptionPane.showMessageDialog(parentComponent, "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		}
		else
		{
			// Local bag doesn't exist, check if there's one to download.
			GetInfoTask getInfoTask = new GetInfoTask(pidBagUri);
			getInfoTask.addProgressListener(new ProgressDialog(parentComponent));
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
							if (JOptionPane.showConfirmDialog(parentComponent, "Bag doesn't exist in Data Commons. Would you like to create one?",
									"Bag not found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
							{
								if (localBagFile != null)
									DcBag.deleteDir(localBagFile);
								DcBag dcBag = new DcBag(txtPid.getText());
								localBagFile = dcBag.saveAs(Global.getLocalBagStoreAsFile(), txtPid.getText(), Format.FILESYSTEM);
								// localBagFile.mkdirs();
								File dataDir = new File(localBagFile, "data/");
								if (dataDir.mkdir())
									bagExplorer.changeDir(dataDir);
								else
									throw new Exception("Unable to create directory in the local bags directory.");
							}
						}
					}
					catch (InterruptedException e)
					{
						JOptionPane.showMessageDialog(parentComponent, "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
					}
					catch (ExecutionException e)
					{
						JOptionPane.showMessageDialog(parentComponent, "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(parentComponent, "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			});
		}
	}
}
