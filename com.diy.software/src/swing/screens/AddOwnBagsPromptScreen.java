package swing.screens;

<<<<<<< Upstream, based on origin/main
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.BagsControlListener;

import swing.styling.*;

import javax.swing.*;
import java.awt.*;

public class AddOwnBagsPromptScreen extends Screen implements BagsControlListener{
	private GUI_JLabel promptLabel;
	private GUI_JButton doneAddingBags;
	private BagsControl bc;

	public AddOwnBagsPromptScreen(StationControl systemControl, String message) {
		super(systemControl);
		bc = systemControl.getBagsControl();
		bc.addListener(this);
		
		promptLabel = new GUI_JLabel(message);
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);

		this.addLayer(promptLabel, 0);

		doneAddingBags = new GUI_JButton();
		doneAddingBags.setText("Done");
		doneAddingBags.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, GUI_Color_Palette.DARK_BLUE));
		doneAddingBags.setPreferredSize(new Dimension(400, 100));
		doneAddingBags.setActionCommand("done adding bags");
		doneAddingBags.addActionListener(bc);

		this.addLayer(doneAddingBags, 75);

	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		doneAddingBags.setVisible(false);
		promptLabel.setText("Please Wait For The Attendant's Approval");
		
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		//systemControl.goBackOnUI();
		
=======
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



>>>>>>> 295e4e1 Created AddOwnBagsPromptScreen
	}



}
