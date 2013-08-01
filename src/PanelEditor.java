/*
 * Created on Jul 24, 2013 11:58:51 AM
 */

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * Creates a panel for Editor
 * 
 * @author Olga Tsibulevskaya
 */
public class PanelEditor extends PanelTabs {

	private static final long serialVersionUID = 1L;
	
	static JTextArea label;	
	
	public PanelEditor() {
		super();
	}

	/* (non-Javadoc)
	 * @see gui.builder.TabsPanel#createButtons()
	 */
	@Override
	protected JButton[] createButtons() {
		JButton[] buttons = new JButton[2];
		buttons[0] = new JButton("Add empty rows");
		buttons[1] = new JButton("Remove empty rows");
		return buttons;
	}
	@Override
	protected JTextArea createLabel(int row, int col) {
		label = new JTextArea(row, col);
		return label;	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Add empty rows")) {
			new Combo(true);
		}
		else {
			new Combo(false);
		}
	}
}