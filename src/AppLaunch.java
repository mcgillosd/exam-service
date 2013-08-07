/*
 * AppLaunch.java
 * 
 * Created on 2013-06-11 10:42:08 AM
 */
import java.awt.EventQueue;

import javax.swing.UIManager;

/**
 * Launches the application.
 *
 * @author Olga Tsibulevskaya
 */
public class AppLaunch {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new AppFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}