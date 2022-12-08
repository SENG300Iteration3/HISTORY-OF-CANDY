package swing.screens;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class PresentCardScreen extends Screen {
	private GUI_JLabel prompt;
	private GUI_JButton backButton;

	public PresentCardScreen(final StationControl systemControl, String cardType) {
		super(systemControl);

		systemControl.getWalletControl().enablePayments();

		this.prompt = new GUI_JLabel("Please insert, swipe, or tap your " + cardType + " card.");
		prompt.setFont(GUI_Fonts.FRANKLIN_BOLD);
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setPreferredSize(new Dimension(this.width - 200, 100));
		prompt.setForeground(Color.BLACK);
		this.addLayer(prompt, 0);

		this.backButton = makeCentralButton("BACK", this.width - 200, 100);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				systemControl.getWalletControl().disablePayments();
				systemControl.goBackOnUI();
			}
		});
		this.addLayer(backButton, 100);
	}
	
	public GUI_JButton getBackButton() {
		return backButton;
	}

}
