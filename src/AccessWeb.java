/* 
 * AccessWeb.java
 * 
 * Created on 2013-06-11 11:21:09 AM
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Connects to the web page and gets the data from it.
 * Called by the <code>StudentsList</code> class, when the event is done 
 * (successful login), will call <code>StudentsList.start()</code>
 * 
 * @author Olga Tsibulevskaya
 */
public class AccessWeb {

	private String html;
	private JFrame frame;
	private JLabel label;
	private final JDialog dialog;
	private final StudentsMidterm sd;
	
	/**
	 * Creates a new panel to login, sets <code>StudentsMidterm</code> data
	 * to be used for calling after ActionListeners events
	 * 
	 * @param frame	To create new panels (ex. login panel)
	 * @param label	To print the status of the current operation
	 * @param sd	<code>StudentsList</code> will be used to call them 
	 * 				back after an ActionListener event
	 */
	public AccessWeb(JFrame frame, JLabel label, StudentsMidterm sd) {
		this.label = label;
		label.setText("Please login");
		this.frame = frame;
		dialog = new JDialog(frame, "Authentication");
		this.sd = sd;	
		JPanel login = login();
			
		dialog.setSize(new Dimension(400, 220));
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
		dialog.setContentPane(login);
	}
	/**
	 * Creates a login panel to authenticate users
	 * @return login panel
	 */
	public JPanel login() {
		JPanel loginPanel = new JPanel();
		Font font = new Font("Arial", Font.BOLD, 16);
		
		JLabel labelLogin = new JLabel("<html><br><p align=center>"
				+ "Please login:<br></p></html>");
		labelLogin.setHorizontalAlignment(JLabel.CENTER);
		labelLogin.setFont(font);
			
		final JTextField textUser = new JTextField(22);
		final JPasswordField textPass = new JPasswordField(22);
		textUser.setFont(font);
		textPass.setFont(font);
		
		
		JButton loginButton = new JButton("Login");
		loginButton.setFont(font);
		loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JRootPane rootPane = frame.getRootPane();
		rootPane.setDefaultButton(loginButton);
		
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = textUser.getText();
				char[] pass = textPass.getPassword();
				dialog.setVisible(false);
				dialog.dispose();
				try {
					html = connect(user, pass);
					sd.start(html);
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		// innerPanel for the main components	
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,
				BoxLayout.PAGE_AXIS));
			
		innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		innerPanel.add(textUser);
		innerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		innerPanel.add(textPass);
		innerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		innerPanel.add(loginButton);
		
		loginPanel.add(labelLogin, BorderLayout.CENTER);
		loginPanel.add(innerPanel, BorderLayout.PAGE_END);
		loginPanel.setOpaque(true);
		return loginPanel;
	}
	/**
	 * Connects to the web page
	 * 
	 * @param user
	 * @param password
	 * @return A <code>String</code> with content of the page
	 * @throws IOException
	 */
	public String connect(String user, char[] password) throws IOException {
		label.setText("Authentication...");
		label.paintImmediately(label.getVisibleRect());
		
        HttpClient httpclient = new HttpClient();
        httpclient.getHttpConnectionManager().
                getParams().setConnectionTimeout(30000);
             
        final String url = "https://www.mcgill.ca/osd/node/169/webform-results/table?results=0";
        
        PostMethod httppost = new PostMethod(url);
       
        // login
        httppost.addParameter("name", user);
        String pass = new String(password);
        httppost.addParameter("pass", pass);
        httppost.addParameter("form_build_id", "form-4aca174bc2e44ec94fd77a29c1e6ba65");
        httppost.addParameter("form_id", "user_login");
        
        int result = 0;
        try {
            result = httpclient.executeMethod(httppost);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        if (result == 302) { 
        	label.setText("Authentication successful");
        	label.paintImmediately(label.getVisibleRect());
        }
        else {
        	label.setText("Authentication failed");
        	label.paintImmediately(label.getVisibleRect());
        }
        httppost.releaseConnection();
 
        // Now we've got cookies, go to the page
 
        GetMethod httpget = new GetMethod(url);
        try {
           	result = httpclient.executeMethod(httpget);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
         
        if (result == 200) {
        	label.setText("Downloading data from the database...");
        	label.paintImmediately(label.getVisibleRect());
        }
        else
        	label.setText("Connection failed");
         
        InputStream in = httpget.getResponseBodyAsStream();
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        html = s.hasNext() ? s.next() : "";

        httpget.releaseConnection();
		return html;		 
	}
	
	public String getHtml() {
		return html;
	}
}

