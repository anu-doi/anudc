package au.edu.anu.dcclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.tasks.GetUserInfoTask;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.Authenticator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JProgressBar;

public class LoginDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginDialog.class);

	private static LoginDialog instance = null;

	private int optionSelected = JOptionPane.CANCEL_OPTION;
	private String[] userInfo = null;

	private JPanel contentPanel;
	private JLabel lblUser;
	private JLabel lblPassword;
	private JTextField txtUser;
	private JTextField txtPassword;
	private JProgressBar progressBar;

	/**
	 * Create the dialog.
	 */
	protected LoginDialog()
	{
		setResizable(false);
		setTitle("Login");
		setSize(293, 128);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(MainWindow.getInstance());
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel = new JPanel();
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 73, 157, 0 };
		gbl_contentPanel.rowHeights = new int[] { 20, 20, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		this.contentPanel.setLayout(gbl_contentPanel);

		this.lblUser = new JLabel("User");
		GridBagConstraints gbc_lblUser = new GridBagConstraints();
		gbc_lblUser.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblUser.insets = new Insets(0, 0, 5, 5);
		gbc_lblUser.gridx = 0;
		gbc_lblUser.gridy = 0;
		this.contentPanel.add(this.lblUser, gbc_lblUser);

		this.txtUser = new JTextField();
		GridBagConstraints gbc_txtUser = new GridBagConstraints();
		gbc_txtUser.anchor = GridBagConstraints.NORTH;
		gbc_txtUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUser.insets = new Insets(0, 0, 5, 0);
		gbc_txtUser.gridx = 1;
		gbc_txtUser.gridy = 0;
		this.contentPanel.add(this.txtUser, gbc_txtUser);
		this.txtUser.setColumns(10);

		this.lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 0, 0, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		this.contentPanel.add(this.lblPassword, gbc_lblPassword);

		this.txtPassword = new JPasswordField();
		this.txtPassword.setColumns(10);
		GridBagConstraints gbc_txtPassword = new GridBagConstraints();
		gbc_txtPassword.anchor = GridBagConstraints.NORTH;
		gbc_txtPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPassword.gridx = 1;
		gbc_txtPassword.gridy = 1;
		this.contentPanel.add(this.txtPassword, gbc_txtPassword);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						LOGGER.info("Setting credentials: User {}, Password ****.", LoginDialog.this.txtUser.getText());
						progressBar.setVisible(true);
						progressBar.setIndeterminate(true);
						optionSelected = JOptionPane.OK_OPTION;
						Authenticator.setDefault(new DcAuthenticator(LoginDialog.this.txtUser.getText(), LoginDialog.this.txtPassword.getText()));

						GetUserInfoTask task = new GetUserInfoTask(Global.getUserInfoUri());
						final Future<String[]> userInfoResult = ThreadPoolManager.getExecSvc().submit(task);
						ThreadPoolManager.getExecSvc().submit(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									userInfo = userInfoResult.get();
									if (userInfo == null)
									{
										JOptionPane.showMessageDialog(MainWindow.getInstance(), "Invalid username and/or password",
												"Invalid username/password", JOptionPane.ERROR_MESSAGE);
										Authenticator.setDefault(null);
									}
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								catch (ExecutionException e)
								{
									LOGGER.error("Execution exception while authenticating credentials.", e);
									JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unable to connect to server: \r\n\r\n" + e.getMessage(), "Error",
											JOptionPane.ERROR_MESSAGE);
									Authenticator.setDefault(null);
								}
								finally
								{
									progressBar.setIndeterminate(false);
									progressBar.setVisible(false);
									LoginDialog.this.setVisible(false);
								}
							}

						});
					}
				});

				this.progressBar = new JProgressBar();
				this.progressBar.setVisible(false);
				buttonPane.add(this.progressBar);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						optionSelected = JOptionPane.CANCEL_OPTION;
						LoginDialog.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	public void setVisible(boolean b)
	{
		this.setLocationRelativeTo(MainWindow.getInstance());
		this.txtUser.requestFocusInWindow();
		this.progressBar.setIndeterminate(false);
		super.setVisible(b);
	}

	public int display()
	{
		setVisible(true);
		return this.optionSelected;
	}

	public static LoginDialog getInstance()
	{
		if (instance == null)
			instance = new LoginDialog();
		return instance;
	}

	public String[] getUserInfo()
	{
		return userInfo;
	}
}
