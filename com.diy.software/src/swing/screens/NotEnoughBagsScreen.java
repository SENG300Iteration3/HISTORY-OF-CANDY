package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class NotEnoughBagsScreen extends Screen {
	private JLabel promptLabel;
	private JLabel promptLabel2;
	private JButton addBagsButton;
	private JButton askAttendantButton;

	public NotEnoughBagsScreen(final StationControl systemControl, int numBag) {
		super(systemControl);

		this.promptLabel = new GUI_JLabel("There are only " + numBag + " bags left in stock");
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		promptLabel.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(promptLabel, 0);

		this.addBagsButton = makeCentralButton("Take " + numBag + " Bags", this.width - 200, 100);
		addBagsButton.setActionCommand("dispense remaining");
		addBagsButton.addActionListener(systemControl.getBagDispenserControl());
		this.addLayer(addBagsButton, 100);
		
		askAttendantButton = makeCentralButton("Ask For More Bags", this.width - 200, 100);
		askAttendantButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//systemControl.goBackOnUI();
				systemControl.notifyNoBagsInStock();
			}
		});
		this.addLayer(askAttendantButton, 0);
		
	}
}
