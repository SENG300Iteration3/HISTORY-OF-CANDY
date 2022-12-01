package swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.diy.software.controllers.StationControl;
import com.diy.software.util.StringOps;
import com.jimmyselectronics.nightingale.Keyboard;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Constants;
import swing.styling.Screen;

public class KeyboardScreen extends Screen {

	private static final List<String> KEYS = Keyboard.WINDOWS_QWERTY;
	private static final String[] END_OF_ROW = new String[] { "Delete", "Backspace", "\\ |", "Enter", "Shift (Right)", "Down Arrow"};
	
	private GUI_JLabel outputText;
	private boolean capsLockOn = false;
	
	public KeyboardScreen(StationControl systemControl) {
		super(systemControl);
		
		createOutputLabel();
		
		int curRow = 0;
		int curCol = 0;
		for (String k : KEYS) {
			addKeyButton(k, curRow, curCol, 1, 1);
			//System.out.println(k + " " + curRow);
			curCol++;
			if (curRow < END_OF_ROW.length-1 && k.equals(END_OF_ROW[curRow])) {
				curRow++;
				curCol = 0;
			}
		}
		
//		int curRow = 0;
//		int curCol = 0;
//		for (String k : KEYS) {
//			double keyWidth = 1;
//			int keyHeight = 1;
//			if (curRow == 0) {
//				if (k.equals("Esc")) {
//					keyWidth = 2;
//				}
//				addKeyButton(k, curRow, curCol, 1, 1);
//			}
//
//			curCol++;
//			if (curRow < END_OF_ROW.length-1 && k.equals(END_OF_ROW[curRow])) {
//				curRow++;
//				curCol = 0;
//			}
//		}
	}
	
	public void createOutputLabel() {
		this.outputText = new GUI_JLabel();
		outputText.setBackground(Color.WHITE);
		outputText.setOpaque(true);
		outputText.setForeground(Color.BLACK);
		outputText.setFont(GUI_Fonts.FRANKLIN_BOLD);
		outputText.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH - 100, 30));
		this.rootPanel.add(outputText, BorderLayout.NORTH);
	}
	
	public void addKeyButton(final String key, int row, int col, int keyWidth, int keyHeight) {
		final GUI_JButton keyBtn = new GUI_JButton(key);
		
		keyBtn.setFont(GUI_Fonts.SMALL_TEXT);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = row;
		gc.gridx = col;
		gc.gridwidth = keyWidth;
		gc.gridheight = keyHeight;
		gc.anchor = (col == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
	    gc.fill = (col == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(10, 0, 0, 0);
		
		// Alphanumeric key
		if (key.length() == 1) {
			keyBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String next = capsLockOn ? key.toUpperCase() : key.toLowerCase();
					outputText.setText(outputText.getText() + next);
				}
			});
		} else {
			switch (key) {
				case "Backspace":
					keyBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							outputText.setText(StringOps.removeLastChar(outputText.getText()));
						}
					});
					break;
				case "Delete":
					keyBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							outputText.setText("");
						}
					});
					break;
				case "Spacebar":
					keyBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							outputText.setText(outputText.getText() + " ");
						}
					});
					break;
				case "CapsLock":
					keyBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (capsLockOn) {
								capsLockOn = false;
								keyBtn.setBackground(GUI_Color_Palette.DARK_BROWN);
							} else {
								capsLockOn = true;
								keyBtn.setBackground(Color.GREEN);
							}
						}
					});
					break;
				default:
					break;
			}
		}
		
		this.centralPanel.add(keyBtn, gc);
	}
	
	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}
}
