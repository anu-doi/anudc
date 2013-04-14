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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import au.edu.anu.datacommons.image.magick.ConvertImage;
import au.edu.anu.datacommons.image.main.StraplineEditor;

/**
 * AddCaptionAction
 * 
 * Australian National University Data Commons
 * 
 * Action to add a caption to an item
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
public class AddCaptionAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	StraplineEditor straplineEditor;
	ConvertImage convertImage;
	
	/**
	 * Constructor
	 * 
	 * Constructor class for AddCaptionAction
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param straplineEditor The strapline editor to retrieve caption information from
	 */
	public AddCaptionAction(StraplineEditor straplineEditor) {
		super("Add Caption");
		this.straplineEditor = straplineEditor;
		this.convertImage = new ConvertImage();
	}

	/**
	 * actionPerformed
	 * 
	 * Execute the adding of the caption to the image
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param e The ActionEvent that has occured
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Performing Add Caption Action");
		
		try {
			straplineEditor.getPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String leftText = straplineEditor.getLeftStraplineText();
			String rightText = straplineEditor.getRightStraplineText();
			String location = straplineEditor.getSelectedLocation();
			convertImage.addTextToImage(location, leftText, rightText);
		}
		catch (IOException ex) {
			System.err.println("Exception converting image" + ex);
		}
		finally {
			straplineEditor.getPanel().setCursor(Cursor.getDefaultCursor());
		}
	}

}
