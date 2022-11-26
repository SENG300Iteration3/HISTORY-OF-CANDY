package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import com.diy.software.controllers.SystemControl;

import swing.GUI_Fonts;
import swing.GUI_JButton;
import swing.GUI_JLabel;
import swing.Screen;

public class PresentCardScreen extends Screen {
	private GUI_JLabel prompt;
	private GUI_JButton backButton;

	public PresentCardScreen(final SystemControl systemControl, String cardType) {
		super(systemControl);

		systemControl.getWalletControl().enablePayments();

		this.prompt = new GUI_JLabel("Please insert, swipe, or tap your " + cardType + " card.");
		prompt.setFont(GUI_Fonts.FRANKLIN_BOLD);
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setPreferredSize(new Dimension(this.width - 200, 100));
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

	/* for testing purposes */
	public static void main(String[] args) {
		SystemControl sc = new SystemControl();
		PresentCardScreen gui = new PresentCardScreen(sc, "TEST");
		gui.openInNewJFrame();
	}
}
