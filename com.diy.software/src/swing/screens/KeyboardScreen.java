package swing.screens;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import com.diy.software.controllers.StationControl;
import com.jimmyselectronics.nightingale.Keyboard;

import swing.styling.GUI_JButton;
import swing.styling.Screen;

public class KeyboardScreen extends Screen {

	List<String> keys = Keyboard.WINDOWS_QWERTY;
	String[] endOfRow = new String[] { "Delete", "Backspace", "\\ |", "Enter", "Shift (Right)", "Down Arrow" };
	
	public KeyboardScreen(StationControl systemControl) {
		super(systemControl);
		
		GUI_JButton keyBtn;
		int curRow = 0;
		for (String k : keys) {
			keyBtn = makeKeyButton(k, curRow, 2, 1);
			//System.out.println(k + " " + curRow);
			if (curRow < endOfRow.length-1 && k.equals(endOfRow[curRow])) {
				curRow++;
			}
		}
	}
	
	public GUI_JButton makeKeyButton(String text, int row, int keyLength, int keyHeight) {
		GUI_JButton keyBtn = new GUI_JButton(text);
		
		keyBtn.setText(text);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = row;
		gc.insets = new Insets(10, 0, 0, 0);

		this.centralPanel.add(keyBtn, gc);
		
		return keyBtn;
	}
	
	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}
}
