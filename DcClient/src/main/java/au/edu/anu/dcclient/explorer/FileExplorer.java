package au.edu.anu.dcclient.explorer;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.ThreadPoolManager;

/**
 * This class displays a JPanel that includes a JTree resembling a Tree View similar to the directory structure in Windows Explorer. It enables the user to perform
 * file and directory operations such as copy, move, delete and rename.
 * 
 * @see <a
 *      href="http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html">http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html</a>
 */
public class FileExplorer extends JPanel
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private JTree tree = null;
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private DefaultMutableTreeNode node;
	private final PopupMenu pMenu = new PopupMenu();

	/**
	 * Instantiates a new file explorer.
	 */
	public FileExplorer()
	{
		initTree();
	}

	/**
	 * Instantiates a new file explorer opening the specified directory as the root.
	 * 
	 * @param dir
	 *            directory as root
	 */
	public FileExplorer(File dir)
	{
		changeDir(dir);
		initTree();
	}

	/**
	 * Changes the directory displayed in the JTree component.
	 * 
	 * @param dir
	 *            new directory
	 */
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

	/**
	 * Initialises the JTree component.
	 */
	private void initTree()
	{
		if (tree != null)
			tree.getParent().remove(tree);
		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.setTransferHandler(new TransferHandler()
		{
			@Override
			public int getSourceActions(JComponent c)
			{
				return COPY;
			}

			@Override
			protected Transferable createTransferable(JComponent component)
			{
				List<File> transFiles = new ArrayList<File>();
				transFiles.add((File) node.getUserObject());
				Transferable transferable = new GenericTransferable(transFiles);
				return transferable;
			}

			class GenericTransferable implements Transferable
			{
				private final List<File> transFfiles;

				public GenericTransferable(List<File> files)
				{
					this.transFfiles = files;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors()
				{
					return new DataFlavor[] { DataFlavor.javaFileListFlavor };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor)
				{
					return flavor.equals(DataFlavor.javaFileListFlavor);
				}

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
				{
					return transFfiles;
				}
			}
		});

		tree.setDropTarget(new DropTarget()
		{
			@Override
			public void dragExit(DropTargetEvent arg0)
			{
				tree.repaint();
				tree.setSelectionPath(null);
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde)
			{
				int action = dtde.getDropAction();
				tree.setSelectionPath(tree.getClosestPathForLocation((int) dtde.getLocation().getX(), (int) dtde.getLocation().getY()));
			}

			@Override
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
			@Override
			public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException
			{
			}

			@Override
			public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException
			{
				tree.setSelectionPath(evt.getPath());
				node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				loadDir(node);
			}

		});

		tree.addMouseListener(new MouseAdapter()
		{
			@Override
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

	/**
	 * Gets the current bag directory.
	 * 
	 * @return the bag directory
	 */
	public File getBagDir()
	{
		LOGGER.trace("In getBagDir");
		if (this.root.getChildCount() != 1)
			throw new RuntimeException("Tree root has more than one child nodes.");

		File dataDir = (File) ((DefaultMutableTreeNode) this.root.getFirstChild()).getUserObject();
		return dataDir.getParentFile();
	}

	/**
	 * Refreshes the current view in JTree component.
	 */
	public void refresh()
	{
		changeDir((File) ((DefaultMutableTreeNode) root.getFirstChild()).getUserObject());
	}
}
