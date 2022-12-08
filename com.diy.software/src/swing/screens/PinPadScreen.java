package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.diy.software.controllers.PinPadControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PinPadControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class PinPadScreen extends Screen implements PinPadControlListener {
	private PinPadControl pinPadController;

	private GridBagConstraints gridConstraint = new GridBagConstraints();

	private JButton[] pinpadButtons = new JButton[10];
	private JButton cancelButton = createPinPadButton("X");
	private JButton correctButton = createPinPadButton("O");
	private JButton submitButton = createPinPadButton(">");

	GUI_JPanel pinPadPanel;

	private static String HeaderText = "Pin Pad";

	JLabel message;
	JTextField passcode;

	public PinPadScreen(StationControl sc) {
		super(sc, HeaderText);
		pinPadController = sc.getPinPadControl();
		pinPadController.addListener(this);

		pinPadPanel = new GUI_JPanel();
		pinPadPanel.setLayout(new GridBagLayout());
		pinPadPanel.setBackground(GUI_Color_Palette.DARK_BLUE);

		initalizeMessageLabel();
		initalizeTextField();

		gridConstraint.gridy = 1;
		gridConstraint.ipadx = 100;
		gridConstraint.ipady = 10;

		for (int i = 0; i < 10; i++) {
			JButton currButton = createPinPadButton("" + (i + 1) % 10);
			currButton.setActionCommand("PIN_INPUT_BUTTON: " + (i + 1) % 10);
			currButton.addActionListener(pinPadController);
			pinpadButtons[i] = currButton;
			gridConstraint.gridx = i % 3;
			gridConstraint.gridy = (i / 3) + 2;
			if (i == 9) {
				gridConstraint.gridx = 1;
			}
			pinPadPanel.add(currButton, gridConstraint);
		}

		gridConstraint.gridy = 6;

		gridConstraint.gridx = 0;
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(pinPadController);
		pinPadPanel.add(cancelButton, gridConstraint);

		gridConstraint.gridx = 1;
		correctButton.setActionCommand("correct");
		correctButton.addActionListener(pinPadController);
		pinPadPanel.add(correctButton, gridConstraint);

		gridConstraint.gridx = 2;
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(pinPadController);
		pinPadPanel.add(submitButton, gridConstraint);

		addLayer(pinPadPanel, 0);
	}

	private void initalizeMessageLabel() {
		message = new GUI_JLabel("Enter your pin".toUpperCase());
		message.setFont(GUI_Fonts.FRANKLIN_BOLD);
		message.setHorizontalAlignment(JLabel.CENTER);

		int width = 405;
		int height = 50;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(message);
		addLayer(centerPanel, 0);

	}

	private void initalizeTextField() {
		passcode = new JTextField("pin".toUpperCase());
		passcode.setFont(GUI_Fonts.FRANKLIN_BOLD);
		passcode.setHorizontalAlignment(JLabel.CENTER);
		passcode.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

		int width = 405;
		int height = 70;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(passcode);
		addLayer(centerPanel, 0);

	}

	@Override
	public void pinHasBeenUpdated(PinPadControl ppc, String pin) {
		passcode.setText(pin);
	}

	private GUI_JButton createPinPadButton(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton pinPadButton = new GUI_JButton();
		pinPadButton.setText(text);
		pinPadButton.setBackground(GUI_Color_Palette.DARK_BROWN);
		pinPadButton.setForeground(GUI_Color_Palette.WHITE);

		pinPadButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		pinPadButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		pinPadButton.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return pinPadButton;
	}
	
	public JButton[] getPinpadButtons() {
		return pinpadButtons;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
	
	public JButton getCorrectButton() {
		return correctButton;
	}
	
	public JButton getSubmitButton() {
		return submitButton;
	}
	

}
