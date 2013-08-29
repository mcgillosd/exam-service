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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
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
	int width;
	
	public PanelTabs() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)d.getWidth();
		
		int width2 = width;
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge =
	   		GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int j = 0; j < gs.length; j++) {
	   		GraphicsDevice gd = gs[j];
	   		GraphicsConfiguration[] gc = gd.getConfigurations();
	   		for (int i = 0; i < gc.length; i++) {
	   			virtualBounds = virtualBounds.union(gc[i].getBounds());
	   			if (j == 0) {
	      		  width2 = virtualBounds.width;
	      		  break;
	   			}
	   			
	   		}
	   		
		}
		if (gs.length > 1) {
			width -= width2;
		}
		add(createLeftPanel());
		add(createRightPanel());
		Box.createRigidArea(new Dimension((int)(width*0.035), 0));
	}
	
	protected JPanel createLeftPanel() {
		JPanel pane = new JPanel(); 
		pane.setLayout(new BorderLayout());	
		
		JPanel panel = new JPanel();
		pane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				
		JButton[] buttons = createButtons(); 
				
		Font font = new Font("Georgia", Font.PLAIN, 26);
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int)d.getHeight();
		
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setMaximumSize(new Dimension(280, (int)(height*0.06)));
			buttons[i].setFont(font);
			buttons[i].setAlignmentX(Component.RIGHT_ALIGNMENT);
			buttons[i].add(Box.createHorizontalGlue());
			buttons[i].addActionListener(this);
			if (i == 0) {
				if (buttons.length == 3) 
					panel.add(Box.createRigidArea(new Dimension(0,(int)(height*0.14)))); //100
				else
					panel.add(Box.createRigidArea(new Dimension(0,(int)(height*0.19)))); //140
			}
			else
				panel.add(Box.createRigidArea(new Dimension(0,(int)(height*0.04))));
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
		
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int)d.getHeight();
		
		final JScrollPane scroll = new JScrollPane(label);
		scroll.setMaximumSize(new Dimension((int)(width*0.4), (int)(height*0.36)));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		panel.add(Box.createRigidArea(new Dimension(0,(int)(height*0.1)))); //
		panel.add(scroll);
		
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.LINE_AXIS));
		panel_buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Font font_text = new Font("Georgia", Font.PLAIN, 20);
		
		JButton[] buttons = new JButton[3];
		buttons[0] = new JButton("Clear");
		buttons[1] = new JButton("Stop");
		buttons[2] = new JButton("Exit");
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setMaximumSize(new Dimension(120, 40));
			buttons[i].setFont(font_text);
			panel_buttons.add(buttons[i]);
			if (i < 2)
				panel_buttons.add(Box.createRigidArea(new Dimension(30, 0)));
			buttons[i].addActionListener(new TextActionListener(label)); 
		}
		panel.add(Box.createRigidArea(new Dimension(0,20)));
		panel.add(panel_buttons);
	
		pane.add(panel, BorderLayout.CENTER);
		return pane;
	}
	
	private void setLabelProperties(JTextArea label) {
		label.setText("-- Choose an option and click the button\n");
		Font font_text = new Font("Georgia", Font.PLAIN, 18);
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
			else if (e.getActionCommand().equalsIgnoreCase("Stop")) {
				Worker w = PanelFinals.w;
				if (w != null)
					w.stop();
			}
			else {
				System.exit(0);
			}
		}
	}
}