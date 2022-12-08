package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.diy.software.controllers.BagDispenserControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.BagDispenserControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class PurchaseBagScreen extends Screen implements BagDispenserControlListener{
	private BagDispenserControl bdc;

	private GridBagConstraints gridConstraint = new GridBagConstraints();

	private JButton[] numberPadButtons = new JButton[10];
	private JButton cancelButton = createNumberPadButton("X");
	private JButton correctButton = createNumberPadButton("O");
	private JButton submitButton = createNumberPadButton(">");

	private JTextField numberEntry;
	private JLabel bagMessage = new JLabel("");
	GUI_JPanel numberInputPanel;

	private static String HeaderText = "Purchase Reusable Bags";
	
	public PurchaseBagScreen(StationControl sc) {
		super(sc, HeaderText);
		bdc = sc.getBagDispenserControl();
		bdc.addListener(this);
		
		
		gridConstraint.gridy = 1;
		gridConstraint.gridx = 1;

		numberInputPanel = new GUI_JPanel();
		numberInputPanel.setLayout(new GridBagLayout());
		numberInputPanel.setBackground(GUI_Color_Palette.DARK_BLUE);

		initalizeMessageLabel();
		initalizeTextField();

		gridConstraint.gridy = 1;
		gridConstraint.ipadx = 100;
		gridConstraint.ipady = 10;

		for (int i = 0; i < 10; i++) {
			GUI_JButton currButton = createNumberPadButton("" + (i + 1) % 10);
			currButton.setActionCommand("NUMBER_BAGS: " + (i + 1) % 10);
			currButton.addActionListener(bdc);
			numberPadButtons[i] = currButton;
			gridConstraint.gridx = i % 3;
			gridConstraint.gridy = (i / 3) + 2;
			if (i == 9) {
				gridConstraint.gridx = 1;
			}
			numberInputPanel.add(currButton, gridConstraint);
		}

		gridConstraint.gridy = 6;

		gridConstraint.gridx = 0;
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(bdc);
		numberInputPanel.add(cancelButton, gridConstraint);

		gridConstraint.gridx = 1;
		correctButton.setActionCommand("correct");
		correctButton.addActionListener(bdc);
		numberInputPanel.add(correctButton, gridConstraint);

		gridConstraint.gridx = 2;
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(bdc);
		numberInputPanel.add(submitButton, gridConstraint);

		addLayer(numberInputPanel, 0);
	}
	
	private void initalizeMessageLabel() {
		bagMessage = new GUI_JLabel("Number of bags you want to purchase");
		bagMessage.setFont(new Font("Franklin Gothic", Font.BOLD, 22));
		bagMessage.setHorizontalAlignment(JLabel.CENTER);

		int width = 405;
		int height = 50;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(bagMessage);
		addLayer(centerPanel, 0);

	}

	private void initalizeTextField() {
		numberEntry = new JTextField();
		numberEntry.setFont(GUI_Fonts.FRANKLIN_BOLD);
		numberEntry.setHorizontalAlignment(JLabel.CENTER);
		numberEntry.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

		int width = 405;
		int height = 70;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(numberEntry);
		addLayer(centerPanel, 0);

	}

	private GUI_JButton createNumberPadButton(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton numberPadButton = new GUI_JButton();
		numberPadButton.setText(text);
		numberPadButton.setBackground(GUI_Color_Palette.DARK_BROWN);
		numberPadButton.setForeground(GUI_Color_Palette.WHITE);

		numberPadButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		numberPadButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		numberPadButton.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return numberPadButton;
	}

	@Override
	public void numberFieldHasBeenUpdated(BagDispenserControl bdp, String input) {
		numberEntry.setText(input);
	}
}
