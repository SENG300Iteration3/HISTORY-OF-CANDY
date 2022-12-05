package swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.VirtualKeyboardControl;
import com.diy.software.listeners.KeyboardControlListener;
import com.jimmyselectronics.nightingale.Keyboard;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Constants;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.Screen;

public class KeyboardScreen extends Screen implements KeyboardControlListener {
	public static final String[] END_OF_ROW = new String[] { "Delete", "Backspace", "\\ |", "Enter", "Shift (Right)",
			"Right Arrow" };
	public static final int NUM_ROWS = END_OF_ROW.length;

	private VirtualKeyboardControl keyboardController;
	private Map<String, JButton> keyBtnMap = new HashMap<>();
	private JPanel keyboardContainer;
	private JTextField queryField;

	public KeyboardScreen(StationControl sc) {
		super(sc);
		keyboardController = sc.getKeyboardControl();
		keyboardController.addListener(this);

		this.keyboardContainer = new JPanel(new GridLayout(0, 1, 0, 0));
		this.keyboardContainer.setOpaque(false);
		this.rootPanel.add(keyboardContainer, BorderLayout.SOUTH);

		createOutputLabel();

		int curRow = 0;
		int curCol = 0;
		JPanel keyRow = makeKeyRow(curRow, curCol);
		for (String k : Keyboard.WINDOWS_QWERTY) {
			keyRow.add(makeKeyButton(k));
			curCol++;
			// Check for end of current key row
			if (curRow < NUM_ROWS - 1 && k.equals(END_OF_ROW[curRow])) {
				keyRow = makeKeyRow(curRow, curCol);
				curRow++;
				curCol = 0;
			}
		}
	}

	public void createOutputLabel() {
		this.queryField = new JTextField();
		queryField.setEditable(false);
		queryField.getCaret().setVisible(true); // making it non-editable also disables the caret...
		queryField.setBackground(Color.WHITE);
		queryField.setOpaque(true);
		queryField.setForeground(Color.BLACK);
		queryField.setFont(GUI_Fonts.FRANKLIN_BOLD);
		queryField.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH - 100, 30));
		this.rootPanel.add(queryField, BorderLayout.NORTH);
	}

	public JPanel makeKeyRow(int row, int col) {
		JPanel rowContainer = new JPanel(new GridLayout(1, 0, 0, 0));

		rowContainer.setOpaque(false);
		this.keyboardContainer.add(rowContainer);

		return rowContainer;
	}

	public GUI_JButton makeKeyButton(final String key) {
		final GUI_JButton keyBtn = new GUI_JButton();
		keyBtn.setText(key.replace(" Arrow", "").replace("FnLock ", ""));
		keyBtn.setFont(GUI_Fonts.SMALL_BOLD);
		keyBtn.setPreferredSize(new Dimension(60, 60));
		keyBtn.setMargin(new Insets(0, 0, 0, 0));
		keyBtn.setActionCommand("KEY_PRESS: " + key);
		keyBtn.addActionListener(keyboardController);

		keyBtnMap.put(key, keyBtn);

		return keyBtn;
	}

	private void toggleKeyColor(JButton keyBtn) {
		if (keyBtn.getBackground() == GUI_Color_Palette.DARK_BROWN) {
			keyBtn.setBackground(GUI_Color_Palette.DARKER_BROWN);
		} else {
			keyBtn.setBackground(GUI_Color_Palette.DARK_BROWN);
		}
	}

	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}

	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
		JButton keyBtn = keyBtnMap.get(key);
		
		queryField.setText(text); // Update the text visual to reflect KeyController
		queryField.requestFocus(); // Required before setting caret
	    queryField.setCaretPosition(pointerPosition); // Update cursor position

		if (key.equals("CapsLock")) {
			toggleKeyColor(keyBtn);
		} else if (key.startsWith("Shift")) {
			toggleKeyColor(keyBtn);
		}
	}

	@Override
	public void awaitingKeyboardInput(KeyboardControl kc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String text) {

	}
}
