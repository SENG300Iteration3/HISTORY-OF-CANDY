package swing.screens;

import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class BlockedPromptScreen extends Screen {
	private GUI_JLabel promptLabel;

	public BlockedPromptScreen(StationControl systemControl, String message) {
		super(systemControl);
		promptLabel = new GUI_JLabel(message);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);

		this.addLayer(promptLabel, 0);
	}
}
