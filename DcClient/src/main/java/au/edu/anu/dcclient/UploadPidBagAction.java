package au.edu.anu.dcclient;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.duvanslabbert.FileExplorer;
import au.edu.anu.dcclient.tasks.SaveBagTask;
import au.edu.anu.dcclient.tasks.UploadBagTask;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.ContentDisposition.ContentDispositionBuilder;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

public class UploadPidBagAction extends AbstractAction implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private Component parentComponent;
	private JTextComponent txtPid;
	private FileExplorer bagExplorer;

	public UploadPidBagAction(Component parentComponent, JTextComponent txtPid, FileExplorer bagExplorer)
	{
		this.parentComponent = parentComponent;
		this.txtPid = txtPid;
		this.bagExplorer = bagExplorer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File bagFile = bagExplorer.getBagDir();
		final DcBag dcBag = new DcBag(bagFile, LoadOption.BY_FILES);
		ExecutorService execSvc = Executors.newSingleThreadExecutor();
		SaveBagTask saveTask = new SaveBagTask(dcBag, Global.getLocalBagStoreAsFile(), dcBag.getExternalIdentifier(), Format.FILESYSTEM);
		saveTask.addProgressListener(new ProgressDialog(this.parentComponent));
		final Future<File> saveTaskResult = execSvc.submit(saveTask);

		// Upload ZIP.
		UploadBagTask uploadTask = new UploadBagTask(dcBag, Global.getBagUploadUri());
		uploadTask.addProgressListener(new ProgressDialog(this.parentComponent));
		final Future<ClientResponse> uploadTaskResult = execSvc.submit(uploadTask);
		
		// Check if upload was successful.
		execSvc.submit(new Runnable() {

			@Override
			public void run()
			{
				ClientResponse resp;
				try
				{
					resp = uploadTaskResult.get();
					if (resp.getStatus() == HttpStatus.SC_OK)
						JOptionPane.showMessageDialog(parentComponent, "Bag successfully uploaded.", "Bag Upload", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(parentComponent, "Error uploading bag.", "Bag Upload", JOptionPane.ERROR_MESSAGE);
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
				finally
				{
					try
					{
						saveTaskResult.get().delete();
						dcBag.close();
					}
					catch (Exception e)
					{
						LOGGER.warn("Unable to delete the Zip file created.");
					}
				}
			}
		});
		
	}
}
