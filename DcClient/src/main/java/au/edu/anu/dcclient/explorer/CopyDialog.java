/*
 * Source: http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html
 */
package au.edu.anu.dcclient.explorer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.*;

import au.edu.anu.dcclient.MainWindow;

public class CopyDialog implements Runnable
{

	private JDialog dlg;
	private File sourceLocation, targetLocation;
	private JProgressBar progressBar;
	private boolean removeSource, stopCopy;

	CopyDialog(File sourceLocation, File targetLocation, boolean removeSource)
	{
		this.sourceLocation = sourceLocation;
		this.targetLocation = targetLocation;
		this.removeSource = removeSource;
	}

	public void run()
	{
		dlg = new JDialog();
		dlg.setForeground(Color.WHITE);
		dlg.setLayout(null);
		dlg.setSize(390, 120);
		dlg.setLocationRelativeTo(MainWindow.getInstance());

		JLabel label = new JLabel(new ImageIcon("loading.gif"));
		label.setBounds(5, 8, 31, 31);
		label.setOpaque(false);
		dlg.add(label);

		label = new JLabel("Copying...");
		label.setBounds(40, 0, 315, 25);
		dlg.add(label);

		progressBar = new JProgressBar(0, (int) (getDirSize(sourceLocation) / 1024));
		progressBar.setBounds(40, 25, 340, 15);
		dlg.add(progressBar);

		JButton btn = new JButton("Cancel");
		btn.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				stopCopy = true;
			}
		});
		btn.setBounds(290, 60, 90, 25);
		dlg.add(btn);

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

	private void moveDirectory(File sourceLocation, File targetLocation, boolean removeSource) throws IOException
	{
		if (sourceLocation.isDirectory())
		{
			String[] children = sourceLocation.list();
			if (!targetLocation.exists())
				targetLocation.mkdir();
			for (int i = 0; i < children.length; i++)
			{
				moveDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]), removeSource);
			}
		}
		else
		{
			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);
			byte[] buf = new byte[16777216];
			int len;
			while ((len = in.read(buf)) > 0 && stopCopy == false)
			{
				out.write(buf, 0, len);
				progressBar.setValue(progressBar.getValue() + (buf.length / 1024));
			}
			if (stopCopy)
				dlg.dispose();
			in.close();
			out.close();
		}
		if (removeSource)
			clearAndDelDir(sourceLocation);
		if (progressBar.getValue() == progressBar.getMaximum())
			dlg.dispose();
	}

	long getDirSize(File dir)
	{
		long size = 0;
		if (dir.isFile())
			size = dir.length();
		else
		{
			File[] subFiles = dir.listFiles();
			for (File file : subFiles)
			{
				if (file.isFile())
					size += file.length();
				else
					size += this.getDirSize(file);
			}
		}
		return size;
	}

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
