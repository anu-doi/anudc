package au.edu.anu.dcclient.explorer;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.io.FileUtils;

import au.edu.anu.dcclient.MainWindow;

/**
 * This class represents the menu that pops up for directories and files allowing file operations such as rename, delete, cut, copy and paste.
 * 
 * @see <a
 *      href="http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html">http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html</a>
 */
public class PopupMenu extends JPopupMenu
{
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode lastSelNode, curNode;
	private JMenuItem menuItem;
	private boolean cut;

	/**
	 * Constructor that initialises the popup menu items.
	 */
	public PopupMenu()
	{
		// Rename menu item.
		add(menuItem = new JMenuItem("Rename"));
		menuItem.setIcon(new ImageIcon(this.getClass().getResource("edit.png")));
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				String str = curNode.toString();
				str = str.substring(str.lastIndexOf(File.separatorChar) + 1, str.length());
				String reply = JOptionPane.showInputDialog(MainWindow.getInstance(), "Rename " + str);
				if (reply != "" && reply != null)
				{
					str = curNode.toString();
					str = str.substring(0, str.lastIndexOf(File.separatorChar));
					((File) curNode.getUserObject()).renameTo(new File(str + File.separatorChar + reply));
					curNode.setUserObject(new File(str + File.separatorChar + reply));
				}
			}
		});

		// Delete menu item.
		add(menuItem = new JMenuItem("Delete"));
		menuItem.setIcon(new ImageIcon(this.getClass().getResource("delete.png")));
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if (JOptionPane.showConfirmDialog(MainWindow.getInstance(), "Are you sure you want to delete\r\n" + curNode, "Delete File",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					if (((File) curNode.getUserObject()).isFile())
						((File) curNode.getUserObject()).delete();
					else
						FileUtils.deleteQuietly((File) curNode.getUserObject());
					treeModel = (DefaultTreeModel) tree.getModel();
					treeModel.removeNodeFromParent(curNode);
				}
			}
		});

		// Cut menu item.
		addSeparator();
		add(menuItem = new JMenuItem("Cut"));
		menuItem.setIcon(new ImageIcon(this.getClass().getResource("cut.png")));
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				lastSelNode = curNode;
				cut = true;
			}
		});

		// Copy menu item.
		add(menuItem = new JMenuItem("Copy"));
		menuItem.setIcon(new ImageIcon(this.getClass().getResource("copy.png")));
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				lastSelNode = curNode;
				cut = false;
			}
		});

		// Paste menu item.
		add(menuItem = new JMenuItem("Paste"));
		menuItem.setIcon(new ImageIcon(this.getClass().getResource("paste.png")));
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				String str = lastSelNode.toString();
				str = str.substring(str.lastIndexOf(File.separatorChar) + 1, str.length());
				ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
				threadExecutor.execute(new CopyDialog((File) lastSelNode.getUserObject(), new File(curNode.toString() + File.separatorChar + str), cut));
				threadExecutor.shutdown();
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new File(((MutableTreeNode) tree.getLastSelectedPathComponent()).toString()
						+ File.separatorChar + str));
				if (lastSelNode.getChildCount() != 0)
					newNode.add(new DefaultMutableTreeNode("**"));
				treeModel.insertNodeInto(newNode, (MutableTreeNode) tree.getLastSelectedPathComponent(), 0);
				if (cut)
					treeModel.removeNodeFromParent(lastSelNode);
				tree.repaint();
			}
		});
	}

	/**
	 * Method called when the popup menu is to be displayed.
	 * 
	 * @param comp
	 *            parent component within which the menu will be displayed
	 * @param p
	 *            point at which to display the menu
	 * @param node
	 *            node which was right clicked resulting in the menu to display
	 */
	public void show(JTree comp, Point p, DefaultMutableTreeNode node)
	{
		if (comp != null & p != null & node != null)
		{
			curNode = node;
			tree = comp;
			show(comp, p.x, p.y);
			if (lastSelNode != null & ((File) node.getUserObject()).isDirectory())
			{
				if (!lastSelNode.isNodeDescendant(curNode))
					menuItem.setEnabled(true);
			}
			else
				menuItem.setEnabled(false);
		}
	}
}
