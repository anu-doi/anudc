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

package au.edu.anu.datacommons.image.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import au.edu.anu.datacommons.image.main.StraplineEditor;

/**
 * OpenFileChooserAction
 * 
 * Australian National University Data Commons
 * 
 * Action for opening the file chooser dialog
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
public class OpenFileChooserAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	JFileChooser fileChooser;
	StraplineEditor straplineEditor;
	
	/**
	 * Constructor
	 * 
	 * Constructor class for the file chooser dialog
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fileChooser The file chooser dialog to open
	 * @param straplineEditor The strapline editor to set the value for
	 */
	public OpenFileChooserAction(JFileChooser fileChooser, StraplineEditor straplineEditor) {
		super("Open");
		this.fileChooser = fileChooser;
		this.straplineEditor = straplineEditor;
	}
	
	/**
	 * actionPerformed
	 * 
	 * Opens the file chooser dialog and sets the selected location
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param e The action event
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int returnVal = fileChooser.showOpenDialog(straplineEditor.getPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			straplineEditor.setSelectedLocation(file.getAbsolutePath());
		}
	}
}
