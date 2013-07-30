/*
 * PanelTabs.java
 * 
 * Created on Jul 23, 2013 12:34:24 PM
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * Abstract class to populate <code>JTabbedPane</code>
 * 
 * @author Olga Tsibulevskaya
 */
public abstract class PanelTabs extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	public PanelTabs() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(createLeftPanel());
		add(createRightPanel());
	}
	
	protected JPanel createLeftPanel() {
		JPanel pane = new JPanel(); 
		pane.setLayout(new BorderLayout());	
		
		JPanel panel = new JPanel();
		pane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				
		JButton[] buttons = createButtons(); 
				
		Font font = new Font("Georgia", Font.PLAIN, 26);
			
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setMaximumSize(new Dimension(280, 50));
			buttons[i].setFont(font);
			buttons[i].setAlignmentX(Component.RIGHT_ALIGNMENT);
			buttons[i].add(Box.createHorizontalGlue());
			buttons[i].addActionListener(this);
			if (i == 0) {
				if (buttons.length == 3) 
					panel.add(Box.createRigidArea(new Dimension(0,100)));
				else
					panel.add(Box.createRigidArea(new Dimension(0,140)));
			}
			else
				panel.add(Box.createRigidArea(new Dimension(0,30)));
			panel.add(buttons[i]);
			
		}
		pane.add(panel, BorderLayout.CENTER);
		return pane;
	}
	protected abstract JButton[] createButtons();
	protected abstract JTextArea createLabel(int row, int col);		
	public abstract void actionPerformed(ActionEvent e);
	
	protected JPanel createRightPanel() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		final JTextArea label = createLabel(8, 20);
		setLabelProperties(label);
		
		final JScrollPane scroll = new JScrollPane(label);
		scroll.setMaximumSize(new Dimension(500, 260));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		panel.add(Box.createRigidArea(new Dimension(0,80)));
		panel.add(scroll);
		
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.LINE_AXIS));
		panel_buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Font font_text = new Font("Georgia", Font.PLAIN, 20);
		
		JButton[] buttons = new JButton[2];
		buttons[0] = new JButton("Clear");
		buttons[1] = new JButton("Exit");
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setMaximumSize(new Dimension(120, 40));
			buttons[i].setFont(font_text);
			panel_buttons.add(buttons[i]);
			if (i == 0)
				panel_buttons.add(Box.createRigidArea(new Dimension(40, 0)));
			buttons[i].addActionListener(new TextActionListener(label)); 
		}
			
		panel.add(Box.createRigidArea(new Dimension(0,20)));
		panel.add(panel_buttons);
	
		pane.add(panel, BorderLayout.CENTER);
		return pane;
	}
	
	private void setLabelProperties(JTextArea label) {
		label.setText("-- Choose an option and click the button\n");
		Font font_text = new Font("Georgia", Font.PLAIN, 20);
		label.setFont(font_text);
		label.setBackground(new Color(213, 209, 206));
		label.setOpaque(true);
		label.setEditable(false); 
				
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		label.setBorder(loweredetched);
		
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
	}
	
	class TextActionListener implements ActionListener {
		private JTextArea label;
		public TextActionListener(JTextArea label) {
			this.label = label;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Clear")) {
				label.setText("-- Choose an option and click the button\n");
			}
			else {
				System.exit(0);
			}
		}
	}
}