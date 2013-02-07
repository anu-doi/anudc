package au.edu.anu.dcclient;

import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.progresslistener.ProgressListenerHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the JDialog class to display a dialog box with a progress bar indicating the details of the task being performed.
 */
public class ProgressDialog extends JDialog implements ProgressListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private final JPanel contentPanel = new JPanel();

	private final JTextPane logTextPane;
	private final JPanel buttonPane;
	private final JProgressBar progressBar;

	/**
	 * ProgressDialog
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for ProgressDialog
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	public ProgressDialog()
	{
		setBounds(100, 100, 401, 213);
		this.setLocationRelativeTo(MainWindow.getInstance());
		// this.setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.contentPanel.setLayout(new BorderLayout(0, 0));
		logTextPane = new JTextPane();
		((DefaultCaret) logTextPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // To move the caret to end everytime text is added.
		this.logTextPane.setText("Initialising...");
		this.contentPanel.add(logTextPane, BorderLayout.CENTER);
		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		this.progressBar = new JProgressBar();
		this.progressBar.setIndeterminate(true);
		this.buttonPane.add(this.progressBar);
		JButton okButton = new JButton("OK");
		okButton.setEnabled(false);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}

	/**
	 * reportProgress
	 * 
	 * Australian National University Data Commons
	 * 
	 * Updates the components on this page to reflect the progress update values passed to this method as arguments.
	 * 
	 * @see gov.loc.repository.bagit.ProgressListener#reportProgress(java.lang.String, java.lang.Object, java.lang.Long, java.lang.Long)
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param activity
	 * @param item
	 * @param count
	 * @param total
	 */
	@Override
	public void reportProgress(final String activity, final Object item, final Long count, final Long total)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				if (activity.equals("done"))
					ProgressDialog.this.dispose();
				else
				{
					if (!ProgressDialog.this.isVisible())
						ProgressDialog.this.setVisible(true);
					logTextPane.setText(logTextPane.getText() + "\r\n" + ProgressListenerHelper.format(activity, item, count, total));
				}
			}
		});
	}
	
	@Override
	public void setVisible(boolean b)
	{
		this.setLocationRelativeTo(MainWindow.getInstance());
		super.setVisible(b);
	}
}
