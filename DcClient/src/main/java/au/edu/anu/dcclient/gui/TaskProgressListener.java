package au.edu.anu.dcclient.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskProgressListener implements PropertyChangeListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskProgressListener.class);
	
	private final JProgressBar pb;

	public TaskProgressListener(JProgressBar pb) {
		this.pb = pb;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("state")) {
			if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pb.setString("");
						pb.setIndeterminate(true);
					};
				});
			} else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
				if (pb.isIndeterminate()) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (pb.isIndeterminate()) {
								pb.setIndeterminate(false);
							}
						}
					});
				}
			}
		} else if (evt.getPropertyName().equals("progress")) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int percentComplete = (Integer) evt.getNewValue();
					if (pb.isIndeterminate()) {
						pb.setIndeterminate(false);
					}
					LOGGER.debug("Percent Complete: {}", percentComplete);
					pb.setValue(percentComplete);
					pb.setString(percentComplete + "%");
				};
			});
		}
	}
}
