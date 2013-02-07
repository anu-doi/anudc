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

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.MainWindow;
import au.edu.anu.dcclient.ProgressDialog;
import au.edu.anu.dcclient.ThreadPoolManager;
import au.edu.anu.dcclient.explorer.FileExplorer;
import au.edu.anu.dcclient.tasks.SaveBagTask;

/**
 * This class implements an Action Listener that gets invoked when a GUI action for saving a Bag is requested.
 */
public class SavePidBagAction extends AbstractAction implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private final FileExplorer bagExplorer;
	private final JTextComponent txtPid;

	/**
	 * SavePidBagAction
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for SavePidBagAction
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * @param txtPid
	 *            Component containing the pid whose bag will be saved.
	 * @param bagExplorer
	 *            The bag explorer component.
	 */
	public SavePidBagAction(JTextComponent txtPid, FileExplorer bagExplorer)
	{
		this.txtPid = txtPid;
		this.bagExplorer = bagExplorer;
	}

	/**
	 * actionPerformed
	 * 
	 * Australian National University Data Commons
	 * 
	 * Saves the bag after changes made.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		File bagFile = bagExplorer.getBagDir();
		DcBag bag = new DcBag(bagFile, LoadOption.BY_FILES);
		ExecutorService execSvc = ThreadPoolManager.getExecSvc();

		SaveBagTask saveTask = new SaveBagTask(bag, Global.getLocalBagStoreAsFile(), txtPid.getText(), Format.FILESYSTEM);
		saveTask.addProgressListener(new ProgressDialog());
		final Future<File> saveTaskResult = execSvc.submit(saveTask);
		execSvc.submit(new Runnable() {
			@Override
			public void run()
			{
				File savedBagFile;
				try
				{
					savedBagFile = saveTaskResult.get();
					if (savedBagFile != null)
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Bag saved.", "Bag", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to save Bag", "Bag", JOptionPane.ERROR_MESSAGE);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ExecutionException e)
				{
					if (e.getCause() != null)
						JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					else
						JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getMessage(),  "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}
}
