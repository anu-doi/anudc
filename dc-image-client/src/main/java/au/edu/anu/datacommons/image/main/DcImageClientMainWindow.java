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

package au.edu.anu.datacommons.image.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import au.edu.anu.datacommons.image.action.ExitAction;
import au.edu.anu.datacommons.image.log.StreamCapturer;

/**
 * DcImageClientMainWindow
 * 
 * Australian National University Data Commons
 * 
 * Construts the gui and shows them
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class DcImageClientMainWindow {
	JFrame jFrame;
	ExitAction exitAction;
	StraplineEditor straplineEditor;
	
	/**
	 * Constructor
	 * 
	 * Placeholder
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public DcImageClientMainWindow() {
		initFrame();
		initActions();
		initMainGui();
		initLog();
		showMainWindow();
	}
	
	/**
	 * initFrame
	 *
	 * Create the frame for the gui
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private void initFrame() {
		jFrame = new JFrame("Data Commons Image Caption Application");
	}
	
	/**
	 * initActions
	 *
	 * Create the overall actions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private void initActions() {
		exitAction = new ExitAction();
	}
	
	/**
	 * initMainGui
	 *
	 * Create the main gui
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private void initMainGui() {
		jFrame.setJMenuBar(getMenuBar());
		jFrame.getContentPane().add(getPanel());
		jFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitAction.actionPerformed(null);
			}
		});
	}
	
	/**
	 * getMenuBar
	 *
	 * Get the menu bar
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The menu bar
	 */
	private JMenuBar getMenuBar() {
		JMenuBar menubar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(exitAction);
		menubar.add(fileMenu);
		
		return menubar;
	}
	
	/**
	 * getPanel
	 *
	 * Get the main panel
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The panel
	 */
	private JPanel getPanel() {
		straplineEditor = new StraplineEditor();
		return straplineEditor.getPanel();
	}
	
	/**
	 * initLog
	 *
	 * Logs System.out to a text area
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public void initLog() {
		PrintStream ps = System.out;
		System.setOut(new PrintStream(new StreamCapturer(straplineEditor, ps)));
	}
	
	/**
	 * showMainWindow
	 *
	 * Display the main window
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private void showMainWindow() {
		jFrame.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = jFrame.getSize();
		if (window.height > screen.height) {
			window.height = screen.height;
		}
		if (window.width > screen.width) {
			window.width = screen.width;
		}
		int xCoord = (screen.width/2 - window.width/2);
		int yCoord = (screen.height/2 - window.height/2);
		jFrame.setLocation(xCoord, yCoord);
		jFrame.setVisible(true);
	}
}
