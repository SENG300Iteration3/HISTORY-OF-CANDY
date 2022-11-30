package swing.screens;

import com.diy.software.controllers.StationControl;

import swing.styling.*;

import javax.swing.*;
import java.awt.*;

public class BlockedPromptScreen extends Screen {
	private GUI_JLabel promptLabel;
	private GUI_JButton requestNoBaggingBtn;

	public BlockedPromptScreen(StationControl systemControl, String message) {
		super(systemControl);
		promptLabel = new GUI_JLabel(message);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);

		this.addLayer(promptLabel, 0);

		requestNoBaggingBtn = new GUI_JButton();
		requestNoBaggingBtn.setText("Request No Bagging");
		requestNoBaggingBtn.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, GUI_Color_Palette.DARK_BLUE));
		requestNoBaggingBtn.setPreferredSize(new Dimension(400, 100));

		this.addLayer(requestNoBaggingBtn, 70);



	}



}
