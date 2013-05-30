package au.edu.anu.dcclient.bagfilesexplorer;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map.Entry;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;

public class BagFilesTree extends JTree {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagFilesTree.class);
	private static final long serialVersionUID = 1L;

	private BagSummary bagSummary;
	
	public BagFilesTree(BagSummary bagSummary) {
		this.bagSummary = bagSummary;
		configTree();
		populateTree();
	}

	private void configTree() {
		setDragEnabled(true);
		setActionListener();
		setPropertyListener();
	}

	public BagSummary getBagSummary() {
		return bagSummary;
	}

	private void setActionListener() {
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) && (getSelectionCount() > 0)) {
					BagFileMenu popupMenu = new BagFileMenu(BagFilesTree.this);
					popupMenu.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							BagFilesTree.this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
						}
					});
					popupMenu.show(BagFilesTree.this, e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	private void setPropertyListener() {
		// TODO Auto-generated method stub
		
	}

	private void populateTree() {
		DefaultMutableTreeNode rootNode = null;

		for (Entry<String, FileSummary> fsEntry : bagSummary.getFileSummaryMap().entrySet()) {
			rootNode = addNodeToTree(rootNode, fsEntry);
		}

		TreeModel model = new DefaultTreeModel(rootNode);
		this.setCellRenderer(new BagFilesTreeCellRenderer());
		this.setModel(model);
	}

	private DefaultMutableTreeNode addNodeToTree(DefaultMutableTreeNode rootNode, Entry<String, FileSummary> fsEntry) {
		String[] pathParts = fsEntry.getKey().split("/");
		DefaultMutableTreeNode parentNode = null;
		for (int iPathPart = 0; iPathPart < pathParts.length; iPathPart++) {
			if (iPathPart == 0) {
				if (rootNode == null) {
					rootNode = new DefaultMutableTreeNode(pathParts[iPathPart], true);
				}
				parentNode = rootNode;
			} else if (iPathPart == pathParts.length - 1) {
				DefaultMutableTreeNode bagFileNode = new DefaultMutableTreeNode(fsEntry, false);
				parentNode.add(bagFileNode);
			} else {
				int iChild;
				for (iChild = 0; iChild < parentNode.getChildCount(); iChild++) {
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(iChild);
					if (childNode.getUserObject() instanceof String) {
						if (((String) childNode.getUserObject()).equals(pathParts[iPathPart])) {
							parentNode = childNode;
							break;
						}
					} else {
						throw new RuntimeException("Node is not of type String.");
					}
				}
				if (iChild == parentNode.getChildCount()) {
					DefaultMutableTreeNode createdChild = new DefaultMutableTreeNode(pathParts[iPathPart], true);
					parentNode.add(createdChild);
					parentNode = createdChild;
				}
			}
		}
		return rootNode;
	}
}
