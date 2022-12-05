package swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.VirtualKeyboardControl;
import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.util.MathUtils;
import com.jimmyselectronics.nightingale.Keyboard;

import swing.styling.GUI_Constants;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class KeyboardScreen extends Screen implements KeyboardControlListener {
	private VirtualKeyboardControl keyboardController;

	public static final String[] END_OF_ROW = new String[] {"Delete", "Backspace", "\\ |", "Enter", "Shift (Right)", "PgDn"};
	public static final int NUM_ROWS = END_OF_ROW.length;
	
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
		GUI_JButton keyBtn;
		JPanel rowContainer = addKeyRow(curRow, curCol);
		for (String k : Keyboard.WINDOWS_QWERTY) {
			k = k.replace(" Arrow", "").replace("FnLock ", "");
			keyBtn = addKeyButton(k);
			rowContainer.add(keyBtn);
			//System.out.println(k + " " + curRow + " " + curCol);
			curCol++;
			if (curRow < NUM_ROWS-1 && k.equals(END_OF_ROW[curRow])) {
				rowContainer = addKeyRow(curRow, curCol);
				curRow++;
				curCol = 0;
			}
		}
	}
	
	public void createOutputLabel() {
		this.queryField = new JTextField();
		queryField.setEditable(false);
		queryField.getCaret().setVisible(true); //making it non-editable also disables the caret...
		queryField.setBackground(Color.WHITE);
		queryField.setOpaque(true);
		queryField.setForeground(Color.BLACK);
		queryField.setFont(GUI_Fonts.FRANKLIN_BOLD);
		queryField.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH - 100, 30));
		this.rootPanel.add(queryField, BorderLayout.NORTH);
	}
	
	public JPanel addKeyRow(int row, int col) {
		JPanel rowContainer = new JPanel(new GridLayout(1,0,0,0));
		
		rowContainer.setOpaque(false);
		this.keyboardContainer.add(rowContainer);
		
		return rowContainer;
	}
	
	public GUI_JButton addKeyButton(final String key) {
		final GUI_JButton keyBtn = new GUI_JButton(key);
		
		keyBtn.setFont(GUI_Fonts.SMALL_BOLD);
		keyBtn.setPreferredSize(new Dimension(60,60));
		keyBtn.setMargin(new Insets(0, 0, 0, 0));
		keyBtn.setActionCommand("KEY_PRESS: " + key);
		keyBtn.addActionListener(keyboardController);

		return keyBtn;
	}
	
	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}

	@Override
	public void awaitingKeyboardInput(KeyboardControl kc) {
		// TODO Auto-generated method stub
		
	}
	
	private void moveCaret(int delta) {
		queryField.requestFocus();
		int newPos = MathUtils.clamp(queryField.getCaretPosition()+delta,
				0, queryField.getText().length());
		queryField.setCaretPosition(newPos);
	}
	
	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String key) {
		System.out.println(key);
		if (key.length() == 1) {
			// Alphanumeric key
			queryField.setText(queryField.getText() + key);
		} else if (key.startsWith("Right")) {
			moveCaret(1);
		} else if (key.startsWith("Left")) {
			moveCaret(-1);
		} else if (key.equals("Backspace")) {
			
		} else if (key.equals("Delete")) {
			
		} else if (key.equals("Enter")) {
			
		}
	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String query) {
		// TODO Auto-generated method stub
	}
}
