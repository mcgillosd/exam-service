/*
 * Combo.java
 * 
 * Created on Aug 1, 2013 12:21:38 PM
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Used by the Editor to get info about files names (term) and 
 * type of the exam (Midterm, Final)
 * 
 * @author Olga Tsibulevskaya
 * @see JPanel
 */
public class Combo extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JComboBox type;
	private JComboBox name;
	private boolean add;
	
	public Combo(boolean add) {
		dialog = new JDialog(AppFrame.frame, "Choose terms for the file");
		this.add = add;
		Font font = new Font("Georgia", Font.PLAIN, 20);
		
		JLabel labelType = new JLabel("Exam type:");
		labelType.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelType.setFont(new Font("Georgia", Font.BOLD, 22));
		
		JLabel labelTerm = new JLabel("Exam term:");
		labelTerm.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelTerm.setFont(new Font("Georgia", Font.BOLD, 22));
		
		String[] exams = { "Midterm", "Final" };
		type = new JComboBox(exams);
		type.setFont(font);
		type.setMaximumSize(new Dimension(360, 30));
		
		
		Term now = new Term();
		String[] files = {now.getTerm(), now.termNext().getTerm(), now.termNext().termNext().getTerm()};
		
		name = new JComboBox(files);
		name.setFont(font);
		name.setMaximumSize(new Dimension(360, 30));
		name.setEditable(true);
		
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.LINE_AXIS));
		panel_buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton[] buttons = new JButton[2];
		buttons[0] = new JButton("Submit");
		buttons[1] = new JButton("Cancel");
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setMaximumSize(new Dimension(120, 36));
			buttons[i].setFont(font);
			panel_buttons.add(buttons[i]);
			if (i == 0)
				panel_buttons.add(Box.createRigidArea(new Dimension(40, 0)));
			buttons[i].addActionListener(new ComboActionListener()); 
		}
		
		dialog.getRootPane().setDefaultButton(buttons[0]);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(Box.createRigidArea(new Dimension(0, 34)));
		panel.add(labelType);
		panel.add(Box.createRigidArea(new Dimension(0, 12)));
		panel.add(type);
		panel.add(Box.createRigidArea(new Dimension(0, 16)));
		panel.add(labelTerm);
		panel.add(Box.createRigidArea(new Dimension(0, 12)));
		panel.add(name);
		panel.add(Box.createRigidArea(new Dimension(0, 30)));
		panel.add(panel_buttons);
		
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new BorderLayout());
		comboPanel.add(panel, BorderLayout.CENTER);
		comboPanel.setOpaque(true);
		
		dialog.setSize(new Dimension(460, 330));
		dialog.setLocationRelativeTo(AppFrame.frame);
		dialog.setContentPane(comboPanel);
		dialog.setVisible(true);
	}
	
	class ComboActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Submit")) {
				String exam = (String)type.getSelectedItem();
				String term = (String)name.getSelectedItem();
				dialog.setVisible(false);
				dialog.dispose();
				if (add)
					new Excel().addEmptyRows(exam, term);
				
				else
					new Excel().removeEmptyRows(exam, term, true);
				
			}
			else {
				dialog.setVisible(false);
				dialog.dispose();
			}
		}
	}
}
