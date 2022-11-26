package swing.screens;

import com.diy.software.controllers.SystemControl;

import swing.GUI_Fonts;
import swing.GUI_JLabel;
import swing.Screen;

public class BlockedPromptScreen extends Screen {
	private GUI_JLabel promptLabel;

	public BlockedPromptScreen(SystemControl systemControl, String message) {
		super(systemControl);
		promptLabel = new GUI_JLabel(message);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);

		this.addLayer(promptLabel, 0);
	}
}
