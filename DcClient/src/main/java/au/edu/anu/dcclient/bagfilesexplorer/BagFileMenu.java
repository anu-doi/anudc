package au.edu.anu.dcclient.bagfilesexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.dcclient.gui.MainWindow;
import au.edu.anu.dcclient.tasks.DeleteFilesTask;
import au.edu.anu.dcclient.tasks.DownloadFilesTask;
import au.edu.anu.dcclient.tasks.LocalFilesMoverTask;

public class BagFileMenu extends JPopupMenu {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagFileMenu.class);
	private static final long serialVersionUID = 1L;

	private final BagFilesTree parent;
	private String pid;
	
	private final JMenuItem mntmDelete = new JMenuItem("Delete");
	private final JMenuItem mntmSaveAs = new JMenuItem("Save As...");
	private final JSeparator separator = new JSeparator();

	public BagFileMenu(BagFilesTree parent) {
		this.parent = parent;
		this.pid = parent.getBagSummary().getPid();
		initGui();
	}

	private void initGui() {
		
		add(mntmSaveAs);
		
		add(separator);
		add(mntmDelete);

		TreePath[] paths = parent.getSelectionPaths();
		mntmSaveAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelectedItems();
			}
			
		});
		mntmDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedItems();
			}
			
		});
		
		checkRootSelected(paths);
	}

	private void checkRootSelected(TreePath[] paths) {
		for (int i = 0; i < paths.length; i++) {
			if (parent.getModel().getRoot() == paths[i].getLastPathComponent()) {
				mntmDelete.setEnabled(false);
				break;
			}
		}
	}
	
	private void saveSelectedItems() {
		List<String> filepaths = getSelectedFilepaths();
		DownloadFilesTask dlTask = new DownloadFilesTask(pid, filepaths);
		dlTask.addPropertyChangeListener(new PropertyRethrowerListener());
		dlTask.execute();
		File saveDir = getDirToSave();
		if (saveDir != null) {
			try {
				Map<String, File> downloadResult = dlTask.get();
				Map<File, File> moveList = new HashMap<File, File>(downloadResult.size());
				for (Entry<String, File> entry : downloadResult.entrySet()) {
					moveList.put(entry.getValue(), new File(saveDir, entry.getKey().substring(entry.getKey().lastIndexOf('/') + 1)));
				}
				
				LocalFilesMoverTask moverTask = new LocalFilesMoverTask(moveList);
				moverTask.addPropertyChangeListener(new PropertyRethrowerListener());
				moverTask.execute();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private File getDirToSave() {
		File dir = null;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retVal = fc.showSaveDialog(MainWindow.getInstance());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			dir = fc.getSelectedFile();
		}
		return dir;
	}

	private void deleteSelectedItems() {
		List<String> filepaths = getSelectedFilepaths();
		DeleteFilesTask delTask = new DeleteFilesTask(pid, filepaths);
		delTask.addPropertyChangeListener(new PropertyRethrowerListener());
		delTask.execute();
	}
	
	private List<String> getSelectedFilepaths() {
		TreePath[] paths = parent.getSelectionPaths();
		List<String> filepaths = new ArrayList<String>();
		DefaultMutableTreeNode node;
		for (int i = 0; i < paths.length; i++) {
			node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
			Entry<String, FileSummary> entry = (Entry<String, FileSummary>) node.getUserObject();
			filepaths.add(entry.getKey());
		}
		
		return filepaths;
	}
	
	private class PropertyRethrowerListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			BagFileMenu.this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
		
	}
}
