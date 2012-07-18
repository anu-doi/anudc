package au.edu.anu.dcclient;

import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.Authenticator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagProps;
import au.edu.anu.dcclient.duvanslabbert.FileExplorer;
import javax.swing.JSeparator;

public class MainWindow
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);
	private static Component mainWindow = null;

	private JFrame frmAnuDataCommons;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmExit;
	private JPanel panelPid;
	private JLabel lblPid;
	private JTextField txtPid;
	private JButton btnRetrieve;
	private JToolBar toolBar;
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private FileExplorer bagExplorer;
	private JTabbedPane tabbedPane_1;
	private JPanel panelBagInfo;
	private JButton btnSave;
	private JMenu mnEdit;
	private JMenuItem mntmRefresh;
	private JButton btnUpload;
	private JButton btnDebug;
	private JMenuItem mntmLogin;
	private JSeparator separator;
	private LoginDialog loginDialog = null;
	private JLabel lblDcUri;
	
	/**
	 * MainWindow
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for MainWindow
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 */
	public MainWindow()
	{
		initialize();
		initActions();
		if (mainWindow == null)
			mainWindow = frmAnuDataCommons;
	}

	/**
	 * initialize
	 * 
	 * Australian National University Data Commons
	 * 
	 * Initializes the contents of this window by adding controls.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 */
	private void initialize()
	{
		this.frmAnuDataCommons = new JFrame();
		this.frmAnuDataCommons.setTitle("ANU Data Commons Client");
		this.frmAnuDataCommons.setBounds(1800, 100, 787, 594);
		// TODO Uncomment the following line.
		// this.frmAnuDataCommons.setLocationRelativeTo(null);				// To centre the window on the screen.
		this.frmAnuDataCommons.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.menuBar = new JMenuBar();
		this.frmAnuDataCommons.setJMenuBar(this.menuBar);

		this.mnFile = new JMenu("File");
		this.menuBar.add(this.mnFile);

		this.mntmExit = new JMenuItem("Exit");
		this.mntmLogin = new JMenuItem("Login...");
		this.mnFile.add(this.mntmLogin);

		this.separator = new JSeparator();
		this.mnFile.add(this.separator);
		this.mnFile.add(this.mntmExit);

		this.mnEdit = new JMenu("Edit");
		this.menuBar.add(this.mnEdit);

		this.mntmRefresh = new JMenuItem("Refresh");
		this.mnEdit.add(this.mntmRefresh);
		SpringLayout springLayout = new SpringLayout();
		this.frmAnuDataCommons.getContentPane().setLayout(springLayout);

		this.panelPid = new JPanel();
		springLayout.putConstraint(SpringLayout.SOUTH, this.panelPid, 60, SpringLayout.NORTH, this.frmAnuDataCommons.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, this.panelPid, 769, SpringLayout.WEST, this.frmAnuDataCommons.getContentPane());
		this.frmAnuDataCommons.getContentPane().add(this.panelPid);
		SpringLayout sl_panelPid = new SpringLayout();
		this.panelPid.setLayout(sl_panelPid);

		this.lblPid = new JLabel("Pid");
		sl_panelPid.putConstraint(SpringLayout.NORTH, this.lblPid, 10, SpringLayout.NORTH, this.panelPid);
		sl_panelPid.putConstraint(SpringLayout.WEST, this.lblPid, 10, SpringLayout.WEST, this.panelPid);
		this.panelPid.add(this.lblPid);

		this.txtPid = new JTextField();
		sl_panelPid.putConstraint(SpringLayout.NORTH, this.txtPid, -3, SpringLayout.NORTH, this.lblPid);
		sl_panelPid.putConstraint(SpringLayout.WEST, this.txtPid, 25, SpringLayout.EAST, this.lblPid);
		this.panelPid.add(this.txtPid);
		this.txtPid.setColumns(10);

		this.btnRetrieve = new JButton("Retrieve");
		sl_panelPid.putConstraint(SpringLayout.NORTH, this.btnRetrieve, 4, SpringLayout.NORTH, this.panelPid);
		sl_panelPid.putConstraint(SpringLayout.WEST, this.btnRetrieve, 39, SpringLayout.EAST, this.txtPid);
		this.btnRetrieve.setEnabled(false);
		this.panelPid.getRootPane().setDefaultButton(btnRetrieve);
		this.panelPid.add(this.btnRetrieve);

		this.toolBar = new JToolBar();
		sl_panelPid.putConstraint(SpringLayout.WEST, this.toolBar, 0, SpringLayout.WEST, this.frmAnuDataCommons.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, this.panelPid, 6, SpringLayout.SOUTH, this.toolBar);
		springLayout.putConstraint(SpringLayout.WEST, this.panelPid, 0, SpringLayout.WEST, this.toolBar);
		sl_panelPid.putConstraint(SpringLayout.NORTH, this.toolBar, 10, SpringLayout.NORTH, this.frmAnuDataCommons.getContentPane());
		this.frmAnuDataCommons.getContentPane().add(this.toolBar);

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, this.tabbedPane, 10, SpringLayout.SOUTH, this.panelPid);
		
		this.lblDcUri = new JLabel(Global.getBagUploadUrl());
		sl_panelPid.putConstraint(SpringLayout.NORTH, this.lblDcUri, 0, SpringLayout.NORTH, this.lblPid);
		sl_panelPid.putConstraint(SpringLayout.WEST, this.lblDcUri, 6, SpringLayout.EAST, this.btnRetrieve);
		this.panelPid.add(this.lblDcUri);
		springLayout.putConstraint(SpringLayout.WEST, this.tabbedPane, 10, SpringLayout.WEST, this.frmAnuDataCommons.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, this.tabbedPane, -10, SpringLayout.SOUTH, this.frmAnuDataCommons.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, this.tabbedPane, -10, SpringLayout.EAST, this.frmAnuDataCommons.getContentPane());
		this.frmAnuDataCommons.getContentPane().add(this.tabbedPane);

		this.panel = new JPanel();
		this.tabbedPane.addTab("Bag", null, this.panel, null);
		SpringLayout sl_panel = new SpringLayout();
		this.panel.setLayout(sl_panel);

		this.bagExplorer = new FileExplorer(new File("C:\\"));
		sl_panel.putConstraint(SpringLayout.NORTH, this.bagExplorer, 5, SpringLayout.NORTH, this.panel);
		sl_panel.putConstraint(SpringLayout.WEST, this.bagExplorer, 10, SpringLayout.WEST, this.panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, this.bagExplorer, -5, SpringLayout.SOUTH, this.panel);
		sl_panel.putConstraint(SpringLayout.EAST, this.bagExplorer, 262, SpringLayout.WEST, this.panel);
		this.panel.add(this.bagExplorer);

		this.panelBagInfo = new JPanel();
		sl_panel.putConstraint(SpringLayout.NORTH, this.panelBagInfo, 5, SpringLayout.NORTH, this.panel);
		sl_panel.putConstraint(SpringLayout.WEST, this.panelBagInfo, 6, SpringLayout.EAST, this.bagExplorer);
		sl_panel.putConstraint(SpringLayout.SOUTH, this.panelBagInfo, -5, SpringLayout.SOUTH, this.panel);
		sl_panel.putConstraint(SpringLayout.EAST, this.panelBagInfo, -10, SpringLayout.EAST, this.panel);
		this.panel.add(this.panelBagInfo);
		SpringLayout sl_panelBagInfo = new SpringLayout();
		this.panelBagInfo.setLayout(sl_panelBagInfo);

		this.btnSave = new JButton("Save");
		sl_panelBagInfo.putConstraint(SpringLayout.WEST, this.btnSave, 10, SpringLayout.WEST, this.panelBagInfo);
		sl_panelBagInfo.putConstraint(SpringLayout.SOUTH, this.btnSave, -10, SpringLayout.SOUTH, this.panelBagInfo);
		this.panelBagInfo.add(this.btnSave);

		this.btnUpload = new JButton("Upload");
		sl_panelBagInfo.putConstraint(SpringLayout.NORTH, this.btnUpload, 0, SpringLayout.NORTH, this.btnSave);
		sl_panelBagInfo.putConstraint(SpringLayout.WEST, this.btnUpload, 6, SpringLayout.EAST, this.btnSave);
		this.panelBagInfo.add(this.btnUpload);

		this.btnDebug = new JButton("Debug");
		sl_panelBagInfo.putConstraint(SpringLayout.SOUTH, this.btnDebug, 0, SpringLayout.SOUTH, this.btnSave);
		sl_panelBagInfo.putConstraint(SpringLayout.EAST, this.btnDebug, -10, SpringLayout.EAST, this.panelBagInfo);
		this.panelBagInfo.add(this.btnDebug);

		this.tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		this.tabbedPane.addTab("New tab", null, this.tabbedPane_1, null);
	}

	/**
	 * initActions
	 * 
	 * Australian National University Data Commons
	 * 
	 * Initializes the ActionListeners on components in this window.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 */
	private void initActions()
	{
		this.btnRetrieve.addActionListener(new GetPidBagAction(this.txtPid, this.bagExplorer));
		this.btnSave.addActionListener(new SavePidBagAction(this.txtPid, this.bagExplorer));
		this.btnUpload.addActionListener(new UploadPidBagAction(this.txtPid, this.bagExplorer));
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
				if (txtPid.getText().trim().equals(""))
					btnRetrieve.setEnabled(false);
				else
					btnRetrieve.setEnabled(true);
			}
		});

		this.btnDebug.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// bagExplorer.changeDir(new File("C:\\Rahul\\FileUpload\\Store\\test_5"));
				DcBag bag = new DcBag(bagExplorer.getBagDir(), LoadOption.BY_FILES);
				bag.setBagProperty(DcBagProps.FIELD_DATASOURCE, DcBagProps.DataSource.INSTRUMENT.toString());
				try
				{
					bag.save();
				}
				catch (Exception e1)
				{
					JOptionPane.showMessageDialog(MainWindow.getMainParent(), "Unable to change data source property to instrument.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Menu item - Edit > Refresh
		this.mntmRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				bagExplorer.refresh();
			}
		});
		
		// Menu item - File > Login
		this.mntmLogin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (loginDialog == null)
				{
					loginDialog = new LoginDialog(frmAnuDataCommons);
					loginDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				}
				loginDialog.setVisible(true);
				
				LOGGER.debug("Username: {}, password: {}", loginDialog.getUsername(), "****");
				Authenticator.setDefault(new DcAuthenticator(loginDialog.getUsername(), loginDialog.getPassword()));
			}
		});

		// Menu item - File > Exit
		this.mntmExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
	}

	/**
	 * setVisible
	 * 
	 * Australian National University Data Commons
	 * 
	 * Changes the visibility status of this window.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param isVisible
	 *            true to display, false to hide.
	 */
	public void setVisible(boolean isVisible)
	{
		this.frmAnuDataCommons.setVisible(isVisible);
	}
	
	public static Component getMainParent()
	{
		return mainWindow;
	}
}
