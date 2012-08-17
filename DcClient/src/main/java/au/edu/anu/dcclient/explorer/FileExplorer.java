/*
 * Source: http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html
 */
package au.edu.anu.dcclient.explorer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.ThreadPoolManager;

@SuppressWarnings("serial")
public class FileExplorer extends JPanel
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private JTree tree = null;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private DefaultMutableTreeNode node;
	private PopupMenu pMenu = new PopupMenu();
	private GhostDragImg ghostImg = new GhostDragImg();

	public FileExplorer()
	{
		initTree();
	}

	public FileExplorer(File dir)
	{
		changeDir(dir);
		initTree();
	}

	public void changeDir(File dir)
	{
		root.removeAllChildren();
		node = new DefaultMutableTreeNode(dir);
		if (dir.isDirectory() & dir.listFiles() != null)
			node.add(new DefaultMutableTreeNode(new File("**")));
		root.add(node);
		if (tree != null && tree.getModel() != null)
		{
			((DefaultTreeModel) tree.getModel()).reload();
			tree.expandRow(0);
		}
	}

	private void initTree()
	{
		if (tree != null)
			tree.getParent().remove(tree);
		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.setTransferHandler(new TransferHandler()
		{
			public int getSourceActions(JComponent c)
			{
				return COPY;
			}

			protected Transferable createTransferable(JComponent component)
			{
				List<File> transFiles = new ArrayList<File>();
				transFiles.add((File) node.getUserObject());
				Transferable transferable = new GenericTransferable(transFiles);
				return transferable;
			}

			class GenericTransferable implements Transferable
			{
				private List<File> transFfiles;

				public GenericTransferable(List<File> files)
				{
					this.transFfiles = files;
				}

				public DataFlavor[] getTransferDataFlavors()
				{
					return new DataFlavor[] { DataFlavor.javaFileListFlavor };
				}

				public boolean isDataFlavorSupported(DataFlavor flavor)
				{
					return flavor.equals(DataFlavor.javaFileListFlavor);
				}

				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
				{
					return transFfiles;
				}
			}
		});

		tree.setDropTarget(new DropTarget()
		{
			public void dragExit(DropTargetEvent arg0)
			{
				tree.repaint();
				tree.setSelectionPath(null);
			}

			public void dragOver(DropTargetDragEvent dtde)
			{
				int action = dtde.getDropAction();
				tree.setSelectionPath(tree.getClosestPathForLocation((int) dtde.getLocation().getX(), (int) dtde.getLocation().getY()));
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetDropEvent dtde)
			{
				int action = dtde.getDropAction();
				dtde.acceptDrop(action);
				// Disabling following code as it prevents adding file to the root directory.
				/*
				if (node.isNodeDescendant(((DefaultMutableTreeNode) tree.getLastSelectedPathComponent())))
					return;
				*/

				try
				{
					Transferable data = dtde.getTransferable();
					if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					{
						List<File> files = (List<File>) data.getTransferData(DataFlavor.javaFileListFlavor);
						DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
						for (File file : files)
						{
							// Copy.
							final Future copyTask = ThreadPoolManager.getExecSvc().submit(
									new CopyDialog(file, new File(((MutableTreeNode) tree.getLastSelectedPathComponent()).toString() + File.separator + file.getName()),
											false));
							DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new File(((MutableTreeNode) tree.getLastSelectedPathComponent())
									.toString() + File.separator + file.getName()));
							if (file.isDirectory())
							{
								if (file.listFiles() != null)
									newNode.add(new DefaultMutableTreeNode("**"));
							}
							treeModel.insertNodeInto(newNode, (MutableTreeNode) tree.getLastSelectedPathComponent(), 0);
							LOGGER.debug("Added {} to tree", newNode.toString());
							ThreadPoolManager.getExecSvc().submit(new Runnable()
							{
								@Override
								public void run()
								{
									try
									{
										copyTask.get();
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
									LOGGER.debug("Refreshing tree.");
									refresh();
								}
							});
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		tree.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException
			{
			}

			public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException
			{
				tree.setSelectionPath(evt.getPath());
				node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				loadDir(node);
			}

		});

		tree.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				loadDir(node);
				if (evt.getButton() == 1 & evt.getClickCount() == 2)
					try
					{
						if (node != null)
							if (((File) node.getUserObject()).isFile())
								Desktop.getDesktop().open((File) node.getUserObject());
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(null, "Unable to read file", "Reading Error", JOptionPane.OK_OPTION);
					}
				if (evt.getButton() == 3)
					pMenu.show(tree, evt.getPoint(), (DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
			}
		});

		tree.setDragEnabled(true);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.setShowsRootHandles(true);
		((DefaultTreeModel) tree.getModel()).reload();
		tree.expandRow(0);
		setLayout(new GridLayout(1, 0));
		add(new JScrollPane(tree));
	}

	private void loadDir(final DefaultMutableTreeNode node)
	{
		if (node != null)
		{
			if (node.getChildCount() != 0)
			{
				if (node.getChildAt(0).toString() == "**")
				{
					File parentFile = (File) node.getUserObject();
					tree.setEnabled(false);
					if (parentFile.isDirectory())
					{
						File[] files = FileSystemView.getFileSystemView().getFiles(parentFile, true);
						for (File childFile : files)
						{
							if (childFile.isDirectory())
							{
								DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childFile);
								if (childFile.isDirectory() & childFile.listFiles() != null & childFile.listFiles().length != 0)
									childNode.add(new DefaultMutableTreeNode("**"));
								node.add(childNode);
							}
						}
						for (File childFile : files)
							if (childFile.isFile())
								node.add(new DefaultMutableTreeNode(childFile));
					}
					node.remove(0);
					tree.setEnabled(true);
				}
			}
		}
	}

	public File getBagDir()
	{
		LOGGER.trace("In getBagDir");
		if (this.root.getChildCount() != 1)
			throw new RuntimeException("Tree root has more than one child nodes.");

		File dataDir = (File) ((DefaultMutableTreeNode) this.root.getFirstChild()).getUserObject();
		return dataDir.getParentFile();
	}

	public void refresh()
	{
		changeDir((File) ((DefaultMutableTreeNode) root.getFirstChild()).getUserObject());
	}
}
