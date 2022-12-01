package swing.screens;

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
			if (curRow < endOfRow.length-1 && k.equals(endOfRow[curRow])) {
				curRow++;
			}
			keyBtn = makeKeyButton(k, 2, 1);
			System.out.println(curRow);
		}
	}
	
	public GUI_JButton makeKeyButton(String text, int keyLength, int keyHeight) {
		GUI_JButton keyBtn = new GUI_JButton(text);
		
		keyBtn.setText(text);
		
		return keyBtn;
	}
	
	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}
}
