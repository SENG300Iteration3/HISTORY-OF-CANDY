package swing.screens;

import com.diy.software.controllers.PLUCodeControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PLUCodeControlListener;
import swing.styling.*;

import javax.swing.*;
import java.awt.*;

public class PLUCodeScreen extends Screen implements PLUCodeControlListener {
	private PLUCodeControl pluCodeController;

	private GridBagConstraints gridConstraint = new GridBagConstraints();

	private JButton[] pluCodeButtons = new JButton[10];
	private JButton cancelButton = createNumPadButtons("X");
	private JButton correctButton = createNumPadButtons("O");
	private JButton submitButton = createNumPadButtons(">");

	GUI_JPanel pluCodePanel;

	private static String HeaderText = "PLU Code";

	JLabel message = new JLabel("");
	JTextField pluCode;

	public PLUCodeScreen(StationControl sc) {
		super(sc, HeaderText);
		pluCodeController = sc.getPLUCodeControl();
		pluCodeController.addListener(this);

		pluCodePanel = new GUI_JPanel();
		pluCodePanel.setLayout(new GridBagLayout());
		pluCodePanel.setBackground(GUI_Color_Palette.DARK_BLUE);

		initializePanels();
		initializeTextField();

		gridConstraint.gridy = 1;
		gridConstraint.ipadx = 100;
		gridConstraint.ipady = 10;

		for (int i = 0; i < 10; i++) {
			JButton currButton = createNumPadButtons("" + (i + 1) % 10);
			currButton.setActionCommand("PLU_INPUT_BUTTON: " + (i + 1) % 10);
			currButton.addActionListener(pluCodeController);
			pluCodeButtons[i] = currButton;
			gridConstraint.gridx = i % 3;
			gridConstraint.gridy = (i / 3) + 2;
			if (i == 9) {
				gridConstraint.gridx = 1;
			}
			pluCodePanel.add(currButton, gridConstraint);
		}

		gridConstraint.gridy = 6;

		gridConstraint.gridx = 0;
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(pluCodeController);
		pluCodePanel.add(cancelButton, gridConstraint);

		gridConstraint.gridx = 1;
		correctButton.setActionCommand("correct");
		correctButton.addActionListener(pluCodeController);
		pluCodePanel.add(correctButton, gridConstraint);

		gridConstraint.gridx = 2;
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(pluCodeController);
		pluCodePanel.add(submitButton, gridConstraint);

		addLayer(pluCodePanel, 0);
	}

	private void initializePanels() {
		message = new GUI_JLabel("Enter the item's PLU code".toUpperCase());
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

	private void initializeTextField() {

		pluCode = new JTextField();
		pluCode.setFont(GUI_Fonts.FRANKLIN_BOLD);
		pluCode.setHorizontalAlignment(JLabel.CENTER);
		pluCode.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

		int width = 405;
		int height = 70;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(pluCode);
		addLayer(centerPanel, 0);

	}

	private GUI_JButton createNumPadButtons(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton numpadButton = new GUI_JButton();
		numpadButton.setText(text);
		numpadButton.setBackground(GUI_Color_Palette.DARK_BROWN);
		numpadButton.setForeground(GUI_Color_Palette.WHITE);

		numpadButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		numpadButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		numpadButton.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return numpadButton;
	}

	@Override
	public void pluCodeEntered(PLUCodeControl pcc, String pluCode) {
		this.pluCode.setText("PLU CODE");

		//to give proper formatting for when an invalid code is given and then fixed. previously the code would keep the error message on screen instead of resetting font properly
		//TODO: how to deal with incorrect plu code given for selected item (how would the system know the correct one?)
		message.setText(("Enter the item's PLU code".toUpperCase()));
		message.setFont(GUI_Fonts.FRANKLIN_BOLD);
		message.setForeground(GUI_Color_Palette.WHITE);
	}

	@Override
	public void pluErrorMessageUpdated(PLUCodeControl ppc, String errorText) {
		message.setFont(new Font("Franklin Gothic", Font.BOLD, 17));
		message.setForeground(Color.RED);
		message.setText(errorText);
	}

	@Override
	public void pluHasBeenUpdated(PLUCodeControl pcc, String pluCode) {
		this.pluCode.setText(pluCode);

	}
	
	public JButton[] getPluCodeButtons() {
		return pluCodeButtons;
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
