package au.edu.anu.dcclient;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTree;
import java.awt.Font;
import java.awt.FlowLayout;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;

import au.edu.anu.dcclient.actions.GetPidBagAction;
import au.edu.anu.dcclient.actions.SavePidBagAction;
import au.edu.anu.dcclient.actions.UploadPidBagAction;
import au.edu.anu.dcclient.explorer.FileExplorer;

public class MainWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);

	private static MainWindow instance = null;

	private JPanel panel_north;
	private JPanel panel_west;
	private JPanel panel_east;
	private JPanel panel_bottom;
	private JPanel panel_centre;
	private JList listItems;
	private JTextField txtPid;
	private JButton btnGet;
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmExit;
	private JTextField txtSearch;
	private JScrollPane scrollPane;
	private JButton btnLogin;
	private JTabbedPane tabbedPane;
	private JPanel pnlLocalBag;
	private FileExplorer bagExplorer;
	private JLabel lblSearch;
	private JLabel lblPid;
	private JPanel pnlServerBag;
	private JButton btnRefresh;
	private JButton btnSave;
	private JButton btnUpload;

	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		setTitle("ANU Data Commons");
		initialize();
		lblStatus.setText(Global.getAppServerUriAsString());
		instance = this;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		BorderLayout borderLayout = (BorderLayout) this.getContentPane().getLayout();
		this.setBounds(100, 100, 688, 586);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.panel_north = new JPanel();
		this.getContentPane().add(this.panel_north, BorderLayout.NORTH);
		this.panel_north.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		this.btnLogin = new JButton("Login");
		this.btnLogin.addActionListener(this);
		this.panel_north.add(this.btnLogin);

		this.menuBar = new JMenuBar();
		this.setJMenuBar(this.menuBar);

		this.mnFile = new JMenu("File");
		this.menuBar.add(this.mnFile);

		this.mntmExit = new JMenuItem("Exit");
		this.mntmExit.addActionListener(this);
		this.mnFile.add(this.mntmExit);

		this.panel_west = new JPanel();
		this.getContentPane().add(this.panel_west, BorderLayout.WEST);

		this.panel_east = new JPanel();
		this.getContentPane().add(this.panel_east, BorderLayout.EAST);

		this.panel_bottom = new JPanel();
		this.getContentPane().add(this.panel_bottom, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_bottom = new GridBagLayout();
		gbl_panel_bottom.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel_bottom.rowHeights = new int[] { 0, 0 };
		gbl_panel_bottom.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_bottom.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		this.panel_bottom.setLayout(gbl_panel_bottom);

		this.lblStatus = new JLabel("Status");
		GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.insets = new Insets(0, 5, 0, 5);
		gbc_lblStatus.gridx = 0;
		gbc_lblStatus.gridy = 0;
		this.panel_bottom.add(this.lblStatus, gbc_lblStatus);

		this.progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.gridx = 2;
		gbc_progressBar.gridy = 0;
		this.panel_bottom.add(this.progressBar, gbc_progressBar);

		this.panel_centre = new JPanel();
		this.getContentPane().add(this.panel_centre, BorderLayout.CENTER);
		GridBagLayout gbl_panel_centre = new GridBagLayout();
		gbl_panel_centre.columnWidths = new int[] { 60, 0, 70, 0, 0 };
		gbl_panel_centre.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_centre.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_centre.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		this.panel_centre.setLayout(gbl_panel_centre);

		this.lblSearch = new JLabel("Search");
		GridBagConstraints gbc_lblSearch = new GridBagConstraints();
		gbc_lblSearch.insets = new Insets(0, 0, 5, 5);
		gbc_lblSearch.anchor = GridBagConstraints.WEST;
		gbc_lblSearch.gridx = 0;
		gbc_lblSearch.gridy = 0;
		this.panel_centre.add(this.lblSearch, gbc_lblSearch);

		this.txtSearch = new JTextField();
		GridBagConstraints gbc_txtSearch = new GridBagConstraints();
		gbc_txtSearch.gridwidth = 2;
		gbc_txtSearch.insets = new Insets(0, 0, 5, 5);
		gbc_txtSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearch.gridx = 1;
		gbc_txtSearch.gridy = 0;
		this.panel_centre.add(this.txtSearch, gbc_txtSearch);
		this.txtSearch.setColumns(10);

		this.scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		this.panel_centre.add(this.scrollPane, gbc_scrollPane);

		this.listItems = new JList();
		this.scrollPane.setViewportView(this.listItems);

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 3;
		gbc_tabbedPane.gridy = 1;
		this.panel_centre.add(this.tabbedPane, gbc_tabbedPane);

		this.pnlLocalBag = new JPanel();
		this.tabbedPane.addTab("Local Bag", null, this.pnlLocalBag, null);
		GridBagLayout gbl_pnlLocalBag = new GridBagLayout();
		gbl_pnlLocalBag.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_pnlLocalBag.rowHeights = new int[] { 322, 0, 0 };
		gbl_pnlLocalBag.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlLocalBag.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		this.pnlLocalBag.setLayout(gbl_pnlLocalBag);

		this.bagExplorer = new FileExplorer(new File("C:\\"));
		GridBagConstraints gbc_bagExplorer = new GridBagConstraints();
		gbc_bagExplorer.gridwidth = 3;
		gbc_bagExplorer.insets = new Insets(0, 0, 5, 0);
		gbc_bagExplorer.fill = GridBagConstraints.BOTH;
		gbc_bagExplorer.gridx = 0;
		gbc_bagExplorer.gridy = 0;
		this.pnlLocalBag.add(this.bagExplorer, gbc_bagExplorer);

		this.lblPid = new JLabel("Pid");
		GridBagConstraints gbc_lblPid = new GridBagConstraints();
		gbc_lblPid.insets = new Insets(0, 0, 0, 5);
		gbc_lblPid.anchor = GridBagConstraints.WEST;
		gbc_lblPid.gridx = 0;
		gbc_lblPid.gridy = 2;
		this.panel_centre.add(this.lblPid, gbc_lblPid);

		this.txtPid = new JTextField();
		this.txtPid.setEnabled(false);
		GridBagConstraints gbc_txtPid = new GridBagConstraints();
		gbc_txtPid.insets = new Insets(0, 0, 0, 5);
		gbc_txtPid.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPid.gridx = 1;
		gbc_txtPid.gridy = 2;
		this.panel_centre.add(this.txtPid, gbc_txtPid);
		this.txtPid.setColumns(10);
		this.txtPid.getDocument().addDocumentListener(new DocumentListener()
		{

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				toggleRetrieve(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				toggleRetrieve(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				// TODO Determine when this event is fired, if at all.
			}

			private void toggleRetrieve(DocumentEvent e)
			{
				if (txtPid.getText().length() > 0)
					btnGet.setEnabled(true);
				else
					btnGet.setEnabled(false);
			}
		});

		this.btnGet = new JButton("Get");
		this.btnGet.setEnabled(false);
		this.btnGet.addActionListener(new GetPidBagAction(this.txtPid, this.bagExplorer));

		this.btnRefresh = new JButton("Refresh");
		this.btnRefresh.setEnabled(false);
		this.btnRefresh.addActionListener(this);
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.insets = new Insets(0, 0, 0, 5);
		gbc_btnRefresh.gridx = 0;
		gbc_btnRefresh.gridy = 1;
		this.pnlLocalBag.add(this.btnRefresh, gbc_btnRefresh);

		this.btnSave = new JButton("Save");
		this.btnSave.setEnabled(false);
		this.btnSave.addActionListener(new SavePidBagAction(this.txtPid, this.bagExplorer));
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 1;
		this.pnlLocalBag.add(this.btnSave, gbc_btnSave);

		this.btnUpload = new JButton("Upload");
		this.btnUpload.setEnabled(false);
		this.btnUpload.addActionListener(new UploadPidBagAction(this.txtPid, this.bagExplorer));
		GridBagConstraints gbc_btnUpload = new GridBagConstraints();
		gbc_btnUpload.gridx = 2;
		gbc_btnUpload.gridy = 1;
		this.pnlLocalBag.add(this.btnUpload, gbc_btnUpload);

		this.pnlServerBag = new JPanel();
		this.tabbedPane.addTab("Server Bag", null, this.pnlServerBag, null);
		GridBagConstraints gbc_btnGet = new GridBagConstraints();
		gbc_btnGet.insets = new Insets(0, 0, 0, 5);
		gbc_btnGet.gridx = 2;
		gbc_btnGet.gridy = 2;
		this.panel_centre.add(this.btnGet, gbc_btnGet);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnRefresh)
		{
			do_btnRefresh_actionPerformed(e);
		}
		if (e.getSource() == this.btnLogin)
		{
			do_btnLogin_actionPerformed(e);
		}
		if (e.getSource() == this.mntmExit)
		{
			do_mntmExit_actionPerformed(e);
		}
	}

	protected void do_mntmExit_actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

	protected void do_btnLogin_actionPerformed(ActionEvent e)
	{
		LoginDialog ld = LoginDialog.getInstance();
		if (ld.display() == JOptionPane.OK_OPTION)
		{
			LOGGER.debug("OK button clicked in Login Dialog.");
			if (ld.getUserInfo() != null)
			{
				enableControls();
				btnLogin.setText(MessageFormat.format("Switch user ({0}, {1})", ld.getUserInfo()[0], ld.getUserInfo()[1]));
			}
		}
	}
	
	private void enableControls()
	{
		// Enable controls.
		txtPid.setEnabled(true);
		btnRefresh.setEnabled(true);
		btnSave.setEnabled(true);
		btnUpload.setEnabled(true);
	}

	public static MainWindow getInstance()
	{
		return instance;
	}

	protected void do_btnRefresh_actionPerformed(ActionEvent e)
	{
		bagExplorer.refresh();
	}
}
