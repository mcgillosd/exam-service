/*
 * Login.java
 * 
 * Created on Jul 24, 2013 2:32:23 PM
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Create a frame to login, calls <code>WebConnect</code> to connect
 * 
 * @author Olga Tsibulevskaya
 *
 */
public class Login extends JPanel {
	
	/**  */
	private static final long serialVersionUID = 1L;
	
	/** Used by ActionListener */
	private JDialog dialog;
	private JTextField textUser;
	private JPasswordField textPass;
	
	private JTextArea labelMidterm = PanelMidterms.label;
	private JTextArea labelFinal = PanelFinals.label;
	
	/* to use for invigilators */
	private StudentsFinalSec sfs; 
	/** 
	 * Creates a dialog for Login option
	 * 
	 * @param update if <code>true</code> than update, 
	 * else download everything
	 */
	public Login(boolean update, boolean midterm) {
		dialog = new JDialog(AppFrame.frame, "Authentication");
		Font font = new Font("Georgia", Font.PLAIN, 20);
		
		JLabel labelLogin = new JLabel("Please login");
		labelLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelLogin.setFont(new Font("Georgia", Font.BOLD, 22));
			
		textUser = new JTextField();
		textPass = new JPasswordField();
		textUser.setFont(font);
		textUser.setMaximumSize(new Dimension(360, 30));
		textPass.setFont(font);
		textPass.setMaximumSize(new Dimension(360, 30));
		textUser.requestFocusInWindow();	
		
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.LINE_AXIS));
		panel_buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton[] buttons = new JButton[2];
		buttons[0] = new JButton("Login");
		buttons[1] = new JButton("Cancel");
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setMaximumSize(new Dimension(120, 36));
			buttons[i].setFont(font);
			panel_buttons.add(buttons[i]);
			if (i == 0)
				panel_buttons.add(Box.createRigidArea(new Dimension(40, 0)));
			buttons[i].addActionListener(new LoginActionListener(update, midterm)); 
		}
	
		dialog.getRootPane().setDefaultButton(buttons[0]);
			
		/* innerPanel for the main components */	
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,
				BoxLayout.Y_AXIS));
		
		innerPanel.add(Box.createRigidArea(new Dimension(0, 34)));
		innerPanel.add(labelLogin);
		innerPanel.add(Box.createRigidArea(new Dimension(0, 24)));
		innerPanel.add(textUser);
		innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		innerPanel.add(textPass);
		innerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		innerPanel.add(panel_buttons);
		
		/* main panel */
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());
		loginPanel.add(innerPanel, BorderLayout.CENTER);
		loginPanel.setOpaque(true);
		
		dialog.setSize(new Dimension(460, 270));
		dialog.setLocationRelativeTo(AppFrame.frame);
		dialog.setContentPane(loginPanel);
		dialog.setVisible(true);

	}
	public Login(StudentsFinalSec sfs) {
		this(false, false);
		this.sfs = sfs;
	}
	class LoginActionListener implements ActionListener {
		private boolean update;
		private boolean midterm;
		public LoginActionListener(boolean update, boolean midterm) {
			this.update = update;
			this.midterm = midterm;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Login")) {
				String user = textUser.getText();
				char[] password = textPass.getPassword();
				dialog.setVisible(false);
				dialog.dispose();
				WebConnect wc = new WebConnect(midterm);
				int result = wc.connect(user, password);
				if (result == 302) { 
					if (midterm) {
						labelMidterm.append("-- Authentication successful\n");
						labelMidterm.paintImmediately(labelMidterm.getVisibleRect());
						String html = wc.getContent();
						try {
							new StudentsMidtermInit(update).start(html);
						} catch (FileNotFoundException e1) {
							return;
						}
					}
					else {
						labelFinal.append("-- Authentication successful\n");
						labelFinal.paintImmediately(labelFinal.getVisibleRect());
						String html = wc.getInvigilatorsPage();
						sfs.addInvigilators(html);
					}
			    }
				else {
					JTextArea label;
					if (midterm)
						label = labelMidterm;
					else
						label = labelFinal;
					label.append("-- Authentication failed\n");
					label.paintImmediately(label.getVisibleRect());
					new Login(update, midterm);
			    }
			}
			else {
				dialog.setVisible(false);
				dialog.dispose();
			}
		}
	}
}
