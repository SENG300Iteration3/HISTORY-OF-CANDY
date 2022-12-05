package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class OkayPromptScreen extends Screen {
	private JLabel promptLabel;
	private JButton okayButton;
	GUI_JButton printReceiptButton;

	public OkayPromptScreen(final StationControl systemControl, String prompt, final boolean navigateToInitialScreen) {
		super(systemControl);

		this.promptLabel = new GUI_JLabel(prompt);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		promptLabel.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(promptLabel, 0);
		
		this.printReceiptButton = makeCentralButton("PRINT RECEIPT", this.width - 200, 100);
		printReceiptButton.setActionCommand("printReceipt");
		printReceiptButton.addActionListener(systemControl.getReceiptControl());
		this.addLayer(printReceiptButton, 1);
		
		this.okayButton = makeCentralButton("OKAY", this.width - 200, 100);
		okayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (navigateToInitialScreen) {
					systemControl.goToInitialScreenOnUI();
				} else {
					systemControl.goBackOnUI();
				}
			}
		});
		this.addLayer(okayButton, 0);
	}
}
