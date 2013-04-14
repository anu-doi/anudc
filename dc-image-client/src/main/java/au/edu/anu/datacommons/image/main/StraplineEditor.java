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

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import au.edu.anu.datacommons.image.action.AddCaptionAction;
import au.edu.anu.datacommons.image.action.OpenFileChooserAction;
import au.edu.anu.datacommons.image.log.Consumer;

/**
 * StraplineEditor
 * 
 * Australian National University Data Commons
 * 
 * Class that creates the main panel for the Data Commons Image Client
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
public class StraplineEditor implements Consumer {
	private JPanel panel;
	private JTextField selectedLocation;
	private JTextField leftStraplineText;
	private JTextField rightStraplineText;
	private JTextArea textLog;
	
	/**
	 * Constructor
	 * 
	 * Constructor method for StraplineEditor
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public StraplineEditor() {
		setupPanel();
	}
	
	/**
	 * setupPanel
	 *
	 * Setup the panel
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private void setupPanel() {
		panel = new JPanel(new GridBagLayout());
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		OpenFileChooserAction openFileChooserAction = new OpenFileChooserAction(fileChooser, this);
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		int y = 0;
		int x = 0;
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 0.1;
		constraints.insets = new Insets(2,2,2,2);
		
		JLabel label = new JLabel("File or directory:");
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(label, constraints);
		
		selectedLocation = new JTextField("", 50);
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(selectedLocation, constraints);
		
		JButton button = new JButton();
		button.setAction(openFileChooserAction);
		constraints.weightx = 0.1;
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(button, constraints);
		
		y++;
		x = 0;
		
		label = new JLabel("Left Strapline Text");
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(label, constraints);
		
		String text = DefaultProperties.getProperty("strapline.left.text");
		leftStraplineText = new JTextField(text, 50);
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(leftStraplineText, constraints);
		
		y++;
		x = 0;
				
		label = new JLabel("Right Strapline Text");
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(label, constraints);
		
		rightStraplineText = new JTextField("", 50);
		constraints.gridx = x++;
		constraints.gridy = y;
		panel.add(rightStraplineText, constraints);
		
		y++;
		x= 2;
		
		button = new JButton();
		button.setAction(new AddCaptionAction(this));
		constraints.gridx = x;
		constraints.gridy = y;
		panel.add(button, constraints);
		
		y++;
		x = 0;
		
		textLog = new JTextArea();
		textLog.setRows(10);
		textLog.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textLog);
		scrollPane.setBounds(3, 3, 300, 200);
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		panel.add(scrollPane, constraints);
	}
	
	/**
	 * getPanel
	 *
	 * Get the panel that was created on initialisation
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The panel
	 */
	public JPanel getPanel() {
		return panel;
	}
	
	/**
	 * getLeftStraplineText
	 *
	 * Get the text to place on the left of the caption strapline
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The left strapline text
	 */
	public String getLeftStraplineText() {
		return leftStraplineText.getText();
	}
	
	/**
	 * getRightStraplineText
	 *
	 * Get the text to place on the right of the caption strapline
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The right strapline text
	 */
	public String getRightStraplineText() {
		return rightStraplineText.getText();
	}
	
	/**
	 * getSelectedLocation
	 *
	 * Get the location that has been selected for image(s) to have captions added to
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The location
	 */
	public String getSelectedLocation() {
		return selectedLocation.getText();
	}
	
	/**
	 * setSelectedLocation
	 *
	 * Set the location with image(s) to add captions to
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param location The location
	 */
	public void setSelectedLocation(String location) {
		selectedLocation.setText(location);
	}

	/**
	 * appendText
	 * 
	 * Append text to the text area
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param text The text to add to the text area
	 * @see au.edu.anu.datacommons.image.log.Consumer#appendText(java.lang.String)
	 */
	@Override
	public void appendText(final String text) {
		if (EventQueue.isDispatchThread()) {
			textLog.append(text);
			textLog.setCaretPosition(textLog.getText().length());
		}
		else {
			EventQueue.invokeLater(new Runnable() {
				@Override
                public void run() {
                    appendText(text);
                }
			});
		}
	}
}
