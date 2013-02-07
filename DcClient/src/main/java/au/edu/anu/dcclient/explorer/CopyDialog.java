package au.edu.anu.dcclient.explorer;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.MainWindow;

/**
 * This class implements Runnable interface to copy a file or directory from one location to another.
 * 
 * @see <a
 *      href="http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html">http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html</a>
 */
public class CopyDialog implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyDialog.class);
	
	private JDialog dlg;
	private final File sourceLocation, targetLocation;
	private JProgressBar progressBar;
	private final boolean removeSource;

	private boolean stopCopy;

	/**
	 * Constructor that accepts a source and destination File object and a boolean flag to specify if the source File should be deleted after the copying to target.
	 * 
	 * @param sourceLocation
	 *            the source location
	 * @param targetLocation
	 *            the target location
	 * @param removeSource
	 *            true if source should be removed after a successful copy, false otherwise.
	 */
	CopyDialog(File sourceLocation, File targetLocation, boolean removeSource)
	{
		this.sourceLocation = sourceLocation;
		this.targetLocation = targetLocation;
		this.removeSource = removeSource;
	}

	/**
	 * Performs the copying process.
	 */
	@Override
	public void run()
	{
		dlg = new JDialog();
		dlg.setForeground(Color.WHITE);
		dlg.getContentPane().setLayout(null);
		dlg.setSize(390, 120);
		dlg.setLocationRelativeTo(MainWindow.getInstance());

		JLabel label = new JLabel(new ImageIcon("loading.gif"));
		label.setBounds(5, 8, 31, 31);
		label.setOpaque(false);
		dlg.getContentPane().add(label);

		label = new JLabel("Copying...");
		label.setBounds(40, 0, 315, 25);
		dlg.getContentPane().add(label);

		progressBar = new JProgressBar(0, (int) (getDirSize(sourceLocation)));
		progressBar.setBounds(40, 25, 340, 15);
		dlg.getContentPane().add(progressBar);

		JButton btn = new JButton("Cancel");
		btn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				stopCopy = true;
			}
		});
		btn.setBounds(290, 60, 90, 25);
		dlg.getContentPane().add(btn);

		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.setResizable(false);
		dlg.setVisible(true);

		try
		{
			moveDirectory(sourceLocation, targetLocation, removeSource);
		}
		catch (IOException exc)
		{
			exc.printStackTrace();
		}
	}

	/**
	 * Copies a source File to target File object, and optionally removes the source File.
	 * 
	 * @param sourceFile
	 *            source File
	 * @param targetFile
	 *            target File
	 * @param removeSource
	 *            true if the source file should be deleted after copying to target
	 * @throws IOException
	 *             when unable to perform the copy operation
	 */
	private void moveDirectory(File sourceFile, File targetFile, boolean removeSource) throws IOException
	{
		if (sourceFile.isDirectory())
		{
			String[] children = sourceFile.list();
			if (!targetFile.exists())
				targetFile.mkdir();
			for (int i = 0; i < children.length; i++)
			{
				moveDirectory(new File(sourceFile, children[i]), new File(targetFile, children[i]), removeSource);
			}
		}
		else
		{
//			InputStream in = new FileInputStream(sourceLocation);
//			OutputStream out = new FileOutputStream(targetLocation);
//			byte[] buf = new byte[16777216];
//			int len;
//			while ((len = in.read(buf)) > 0 && stopCopy == false)
//			{
//				out.write(buf, 0, len);
//				progressBar.setValue(progressBar.getValue() + (buf.length / 1024));
//			}
//			if (stopCopy)
//				dlg.dispose();
//			in.close();
//			out.close();
			
			FileInputStream fis = null;
			FileOutputStream fos = null;
			FileChannel sourceChannel = null;
			FileChannel targetChannel = null;
			try
			{
				LOGGER.debug("Copying {} to {}...", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());
				fis = new FileInputStream(sourceFile);
				fos = new FileOutputStream(targetFile);
				sourceChannel = fis.getChannel();
				targetChannel = fos.getChannel();
				targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
				LOGGER.debug("Created {}.", targetFile.getAbsolutePath());
				progressBar.setValue(progressBar.getValue() + 1);
			}
			finally
			{
				IOUtils.closeQuietly(targetChannel);
				IOUtils.closeQuietly(fos);
				IOUtils.closeQuietly(sourceChannel);
				IOUtils.closeQuietly(fis);
			}
		}
		if (removeSource)
			clearAndDelDir(sourceFile);
		if (progressBar.getValue() == progressBar.getMaximum())
			dlg.dispose();
	}

	/**
	 * Gets the number of files in a directory and all its subdirectories. If the specified File object is a file, 1 is returned.
	 * 
	 * @param dir
	 *            directory containing the files to be counted.
	 * @return number of files
	 */
	long getDirSize(File dir)
	{
		long size = 0;
		if (dir.isFile())
			size = 1;
		else
		{
			File[] subFiles = dir.listFiles();
			for (File file : subFiles)
			{
				if (file.isFile())
					size += 1;
				else
					size += this.getDirSize(file);
			}
		}
		return size;
	}

	/**
	 * Deletes all files within a specified directory and then removes the directory itself.
	 * 
	 * @param dir
	 *            Directory to delete
	 */
	public static void clearAndDelDir(File dir)
	{
		if (dir.isDirectory())
		{
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					clearAndDelDir(files[i]);
					files[i].delete();
				}
				else
					files[i].delete();
			}
			dir.delete();
		}
	}
}
