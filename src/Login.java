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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

	/** 
	 * Creates a dialog for Login option
	 * 
	 * @param update if <code>true</code> than update, 
	 * else download everything
	 */
	public Login(boolean update) {
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
			buttons[i].addActionListener(new LoginActionListener(update)); 
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
	class LoginActionListener implements ActionListener {
		private boolean update;
		public LoginActionListener(boolean update) {
			this.update = update;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Login")) {
				String user = textUser.getText();
				char[] password = textPass.getPassword();
				dialog.setVisible(false);
				dialog.dispose();
				String html = new WebConnect().connect(user, password);
				new StudentsMidtermInit(update).start(html);
			}
			else {
				dialog.setVisible(false);
				dialog.dispose();
			}
		}
	}
}
