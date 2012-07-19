/*
 * Source: http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html
 */
package au.edu.anu.dcclient.duvanslabbert;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
class FileTreeCellRenderer extends DefaultTreeCellRenderer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	
	private JTree tree;
	private boolean isDropCell;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		/*
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.toString() != "**")
		{
			File file = (File) node.getUserObject();
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(fileSystemView.getSystemDisplayName(file));

			if (sel)
			{
				label.setBackground(backgroundSelectionColor);
			}
			else
			{
				label.setBackground(backgroundNonSelectionColor);
			}
		}
		return label;
		*/
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		File file = (File) node.getUserObject();

		this.tree = tree;
		this.hasFocus = hasFocus;

		// Filename.
		String nodeText = tree.convertValueToText(fileSystemView.getSystemDisplayName(file), sel, expanded, leaf, row, hasFocus);
		this.setText(nodeText);

		Color fg = null;
		this.isDropCell = false;

		// Foreground colour of this node.
		JTree.DropLocation dropLocation = tree.getDropLocation();
		if (dropLocation != null && dropLocation.getChildIndex() == -1 && tree.getRowForPath(dropLocation.getPath()) == row)
		{
			fg = this.getTextSelectionColor();
			this.isDropCell = true;
		}
		else if (sel)
			fg = this.getTextSelectionColor();
		else
			fg = this.getTextNonSelectionColor();
		setForeground(fg);

		// File icon
		Icon icon = fileSystemView.getSystemIcon(file);
		
		// TODO Fix icon issue.
		
		/*
		if (leaf)
			icon = getLeafIcon();
		else if (expanded)
			icon = getOpenIcon();
		else
			icon = getClosedIcon();
		*/
		
		if (!tree.isEnabled())
		{
			setEnabled(false);
			LookAndFeel laf = UIManager.getLookAndFeel();
			Icon disabledIcon = laf.getDisabledIcon(tree, icon);
			if (disabledIcon != null)
				icon = disabledIcon;
			setDisabledIcon(icon);
		}
		else
		{
			setEnabled(true);
			setIcon(icon);
		}
		setComponentOrientation(tree.getComponentOrientation());
		this.selected = sel;
		return this;
	}
}
