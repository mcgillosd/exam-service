/*
 * PanelMidterms.java
 * 
 * Created on Jul 24, 2013 11:19:37 AM
 */

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * Creates a panel for Midterms
 * 
 * @see PanelTabs
 * @author Olga Tsibulevskaya
 */
public class PanelMidterms extends PanelTabs {
	
	private static final long serialVersionUID = 1L;
	
	/** Used by many classes to get the <code>TextArea</code> */
	static JTextArea label;
	
	public PanelMidterms() {
		super();
	}
	/* (non-Javadoc)
	 * @see gui.builder.TabsPanel#createButtons()
	 */
	@Override
	protected JButton[] createButtons() {
		JButton[] buttons = new JButton[3];
		buttons[0] = new JButton("Update");
		buttons[1] = new JButton("Show the last ID");
		buttons[2] = new JButton("Download");
		return buttons;
	}
	/* (non-Javadoc)
	 * @see gui.builder.TabsPanel#createLabel()
	 */
	@Override
	protected JTextArea createLabel(int row, int col) {	
		label = new JTextArea(row, col);
		return label;	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase("Update")) {
			new Login(true, true);
		}
		else if (command.equalsIgnoreCase("Show the last ID")) {
			int lastid = StudentsMidtermInit.id;
			if (lastid == 0) {
				try {
					lastid = new LastID().getID();
				} catch (FileNotFoundException e1) {
					StringBuilder sb = new StringBuilder();
					for (StackTraceElement element : e1.getStackTrace()) {
						sb.append(element.toString());
						sb.append("\n");
					}
					new Log(sb.toString());
					return;
				}
			}
			label.append("-- The last ID is " + lastid + "\n");
		}
		else if (command.equalsIgnoreCase("Download")) {
			new Login(false, true);
		}
		else {
			// nothing
		}
	}
}