/* 
 * AppFrame.java
 * 
 * Created on Jul 30, 2013 12:58:34 AM
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import java.io.File;
import java.io.IOException;

/**
 * Creates the main frame of the application
 * 
 * @author Olga Tsibulevskaya
 */
public class AppFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/** used by other classes to create <code>Dialog</code> */
	static AppFrame frame;

	/**
	 * Creates <code>ContentPane</code> which consists 
	 * of two main panels: top panel for the label and 
	 * central panel for the rest (Title and <code>TabbedPane</code>)
	 */
	public AppFrame() {
		frame = this;
		setTitle("OSD Exam Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Color.WHITE);
				
		JPanel panel = createTop();
		contentPane.add(panel, BorderLayout.NORTH);
		JPanel panel_main = createMain();
		contentPane.add(panel_main, BorderLayout.CENTER);
		
		setContentPane(contentPane);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
				
	}
	private JPanel createTop() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		Border paddingBorder = BorderFactory.createEmptyBorder(0,100,0,0);
		BufferedImage myPicture;
		try {
			//myPicture = ImageIO.read(new File("img/mcgill_logo.gif"));
			
			myPicture = ImageIO.read(getClass().getResource("/img/mcgill_logo.gif"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			picLabel.setBorder(paddingBorder);
			panel.add(picLabel);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return panel;
	}
		
	private JPanel createMain() {	
		JPanel panel_main = new JPanel();
		panel_main.setBackground(Color.WHITE);
		panel_main.setLayout(new BorderLayout());
		
		String label = "<html><center>OSD Exam Management</center></html>";
		JLabel label_name = new JLabel(label);
	
		Color red = new Color(219, 36, 30);
		
		label_name.setForeground(red);
		Font font = new Font("Georgia", Font.PLAIN, 72);
		label_name.setFont(font);
		
		label_name.setHorizontalAlignment(SwingConstants.CENTER);
		Border paddingBorder = BorderFactory.createEmptyBorder(16,0,40,0);
		label_name.setBorder(paddingBorder);
		panel_main.add(label_name, BorderLayout.NORTH);
		
		/*---------------------------end of the title---------------------------*/
		/*-----------------------------tabbedPane-------------------------------*/
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		panel_main.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("<html><body marginwidth=50 marginheight=5 style=\"font-family:Georgia\">"
				+ "<font size=\"6\">Help</font></body></html>", null, panel_1, null);
		
		JPanel panel_2 = new PanelEditor();
		tabbedPane.addTab("<html><body leftmargin=24 topmargin=8 marginwidth=50 marginheight=5 style=\"font-family:Georgia\">"
				+ "<font size='6'><font family='Serif'>Editor</font></font></body></html>", null, panel_2, null);
		
		JPanel panel_3 = new PanelFinals();
		tabbedPane.addTab("<html><body leftmargin=24 topmargin=8 marginwidth=50 marginheight=5 style=\"font-family:Georgia\">"
				+ "<font size='6'><font family='Serif'>Finals</font></font></body></html>", null, panel_3, null);
		
		JPanel panel_4 = new PanelMidterms();
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=40 marginheight=5 style=\"font-family:Georgia\">"
				+ "<font size='6'>Midterms</font></body></html>", null, panel_4, null);
		
		tabbedPane.setSelectedIndex(3); 
			
		/*-----------------------------end of tabbedPane---------------------------*/
		return panel_main;	
	}	
}
