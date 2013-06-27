/*
 * ExamsApp.java
 * 
 * Created on 2013-06-11 10:42:08 AM
 */
import java.io.IOException;

/**
 * The main class
 *
 * @author Olga Tsibulevskaya
 */
public class ExamsApp {

	public static void main(String[] args) throws IOException { // TODO: handle the exception
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new GUIFrame();
				}
			});
	}
}
