package swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.VirtualKeyboardControl;
import com.diy.software.listeners.KeyboardControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Constants;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.Screen;

public class KeyboardScreen extends Screen implements KeyboardControlListener {
	private VirtualKeyboardControl keyboardController;

	// A modified Keyboard.windowsQwertyLabels with a better layout
	private static final String[] MOD_WIN_QWERTY_LABELS = new String[] { /* Row 1 */ "FnLock Esc", "F1", "F2", "F3", "F4", "F5", "F6",
			"F7", "F8", "F9", "F10", "F11", "F12", "PrtSc", "Home", "End", "Insert", "Delete", /* Row 2 */ "` ~", "1 !", "2 @",
			"3 #", "4 $", "5 %", "6 ^", "7 &", "8 *", "9 (", "0 )", "- _", "= +", "Backspace", /* Row 3 */ "Tab", "Q",
			"W", "E", "R", "T", "Y", "U", "I", "O", "P", "[ {", "] }", "\\ |", /* Row 4 */ "CapsLock", "A", "S", "D",
			"F", "G", "H", "J", "K", "L", "; :", "' \"", "Enter", /* Row 5 */ "Shift (Left)", "Z", "X", "C", "V", "B",
			"N", "M", ", <", ". >", "/ ?", "Shift (Right)", "PgUp", "Up Arrow", "PgDn", /* Row 6 */ "Ctrl (Left)", "Fn", "Alt (Left)",
			"Spacebar", "Alt (Right)", "Windows", "Ctrl (Right)", "Left Arrow", "Down Arrow",
			"Right Arrow" };
	private static final String[] END_OF_ROW = new String[] {"Delete", "Backspace", "\\ |", "Enter", "PgDn", "Right Arrow"};
	private static final int NUM_ROWS = END_OF_ROW.length;
	private static final JPanel[] KEY_ROWS = new JPanel[NUM_ROWS];
	private static final int KEYBOARD_HEIGHT = 400;

	private Map<String, JButton> keyBtnMap = new HashMap<>();
	private JPanel keyboardContainer;
	private JTextField outputField;
	private JButton cancelBtn;

	public KeyboardScreen(StationControl sc) {
		super(sc);
		keyboardController = sc.getKeyboardControl();
		keyboardController.addListener(this);

		this.cancelBtn = this.makeCentralButton("CANCEL", 300, 70);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				systemControl.goBackOnUI();
			}
		});
		this.addLayer(cancelBtn, 0);

		addOutputField();
		addKeyboard();
	}

	private void addOutputField() {
		this.outputField = new JTextField();
		outputField.setEditable(false);
		outputField.getCaret().setVisible(true); // Making it non-editable also makes caret invisible...
		outputField.setBackground(Color.WHITE);
		outputField.setOpaque(true);
		outputField.setForeground(Color.BLACK);
		outputField.setFont(GUI_Fonts.TITLE);
		outputField.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH, 50));
		this.rootPanel.add(outputField, BorderLayout.NORTH);
	}

	private void addKeyboard() {
		this.keyboardContainer = new JPanel(new GridLayout(NUM_ROWS, 1, 0, 0));
		this.keyboardContainer.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH, KEYBOARD_HEIGHT));
		this.keyboardContainer.setOpaque(false);

		for (int i = 0; i < KEY_ROWS.length; i++) {
			this.keyboardContainer.add(makeKeyRow(i));
		}

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.weightx = 1;
		gc.weighty = 1;

		int curRow = 0;
		int curCol = 0;
		JPanel curKeyRow = KEY_ROWS[curRow];

		for (String key : MOD_WIN_QWERTY_LABELS) {
			gc.gridx = curCol;
			gc.gridy = curRow;

			if (key.equals("Backspace")) {
				curKeyRow.add(makeKeyButton(key, key, 320), gc);
			} else if (key.startsWith("Shift")) {
				curKeyRow.add(makeKeyButton("Shift", key, 158), gc);
			} else if (key.startsWith("Alt")) {
				curKeyRow.add(makeKeyButton("Alt", key, 60), gc);
			} else if (key.startsWith("Enter")) {
				curKeyRow.add(makeKeyButton(key, key, 300), gc);
			} else if (key.startsWith("Windows")) {
				GUI_JButton winBtn = makeKeyButton("⊞", key, 60);
				winBtn.setFont(GUI_Fonts.FRANKLIN_BOLD_BIG);
				curKeyRow.add(winBtn, gc);
			} else if (key.startsWith("Ctrl")) {
				curKeyRow.add(makeKeyButton("Ctrl", key, 120), gc);
			} else if (key.startsWith("CapsLock")) {
				curKeyRow.add(makeKeyButton(key, key, 145), gc);
			} else if (key.startsWith("Tab")) {
				curKeyRow.add(makeKeyButton(key, key, 130), gc);
			} else if (key.startsWith("\\")) { //Need to use escape sequence char
				curKeyRow.add(makeKeyButton("\\", key, 250), gc);
			} else if (key.startsWith("Left")) {
				curKeyRow.add(makeKeyButton("Left", key, 60), gc);
			} else if (key.equals("Spacebar")) {
				curKeyRow.add(makeKeyButton(key, key, 462), gc);
			} else {
				curKeyRow.add(makeKeyButton(key, key, 60), gc);
			}

			curCol++;
			// Check for end of current key row
			if (curRow < NUM_ROWS - 1 && key.equals(END_OF_ROW[curRow])) {
				curKeyRow = KEY_ROWS[++curRow];
				curCol = 0;
			}
		}

		this.rootPanel.add(keyboardContainer, BorderLayout.SOUTH);
	}

	private JPanel makeKeyRow(int row) {
		JPanel keyRow = new JPanel();
		keyRow.setOpaque(false);
		keyboardContainer.add(keyRow);
		KEY_ROWS[row] = keyRow;

		return keyRow;
	}

	private GUI_JButton makeKeyButton(final String label, String key, int width) {
		final GUI_JButton keyBtn = new GUI_JButton();
		keyBtn.setText(label.replace(" Arrow", "").replace("FnLock ", "")); // Remove unnecessary text
		keyBtn.setFont(GUI_Fonts.SMALL_BOLD);
		keyBtn.setPreferredSize(new Dimension(width, 60));
		keyBtn.setMargin(new Insets(0, 0, 0, 0));
		keyBtn.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 4));
		keyBtn.setActionCommand("KEY_PRESS: " + key);
		keyBtn.addActionListener(keyboardController);

		keyBtnMap.put(key, keyBtn);

		return keyBtn;
	}

	private void toggleKeyColor(JButton keyBtn) {
		// Swap background colors
		if (keyBtn.getBackground() == GUI_Color_Palette.DARK_BROWN) {
			keyBtn.setBackground(GUI_Color_Palette.DARKER_BROWN);
		} else {
			keyBtn.setBackground(GUI_Color_Palette.DARK_BROWN);
		}
	}
	
	// Returns the typed text
	public String getOutputText() {
		return this.outputField.getText();
	}

	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}

	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
		// Get button that corresponds with the key that was pressed
		JButton keyBtn = keyBtnMap.get(key);

		outputField.setText(text); // Update the text visual to reflect KeyController
		outputField.requestFocus(); // Required before setting caret
		outputField.setCaretPosition(pointerPosition); // Update cursor position

		if (key.equals("CapsLock") || key.startsWith("Shift")) {
			// Change button color to indicate whether it is pressed or not
			toggleKeyColor(keyBtn);
		}
	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String text) {

	}
}
