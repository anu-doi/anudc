package au.edu.anu.dcclient.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressDialogListener extends JDialog implements PropertyChangeListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgressDialogListener.class);
	private static final long serialVersionUID = 1L;
	
	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel lblProgress = new JLabel("Progress");
	public ProgressDialogListener() {
		initGui();
	}
	private void initGui() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][]"));
		
		getContentPane().add(lblProgress, "cell 0 0,alignx center");
		
		getContentPane().add(progressBar, "cell 0 1,growx");
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("state")) {
			if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressBar.setString("");
						progressBar.setIndeterminate(true);
					};
				});
			} else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
				if (progressBar.isIndeterminate()) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (progressBar.isIndeterminate()) {
								progressBar.setIndeterminate(false);
							}
						}
					});
				}
			}
		} else if (evt.getPropertyName().equals("progress")) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int percentComplete = (Integer) evt.getNewValue();
					if (progressBar.isIndeterminate()) {
						progressBar.setIndeterminate(false);
					}
					LOGGER.debug("Percent Complete: {}", percentComplete);
					progressBar.setValue(percentComplete);
					progressBar.setString(percentComplete + "%");
				};
			});
		}
	}

}
