/*
 * GUIFrame.java
 * 
 * Created on 2013-06-11 10:43:33 AM
 */
import javax.swing.JFrame;

/**
 * Creates a frame to build a graphical user interface
 * 
 * @author Olga Tsibulevskaya
 */
public class GUIFrame {
	
	public GUIFrame() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	private static void createAndShowGUI() {
		JFrame frame = new JFrame("OSD ExamsService");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,460);

		GUIPanel newContentPane = new GUIPanel(frame);
		newContentPane.setOpaque(true); 
		frame.setContentPane(newContentPane);

		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
