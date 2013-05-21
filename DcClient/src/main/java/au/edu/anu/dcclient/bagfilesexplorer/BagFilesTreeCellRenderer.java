package au.edu.anu.dcclient.bagfilesexplorer;

import java.awt.Component;
import java.util.Map.Entry;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import au.edu.anu.dcbag.FileSummary;

public class BagFilesTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		String cellTxt = null;
		if (node.getUserObject() instanceof String) {
			cellTxt = (String) node.getUserObject();
		} else if (node.getUserObject() instanceof Entry) {
			Entry<String, FileSummary> fsEntry = (Entry<String, FileSummary>) node.getUserObject();
			cellTxt = fsEntry.getValue().getFilename(); 
		}
		
		Component renderedCell = super.getTreeCellRendererComponent(tree, cellTxt, sel, expanded, leaf, row, hasFocus);
		return renderedCell;
	}
}
