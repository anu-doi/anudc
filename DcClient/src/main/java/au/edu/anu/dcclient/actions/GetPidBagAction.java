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

package au.edu.anu.dcclient.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.explorer.FileExplorer;
import au.edu.anu.dcclient.tasks.GetBagSummaryTask;

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
		GetBagSummaryTask t = new GetBagSummaryTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim());
		t.execute();
		
		return;
		
//		final ProgressDialog progDialog = new ProgressDialog();
//
//		localBagFile = DcBag.getBagFile(Global.getLocalBagStoreAsFile(), txtPid.getText());
//		final ExecutorService execSvc = Executors.newSingleThreadExecutor();
//
//		// Check if a local bag already exists.
//		if (localBagFile != null && localBagFile.exists())
//		{
//			// Ask user to redownload or use existing bag on local drive.
//			if (JOptionPane.showConfirmDialog(MainWindow.getInstance(), "Pid's bag already exists on your local drive. Redownload?", "Confirm Dialog",
//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
//			{
//				// Delete local bag, redownload.
//				FileUtils.deleteQuietly(localBagFile);
//				localBagFile.mkdirs();
//				downloadBag(progDialog);
//			}
//			else
//			{
//				// Check if the local bag is valid.
//				final DcBag localBag = new DcBag(localBagFile, LoadOption.BY_FILES);
//				VerifyBagTask verifyTask = new VerifyBagTask(localBag);
//				verifyTask.addProgressListener(progDialog);
//				final Future<SimpleResult> verifyTaskResult = execSvc.submit(verifyTask);
//				execSvc.submit(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						try
//						{
//							if (verifyTaskResult.get().isSuccess())
//							{
//								bagExplorer.changeDir(new File(localBagFile, "data/"));
//							}
//							else
//							{
//								// Local bag isn't valid. Redownload.
//								if (JOptionPane.showConfirmDialog(MainWindow.getInstance(),
//										"Local bag seems to be corrupted. The bag needs to be redownloaded.", "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION,
//										JOptionPane.ERROR_MESSAGE) == JOptionPane.OK_OPTION)
//								{
//									// Delete bag on local drive, redownload.
//									FileUtils.deleteQuietly(localBagFile);
//									localBagFile.mkdirs();
//									downloadBag(progDialog);
//								}
//							}
//						}
//						catch (InterruptedException e)
//						{
//							JOptionPane.showMessageDialog(MainWindow.getInstance(), "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
//						}
//						catch (ExecutionException e)
//						{
//							JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
//						}
//					}
//				});
//			}
//		}
//		else
//		{
//			// Local bag doesn't exist, check if there's one to download.
//			GetInfoTask getInfoTask = new GetInfoTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim());
//			getInfoTask.addProgressListener(progDialog);
//			final Future<ClientResponse> getInfoTaskResult = execSvc.submit(getInfoTask);
//			execSvc.submit(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					ClientResponse resp;
//					try
//					{
//						resp = getInfoTaskResult.get();
//						if (resp.getStatus() == HttpStatus.SC_NOT_FOUND)
//						{
//							if (JOptionPane.showConfirmDialog(MainWindow.getInstance(), "Bag doesn't exist in Data Commons. Would you like to create one?",
//									"Bag not found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
//							{
//								if (localBagFile != null)
//									FileUtils.deleteQuietly(localBagFile);
//								DcBag dcBag = new DcBag(txtPid.getText());
//								localBagFile = dcBag.saveAs(Global.getLocalBagStoreAsFile(), txtPid.getText(), Format.FILESYSTEM);
//								// localBagFile.mkdirs();
//								File dataDir = new File(localBagFile, "data/");
//								if (dataDir.mkdir())
//									bagExplorer.changeDir(dataDir);
//								else
//									throw new IOException("Unable to create directory in the local bags directory.");
//							}
//						}
//						else if (resp.getStatus() == HttpStatus.SC_OK)
//						{
//							downloadBag(progDialog);
//						}
//					}
//					catch (InterruptedException e)
//					{
//						JOptionPane.showMessageDialog(MainWindow.getInstance(), "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
//					}
//					catch (ExecutionException e)
//					{
//						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to download bag", "Error", JOptionPane.ERROR_MESSAGE);
//					}
//					catch (IOException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					catch (DcBagException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//			});
//		}
//	}
//
//	/**
//	 * Downloads a bag with the progress information being sent to the ProgressDialog.
//	 * 
//	 * @param progDialog
//	 *            ProgressDialog to which progress information will be sent
//	 */
//	private void downloadBag(ProgressDialog progDialog)
//	{
//		// Download bag.
//		ExecutorService execSvc = ThreadPoolManager.getExecSvc();
//		DownloadBagTask dlBagTask = new DownloadBagTask(Global.getBagUploadUri(), txtPid.getText().toLowerCase().trim(), Global.getLocalBagStoreAsFile());
//		dlBagTask.addProgressListener(progDialog);
//		final Future<File> taskResult = execSvc.submit(dlBagTask);
//		execSvc.submit(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				try
//				{
//					File downloadedBagFile = taskResult.get();
//					bagExplorer.changeDir(new File(downloadedBagFile, "data/"));
//					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Bag downloaded successfully.", "Bag Download", JOptionPane.INFORMATION_MESSAGE);
//				}
//				catch (InterruptedException e)
//				{
//					LOGGER.warn(e.getMessage(), e);
//					JOptionPane.showMessageDialog(MainWindow.getInstance(), "The bag download was cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
//				}
//				catch (ExecutionException e)
//				{
//					LOGGER.error(e.getMessage(), e);
//					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to download bag.\r\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
//				}
//			}
//		});
	}
}
