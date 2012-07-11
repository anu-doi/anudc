package au.edu.anu.dcclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog
{

	private final JPanel contentPanel = new JPanel();
	private JLabel lblUser;
	private JLabel lblPassword;
	private JTextField txtUser;
	private JTextField txtPassword;
	
	private String username;
	private String password;

	/**
	 * Create the dialog.
	 */
	public LoginDialog(Component parentComponent)
	{
		setTitle("Login");
		setSize(263, 151);
		this.setLocationRelativeTo(parentComponent);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		this.contentPanel.setLayout(sl_contentPanel);

		this.lblUser = new JLabel("User");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, this.lblUser, 10, SpringLayout.NORTH, this.contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, this.lblUser, 10, SpringLayout.WEST, this.contentPanel);
		this.contentPanel.add(this.lblUser);

		this.lblPassword = new JLabel("Password");
		sl_contentPanel.putConstraint(SpringLayout.WEST, this.lblPassword, 0, SpringLayout.WEST, this.lblUser);
		this.contentPanel.add(this.lblPassword);

		this.txtUser = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, this.txtUser, 0, SpringLayout.NORTH, this.lblUser);
		sl_contentPanel.putConstraint(SpringLayout.WEST, this.txtUser, 48, SpringLayout.EAST, this.lblUser);
		sl_contentPanel.putConstraint(SpringLayout.EAST, this.txtUser, -10, SpringLayout.EAST, this.contentPanel);
		this.contentPanel.add(this.txtUser);
		this.txtUser.setColumns(10);

		this.txtPassword = new JPasswordField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, this.lblPassword, 3, SpringLayout.NORTH, this.txtPassword);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, this.txtPassword, 7, SpringLayout.SOUTH, this.txtUser);
		sl_contentPanel.putConstraint(SpringLayout.WEST, this.txtPassword, 0, SpringLayout.WEST, this.txtUser);
		sl_contentPanel.putConstraint(SpringLayout.EAST, this.txtPassword, 0, SpringLayout.EAST, this.txtUser);
		this.txtPassword.setColumns(10);
		this.contentPanel.add(this.txtPassword);
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
						username = txtUser.getText().toLowerCase().trim();
						password = txtPassword.getText();
						LoginDialog.this.setVisible(false);
					}
				});
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
						LoginDialog.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}
}
