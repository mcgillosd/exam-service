/*
 * PanelFinals.java
 * 
 * Created on Jul 24, 2013 11:50:11 AM
 */

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Creates a panel for Finals
 * 
 * @see PanelTabs
 * @author Olga Tsibulevskaya
 */
public class PanelFinals extends PanelTabs {

	private static final long serialVersionUID = 1L;
	
	/** Used by many classes to get the <code>TextArea</code> */
	static JTextArea label;
	
	public PanelFinals() {
		super();
	}

	/* (non-Javadoc)
	 * @see gui.builder.TabsPanel#createButtons()
	 */
	@Override
	protected JButton[] createButtons() {
		JButton[] buttons = new JButton[3];
		buttons[0] = new JButton("Finals file");
		buttons[1] = new JButton("Assign places");
		buttons[2] = new JButton("List for professors");
		return buttons;
	}
	@Override
	protected JTextArea createLabel(int row, int col) {
		label = new JTextArea(row, col);
		return label;	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase("Finals file")) {
			String term = getOptionPane("Choose a month of the exam", false);
			
			String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
			String fileFinals = newterm + " final exam master list.xlsx";
			File file = new File(fileFinals);			
			if (file.exists()) {
				new Message("File " + fileFinals + " already exists");
				return;
			}
			
			try {
				new StudentsFinalInit(term);
			} catch (FileNotFoundException e1) {
				return;
			}
		}
		else if (command.equalsIgnoreCase("Assign places")) {
			
			String term = getOptionPane("Choose a month of the exam", false);
			if (term == null)
				return;
			
			String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
			final String fileFinals = newterm + " final exam master list.xlsx";
			
			File file = new File(fileFinals);
			if (! file.exists()) {
				new Message("File " + fileFinals + " doesn't exist");
				return;
			}
			label.append("-- Getting info from " + fileFinals + " file\n");
			label.paintImmediately(label.getVisibleRect());
			StudentsFinalSec sfs = new StudentsFinalSec(file);
			
			label.append("-- Allocating rooms\n");
			label.paintImmediately(label.getVisibleRect());
			try {
				sfs.addLocation();
			} catch (FileNotFoundException e1) {
				return;
			}
			
			label.append("-- Adding invigilators\n");
			label.paintImmediately(label.getVisibleRect());
			sfs.getInvigilators();
			
		}
		else if (command.equalsIgnoreCase("List for professors")){
			ArrayList<StudentFinal> list = StudentsFinalSec.getList();
			if (list.size() > 0) {  // the same session
				// nothing
			}
			else { // read from file
				String term = getOptionPane("Choose a month of the exam", false);
				String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
				final String fileFinals = newterm + " final exam master list.xlsx";
				
				File file = new File(fileFinals);
				if (! file.exists()) {
					new Message("File " + fileFinals + " doesn't exist");
					// exit?
				}
				label.append("-- Getting info from " + fileFinals + " file\n");
				label.paintImmediately(label.getVisibleRect());
				
				new StudentsFinalSec(file);
				list = StudentsFinalSec.getList();
			}
			
			wt = new WorkThread(list);
			wt.start();
			
			label.append("-- Choose an option and click the button\n");
			label.paintImmediately(label.getVisibleRect());
		}	
		else {
			// nothing
		}
	}
	/* Creates new dialog to choose a file according to the current term */
	private String getOptionPane(String s, boolean termname) {
		String termNow = null;	
		if (termname) 
			termNow = new Term().getTerm();
		else 	
			termNow = new Term().getMonth();
		
		String term = (String)JOptionPane.showInputDialog(
				null, "Choose the term:",
				s, JOptionPane.PLAIN_MESSAGE,
				null, null, termNow);
		return term;
	}
}