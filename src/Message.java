/*
 * Message.java
 * 
 * Created on 2013-06-11 12:11:26 PM
 */
import javax.swing.JOptionPane;

/**
 * Creates a message with some information
 * 
 * @author Olga Tsibulevskaya
 */
public class Message {

	public Message(String message) {
		JOptionPane.showMessageDialog(null, message, "Message", 
				JOptionPane.INFORMATION_MESSAGE);
	}
}
