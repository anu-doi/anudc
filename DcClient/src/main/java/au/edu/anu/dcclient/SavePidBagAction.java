package au.edu.anu.dcclient;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.duvanslabbert.FileExplorer;
import au.edu.anu.dcclient.tasks.SaveBagTask;

public class SavePidBagAction extends AbstractAction implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private Component parentComponent;
	private FileExplorer bagExplorer;
	private JTextComponent txtPid;

	public SavePidBagAction(Component parentComponent, JTextComponent txtPid, FileExplorer bagExplorer)
	{
		this.parentComponent = parentComponent;
		this.txtPid = txtPid;
		this.bagExplorer = bagExplorer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File bagFile = bagExplorer.getBagDir();
		DcBag bag = new DcBag(bagFile, LoadOption.BY_FILES);
		ExecutorService execSvc = Executors.newSingleThreadExecutor();

		SaveBagTask saveTask = new SaveBagTask(bag, Global.getLocalBagStoreAsFile(), txtPid.getText(), Format.FILESYSTEM);
		saveTask.addProgressListener(new ProgressDialog(parentComponent));
		final Future<File> saveTaskResult = execSvc.submit(saveTask);
		execSvc.submit(new Runnable()
		{
			@Override
			public void run()
			{
				File savedBagFile;
				try
				{
					savedBagFile = saveTaskResult.get();
					if (savedBagFile != null)
						JOptionPane.showMessageDialog(parentComponent, "Bag saved.", "Bag", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(parentComponent, "Unable to save Bag", "Bag", JOptionPane.ERROR_MESSAGE);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ExecutionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		);
		
	}
}
