package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.diy.software.controllers.SystemControl;

import swing.GUI_Fonts;
import swing.GUI_JLabel;
import swing.Screen;

public class OkayPromptScreen extends Screen {
	private JLabel promptLabel;
	private JButton okayButton;

	public OkayPromptScreen(final SystemControl systemControl, String prompt, final boolean navigateToInitialScreen) {
		super(systemControl);

		this.promptLabel = new GUI_JLabel(prompt);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		promptLabel.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(promptLabel, 0);

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
		this.addLayer(okayButton, 100);
	}

	/* for testing purposes */
	public static void main(String[] args) {
		SystemControl sc = new SystemControl();
		OkayPromptScreen gui = new OkayPromptScreen(sc, "This is a test prompt.", false);
		gui.openInNewJFrame();
	}
}
