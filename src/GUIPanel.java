/*
 * GUIPanel.java
 * 
 * Created on 2013-06-11 10:46:32 AM
 * 
 * Inspired by DialogDemo.java 
 * (http://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#dialogdemo):
 * 
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * Creates graphical user interface for the application
 * 
 * @author Olga Tsibulevskaya
 */
public class GUIPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	final JFrame frame;
	final JLabel label;
	String option = "Please choose an option:";
	Font font = new Font("Arial", Font.BOLD, 16);
	/**
	 * Creates new panels
	 * @param frame
	 */
	public GUIPanel(JFrame frame) {
		super(new BorderLayout());
		this.frame = frame;
		// 2 panels so far
		JPanel midtermsPanel = createMidtermsPanel();
		JPanel finalsPanel = createFinalsPanel();
		// at the bottom
		label = new JLabel("Choose an option and click the button", JLabel.CENTER);
		label.setFont(font);
		
		// Lay them out
		Border padding = BorderFactory.createEmptyBorder(20,20,20,20);
		midtermsPanel.setBorder(padding);
		finalsPanel.setBorder(padding);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Midterms", null, midtermsPanel, option);
		tabbedPane.addTab("Finals", null, finalsPanel, option);
		tabbedPane.setFont(font);
		
		add(tabbedPane, BorderLayout.CENTER);
		add(label, BorderLayout.PAGE_END);
		label.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
	}
	public JLabel getLabel() {
		return label;
	}
	public JFrame getFrame() {
		return frame;
	}
	/* Creates a panel with given components */
	private JPanel createPane(String description, 
			JRadioButton[] radioButtons, JButton submitButton) {
		int numChoices = radioButtons.length;
		JPanel box = new JPanel();
		
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(Box.createRigidArea(new Dimension(0, 10)));
		
		for (int i = 0; i < numChoices; i++) {
			box.add(radioButtons[i]);
			radioButtons[i].setFont(font);
			box.add(Box.createRigidArea(new Dimension(0, 5)));
		}
		box.add(Box.createRigidArea(new Dimension(0, 10)));
		
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		TitledBorder title = BorderFactory.createTitledBorder(loweredbevel, description);
		box.setBorder(title);
		title.setTitlePosition(TitledBorder.ABOVE_TOP);
		((TitledBorder)box.getBorder()).setTitleFont(font);
		
		double height = box.getMaximumSize().getHeight();
		box.setMaximumSize(new Dimension(frame.getWidth()-100, (int)height));
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		submitButton.setFont(font);
		submitButton.setMaximumSize(new Dimension(100,30));
		JButton	exitButton = new JButton("Exit");
		exitButton.setFont(font);
		exitButton.setMaximumSize(new Dimension(100, 30));
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		buttons.add(submitButton);
		buttons.add(Box.createRigidArea(new Dimension(20, 0)));
		buttons.add(exitButton);
		buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(Box.createRigidArea(new Dimension(0, 40)));
		pane.add(box);
		pane.add(Box.createRigidArea(new Dimension(0, 20)));
		pane.add(buttons);
		
		return pane;
	}
	/* Populates the content of the Midterm panel with different
	 * options and components */
	private JPanel createMidtermsPanel() {
		final int numButtons = 4;
		JRadioButton[] radioButtons = new JRadioButton[numButtons];
		final ButtonGroup group = new ButtonGroup();
		
		JButton submitButton = null;
		
		final String downloadNew = "downloadnew";
		final String removeEmptyRows = "removeempty";
		final String addEmptyRows = "addempty";
		final String downloadAll = "downloadall";
		
		radioButtons[0] = new JRadioButton("Add new entries to the file");
		radioButtons[0].setActionCommand(downloadNew);
		
		radioButtons[1] = new JRadioButton("Remove empty rows");
		radioButtons[1].setActionCommand(removeEmptyRows);
		
		radioButtons[2] = new JRadioButton("Add empty rows");
		radioButtons[2].setActionCommand(addEmptyRows);
		
		radioButtons[3] = new JRadioButton("Download the database into Excel");
		radioButtons[3].setActionCommand(downloadAll);
		
		for (int i = 0; i < numButtons; i++)
			group.add(radioButtons[i]);
		
		radioButtons[0].setSelected(true);
		
		submitButton = new JButton("Submit");
		
		// to make the button react on the 'enter' key
		JRootPane rootPane = frame.getRootPane();
		rootPane.setDefaultButton(submitButton);
		
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = group.getSelection().getActionCommand();
				// pick a command
				if (command == downloadNew) {
					new StudentsMidterm(frame, label, true);
				}
				else if (command == removeEmptyRows) {
					String term = getOptionPane("Remove empty rows", true);
					if (term != null) {
						Excel file = new Excel();
						file.removeEmptyRows(term, frame, true);						
					}
				}
				else if (command == addEmptyRows) {
					String term = getOptionPane("Add empty rows", true);
					
					if (term != null) {
						Excel file = new Excel();
						file.addEmptyRows(term, frame);
					}
				}
				else if (command == downloadAll) {
					new StudentsMidterm(frame, label, false);
				}
			}
		});
		return createPane(option, radioButtons, submitButton);
	}
	/* Creates a new dialog to choose a file according to the current term */
	private String getOptionPane(String s, boolean termname) {
		String termNow = null;	
		if (termname) 
			termNow = new Term().getTerm();
		else 	
			termNow = new Term().getMonth();
		
		String term = (String)JOptionPane.showInputDialog(
				frame, "Choose the term:",
				s, JOptionPane.PLAIN_MESSAGE,
				null, null, termNow);
		return term;
	}
	/* Creates the Finals panel. Not ready yet. */
	private JPanel createFinalsPanel() {
		final int numButtons = 3;
		JRadioButton[] radioButtons = new JRadioButton[numButtons];
		final ButtonGroup group = new ButtonGroup();
		
		JButton submitButton = null;
		
		final String createfile = "createfile";
		final String assignrooms = "assignrooms";
		final String createlist = "createlist";
		
		radioButtons[0] = new JRadioButton("Create the final exam master list");
		radioButtons[0].setActionCommand(createfile);
		
		radioButtons[1] = new JRadioButton("Assign rooms");
		radioButtons[1].setActionCommand(assignrooms);
		
		radioButtons[2] = new JRadioButton("Create a list of students");
		radioButtons[2].setActionCommand(createlist);
		
		for (int i = 0; i < numButtons; i++)
			group.add(radioButtons[i]);
		
		radioButtons[0].setSelected(true);
		
		submitButton = new JButton("Submit");
		
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = group.getSelection().getActionCommand();
				// pick a command
				if (command == createfile) {
					String term = getOptionPane("Choose a month of the exam", false);
					new StudentsFinalInit(label, term);
				}
				else if (command == assignrooms) {
					// create a new class of list of students by reading the file
					String term = getOptionPane("Choose a month of the exam", false);
					String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
					final String fileFinals = newterm + " final exam master list.xlsx";
					
					File file = new File(fileFinals);
					if (! file.exists()) {
						new Message("File " + fileFinals + " doesn't exist");
						// exit?
					}
					label.setText("Getting info from " + fileFinals + " file");
			    	label.paintImmediately(label.getVisibleRect());
					StudentsFinalSec sfs = new StudentsFinalSec(file);
					
					label.setText("Allocating rooms");
			    	label.paintImmediately(label.getVisibleRect());
					sfs.addLocation();
					new Excel().writeLocation(StudentsFinalSec.getList(), file);
			    	
			    	label.setText("Choose an option and click the button");
			    	label.paintImmediately(label.getVisibleRect());
				}
				else if (command == createlist) {
					ArrayList<Student> list = StudentsFinalSec.getList();
					if (list.size() > 0) {  // the same session
						//
					}
					else { 
						String term = getOptionPane("Choose a month of the exam", false);
						String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
						final String fileFinals = newterm + " final exam master list.xlsx";
						
						File file = new File(fileFinals);
						if (! file.exists()) {
							new Message("File " + fileFinals + " doesn't exist");
							// exit?
						}
						label.setText("Getting info from " + fileFinals + " file");
				    	label.paintImmediately(label.getVisibleRect());
						new StudentsFinalSec(file);
						list = StudentsFinalSec.getList();
					}
					
					if (list.size() > 0)
						new Excel().writeListProf(list, label);
					else 
						System.out.println("oops");
				    	
					label.setText("Choose an option and click the button");
					label.paintImmediately(label.getVisibleRect());
					
				}
			}
		});
		return createPane(option + ":", radioButtons, submitButton);
	}
}
