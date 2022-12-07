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

	String errorText;

	JLabel message;
	JLabel errorMessage;
	GUI_JLabel pluCode;

	private GUI_JPanel centerPanel;

	public PLUCodeScreen(StationControl sc) {
		super(sc, HeaderText);
		pluCodeController = sc.getPLUCodeControl();
		pluCodeController.addListener(this);

		pluCodePanel = new GUI_JPanel();
		pluCodePanel.setLayout(new GridBagLayout());
		pluCodePanel.setBackground(GUI_Color_Palette.DARK_BLUE);

		initializePanels();

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

		//addLayer(pluCodePanel, 0);
	}

	private void initializePanels() {
		message = new GUI_JLabel("Enter the item's PLU code".toUpperCase());
		message.setFont(GUI_Fonts.FRANKLIN_BOLD);
		message.setHorizontalAlignment(JLabel.CENTER);

		errorMessage = new GUI_JLabel(errorText);
		errorMessage.setFont(GUI_Fonts.FRANKLIN_BOLD);
		errorMessage.setHorizontalAlignment(JLabel.CENTER);

		pluCode = new GUI_JLabel("PLU code".toUpperCase());
		pluCode.setFont(GUI_Fonts.FRANKLIN_BOLD);
		pluCode.setHorizontalAlignment(JLabel.CENTER);
		pluCode.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

		int width = 405;				//potentially change for error message size
		int height = 150;				//TODO: FIND NICE HEIGHT

		centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(3, 0));

		centerPanel.add(errorMessage);
		centerPanel.add(message);
		centerPanel.add(pluCode);
		addLayer(centerPanel, 0);

		addLayer(pluCodePanel, 0);

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
		this.pluCode.setText("");
		
	}

	@Override
	public void pluErrorMessageUpdated(PLUCodeControl ppc, String errorText) {
		this.errorText = errorText;
		//initializePanels();			//in order to call this, i need to clear the components on the screen (addLayer() breaks it)
		//centerPanel.revalidate();
		//centerPanel.repaint();
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
