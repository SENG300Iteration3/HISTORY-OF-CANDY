package swing.screens;

import com.diy.software.controllers.StationControl;

import swing.styling.*;

import javax.swing.*;
import java.awt.*;

public class AddOwnBagsPromptScreen extends Screen {
	private GUI_JLabel promptLabel;
	private GUI_JButton doneAddingBags;

	public AddOwnBagsPromptScreen(StationControl systemControl, String message) {
		super(systemControl);
		promptLabel = new GUI_JLabel(message);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);

		this.addLayer(promptLabel, 0);

		doneAddingBags = new GUI_JButton();
		doneAddingBags.setText("Done Adding Bags");
		doneAddingBags.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, GUI_Color_Palette.DARK_BLUE));
		doneAddingBags.setPreferredSize(new Dimension(400, 100));

		this.addLayer(doneAddingBags, 75);



	}



}
