/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.dcclient.gui;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.bagfilesexplorer.BagFilesTree;
import au.edu.anu.dcclient.tasks.DownloadFilesTask;
import au.edu.anu.dcclient.tasks.GetBagSummaryTask;
import au.edu.anu.dcclient.tasks.UploadFilesetTask;

/**
 * This class represents the main application window.
 */
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
	private JLabel lblSearch;
	private JLabel lblPid;
	private JPanel pnlServerBag;
	private JScrollPane scrollPane_1;
	private BagFilesTree tree;
	private final JToolBar toolBar = new JToolBar();
	private final JPanel panel = new JPanel();
	private final JLabel lblFileSize = new JLabel("File Size");
	private final JLabel lblFileSizeValue = new JLabel("");
	private final JLabel lblMd5 = new JLabel("MD5");
	private final JLabel lblMd5Value = new JLabel("");

	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		setTitle("ANU Data Commons");
		initGui();
		lblStatus.setText(Global.getAppServerUriAsString());
		instance = this;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initGui()
	{
		BorderLayout borderLayout = (BorderLayout) this.getContentPane().getLayout();
		this.setBounds(100, 100, 688, 586);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.panel_north = new JPanel();
		this.getContentPane().add(this.panel_north, BorderLayout.NORTH);

		this.btnLogin = new JButton("Login");
		this.btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						LoginDialog ld = new LoginDialog(MainWindow.this);
						if (ld.display() == JOptionPane.OK_OPTION) {
							String[] userInfo = ld.getUserInfo();
							if (userInfo != null) {
								setControlsEnabled(true);
								btnLogin.setText(format("Switch user ({0}, {1})", userInfo[0], userInfo[1]));
								MainWindow.this.getRootPane().setDefaultButton(btnGet);
								txtPid.requestFocusInWindow();
							} else {
								setControlsEnabled(false);
								btnLogin.setText("Login");
								MainWindow.this.getRootPane().setDefaultButton(btnLogin);
								JOptionPane.showMessageDialog(MainWindow.this, "Unable to log into ANU Data Commons",
										"Authentication Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
			}
		});
		panel_north.setLayout(new MigLayout("", "[87px,grow][57px]", "[23px]"));
		
		panel_north.add(toolBar, "cell 0 0,alignx left,aligny center");
		this.panel_north.add(this.btnLogin, "cell 1 0,alignx left,aligny top");

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
		panel_bottom.setLayout(new MigLayout("", "[184px,grow][440px,grow]", "[19px]"));

		this.lblStatus = new JLabel("Status");
		this.panel_bottom.add(this.lblStatus, "cell 0 0,alignx left,aligny center");

		this.progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		this.panel_bottom.add(this.progressBar, "cell 1 0,growx,aligny center");

		this.panel_centre = new JPanel();
		this.getContentPane().add(this.panel_centre, BorderLayout.CENTER);
		panel_centre.setLayout(new MigLayout("", "[33px][27px][86px][111px][66.00px,grow][160px,grow]", "[20px][408px,grow][23px]"));

		this.lblSearch = new JLabel("Search");
		this.panel_centre.add(this.lblSearch, "cell 0 0,alignx left,aligny center");

		this.txtSearch = new JTextField();
		this.panel_centre.add(this.txtSearch, "cell 2 0,growx,aligny center");
		this.txtSearch.setColumns(10);

		this.scrollPane = new JScrollPane();
		this.panel_centre.add(this.scrollPane, "cell 0 1 3 1,grow");

		this.listItems = new JList();
		this.scrollPane.setViewportView(this.listItems);

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.panel_centre.add(this.tabbedPane, "cell 3 1 2 1,grow");
		
		panel_centre.add(panel, "cell 5 1,grow");
		panel.setLayout(new MigLayout("", "[][grow]", "[][]"));
		
		panel.add(lblFileSize, "cell 0 0");
		
		panel.add(lblFileSizeValue, "cell 1 0");
		
		panel.add(lblMd5, "cell 0 1");
		
		panel.add(lblMd5Value, "cell 1 1");

		this.lblPid = new JLabel("Pid");
		this.lblPid.setDisplayedMnemonic('P');
		this.panel_centre.add(this.lblPid, "cell 0 2,alignx left,aligny center");

		this.txtPid = new JTextField();
		this.lblPid.setLabelFor(this.txtPid);
		this.txtPid.setEnabled(false);
		this.panel_centre.add(this.txtPid, "cell 2 2,growx,aligny center");
		this.txtPid.setColumns(10);
		this.txtPid.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						((JTextField) e.getComponent()).selectAll();
					}
				});
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		this.txtPid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton defaultButton = MainWindow.this.getRootPane().getDefaultButton();
				if (defaultButton.isEnabled()) {
					defaultButton.doClick();
				}
			}
		});
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

		this.btnGet = new JButton("Retrieve File List");
		this.btnGet.setEnabled(false);
		this.btnGet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GetBagSummaryTask getBagSummaryTask = new GetBagSummaryTask(txtPid.getText().toLowerCase().trim()) {
					@Override
					protected void done() {
						super.done();
						try {
							BagSummary bagSummary = get();
							BagFilesTree tree = new BagFilesTree(bagSummary);
							tree.addPropertyChangeListener(new TaskProgressListener(progressBar) {
								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									super.propertyChange(evt);
									if ("state".equals(evt.getPropertyName()) && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
										SwingUtilities.invokeLater(new Runnable() {

											@Override
											public void run() {
												btnGet.doClick();
											}
											
										});
									}
								}
							});
							tree.setTransferHandler(new TransferHandler() {

								private static final long serialVersionUID = 1L;
								
								@Override
								public int getSourceActions(JComponent c)
								{
									return COPY;
								}
								
								@Override
								public boolean canImport(TransferSupport support) {
									// TODO Implement
									return super.canImport(support);
								}
								
								
								@Override
								protected Transferable createTransferable(JComponent component)
								{
									LOGGER.trace("In createTransferable");
									TreePath[] paths = ((BagFilesTree) component).getSelectionPaths();
									List<String> filepaths = new ArrayList<String>();
									for (int i = 0; i < paths.length; i++) {
										DefaultMutableTreeNode node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
										Entry<String, FileSummary> entry = (Entry<String, FileSummary>) node.getUserObject();
										filepaths.add(entry.getKey());
									}

									DownloadFilesTask dlTask = new DownloadFilesTask(txtPid.getText(), filepaths);
									dlTask.addPropertyChangeListener(new TaskProgressListener(progressBar));
									
									Transferable transferable = new GenericTransferable(dlTask);
									return transferable;
								}
								
								@Override
								protected void exportDone(JComponent source, Transferable data, int action) {
									super.exportDone(source, data, action);
									LOGGER.trace("In exportDone");
								}
								

								class GenericTransferable implements Transferable
								{
									private final DownloadFilesTask dlTask;

									public GenericTransferable(DownloadFilesTask dlTask) {
										this.dlTask = dlTask;
									}

									@Override
									public DataFlavor[] getTransferDataFlavors()
									{
										return new DataFlavor[] { DataFlavor.javaFileListFlavor };
									}

									@Override
									public boolean isDataFlavorSupported(DataFlavor flavor)
									{
										return flavor.equals(DataFlavor.javaFileListFlavor);
									}

									@Override
									public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
									{
										LOGGER.trace("In getTransferData {} {}", flavor.toString(), this.toString());
										Map<String, File> fileResult;
										List<File> dlFiles = new ArrayList<File>();
										if (flavor.equals(DataFlavor.javaFileListFlavor)) {
											if (dlTask.getState() == SwingWorker.StateValue.PENDING) {
												dlTask.execute();
											}
											if (dlTask.getState() == SwingWorker.StateValue.DONE) {
												try {
													fileResult = dlTask.get();
													dlFiles.addAll(fileResult.values());
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												} catch (ExecutionException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										} else {
											throw new UnsupportedFlavorException(flavor);
										}
										
										return dlFiles;
									}
								}
							});
							tree.addTreeSelectionListener(new TreeSelectionListener () {

								@Override
								public void valueChanged(TreeSelectionEvent e) {
									TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
									if (newLeadSelectionPath != null) {
										DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) newLeadSelectionPath
												.getLastPathComponent();
										Entry<String, FileSummary> entry = (Entry<String, FileSummary>) selectedNode
												.getUserObject();
										lblFileSizeValue.setText(entry.getValue().getFriendlySize());
										lblMd5Value.setText(entry.getValue().getMessageDigests().get(Algorithm.MD5.javaSecurityAlgorithm));
									} else {
										lblFileSizeValue.setText("");
										lblMd5Value.setText("");
									}
								}
								
							});
							tree.setDropTarget(new DropTarget(tree, new DropTargetListener() {

								@Override
								public void dragEnter(DropTargetDragEvent dtde) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void dragOver(DropTargetDragEvent dtde) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void dropActionChanged(DropTargetDragEvent dtde) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void dragExit(DropTargetEvent dte) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void drop(DropTargetDropEvent dtde) {
									try {
										Transferable tr = dtde.getTransferable();
										DataFlavor[] flavors = tr.getTransferDataFlavors();

										for (int i = 0; i < flavors.length; i++) {
											if (flavors[i].isFlavorJavaFileListType()) {
												dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
												List<File> files = (List<File>) tr.getTransferData(flavors[i]);
												BagFilesTree bagFilesTree = (BagFilesTree) ((DropTarget) dtde.getSource()).getComponent();
												String pid = bagFilesTree.getBagSummary().getPid();
												
												UploadFilesetTask ulFilesetTask = new UploadFilesetTask(pid, files) {
													@Override
													protected void done() {
														super.done();
														btnGet.doClick();
													}
												};
												ulFilesetTask.addPropertyChangeListener(new TaskProgressListener(progressBar));
												ulFilesetTask.execute();
												
												dtde.dropComplete(true);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										dtde.rejectDrop();
									}
									return;
								}
								
							}));
							scrollPane_1.setViewportView(tree);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				
				getBagSummaryTask.addPropertyChangeListener(new TaskProgressListener(progressBar));
				getBagSummaryTask.execute();
			}
		});

		this.pnlServerBag = new JPanel();
		this.tabbedPane.addTab("Server Bag", null, this.pnlServerBag, null);
		GridBagLayout gbl_pnlServerBag = new GridBagLayout();
		gbl_pnlServerBag.columnWidths = new int[]{0, 0};
		gbl_pnlServerBag.rowHeights = new int[]{0, 0};
		gbl_pnlServerBag.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_pnlServerBag.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		pnlServerBag.setLayout(gbl_pnlServerBag);
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		pnlServerBag.add(scrollPane_1, gbc_scrollPane_1);
		this.panel_centre.add(this.btnGet, "cell 3 2,alignx center,aligny center");
	}

	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnLogin)
		{
			do_btnLogin_actionPerformed(e);
		}
		if (e.getSource() == this.mntmExit)
		{
			do_mntmExit_actionPerformed(e);
		}
	}

	/**
	 * Method called when the file menu item exit is clicked.
	 * 
	 * @param e
	 *            ActionEvent object
	 */
	protected void do_mntmExit_actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

	/**
	 * Method called when the Login button is clicked.
	 * 
	 * @param e
	 *            ActionEvent object
	 */
	protected void do_btnLogin_actionPerformed(ActionEvent e)
	{
		// TODO Remove this method.
		
	}

	/**
	 * Enables the disabled controls on this window.
	 */
	private void setControlsEnabled(boolean newState)
	{
		txtPid.setEnabled(newState);
	}

	/**
	 * Gets the singleton instance of this window.
	 * 
	 * @return MainWindow instance
	 */
	public static MainWindow getInstance()
	{
		return instance;
	}
}
