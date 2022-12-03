package swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

import com.diy.software.controllers.StationControl;
import com.diy.software.util.StringOps;
import com.jimmyselectronics.nightingale.Keyboard;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Constants;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class KeyboardScreen extends Screen {

	public static final List<String> KEYS = Keyboard.WINDOWS_QWERTY;
	public static final String[] END_OF_ROW = new String[] { "Delete", "Backspace", "\\ |", "Enter", "Shift (Right)", "PgDn"};
	public static final int NUM_ROWS = END_OF_ROW.length;
	
	private JPanel keyboardContainer;
	private GUI_JLabel outputText;
	private boolean capsLockOn = false;
	
	public KeyboardScreen(StationControl systemControl) {
		super(systemControl);
		this.keyboardContainer = new JPanel(new GridLayout(0, 1, 0, 0));
		this.rootPanel.add(keyboardContainer, BorderLayout.SOUTH);
		
		createOutputLabel();
		
		int curRow = 0;
		int curCol = 0;
		GUI_JButton keyBtn;
		JPanel rowContainer = addKeyRow(curRow);
		for (String k : KEYS) {
			k = k.replace(" Arrow", "").replace("FnLock ", "");
			keyBtn = addKeyButton(k);
			rowContainer.add(keyBtn);
			
			System.out.println(k + " " + curRow + " " + curCol);
			
			curCol++;
			if (curRow < NUM_ROWS-1 && k.equals(END_OF_ROW[curRow])) {
				rowContainer = addKeyRow(curRow);
				curRow++;
				curCol = 0;
			}
		}
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
	
	public JPanel addKeyRow(int row) {
		JPanel rowContainer = new JPanel(new GridLayout(1,0,0,0));
		
		rowContainer.setOpaque(true);
		//rowContainer.setBackground(Color.RED);
		this.keyboardContainer.add(rowContainer);
		
		return rowContainer;
	}
	
	public GUI_JButton addKeyButton(final String key) {
		final GUI_JButton keyBtn = new GUI_JButton(key);
		
		keyBtn.setFont(GUI_Fonts.SMALL_TEXT);
		keyBtn.setPreferredSize(new Dimension(50,50));
		keyBtn.setMargin(new Insets(0, 0, 0, 0));
		
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
		
		return keyBtn;
	}
	
	/* temp: for testing purposes */
	public static void main(String[] args) {
		StationControl sc = new StationControl();
		KeyboardScreen ks = new KeyboardScreen(sc);
		ks.openInNewJFrame();
	}
}
