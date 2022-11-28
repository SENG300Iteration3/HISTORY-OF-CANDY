package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import com.diy.software.controllers.MembershipControl;
import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class PresentMembershipCardScreen extends Screen {
	private GUI_JLabel prompt;
	private GUI_JButton backButton;

	public PresentMembershipCardScreen(final StationControl systemControl) {
		super(systemControl);

		//systemControl.startMembershipCardInput();

		this.prompt = new GUI_JLabel("Please scan and swipe your membership card.");
		prompt.setFont(GUI_Fonts.FRANKLIN_BOLD);
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(prompt, 0);

		this.backButton = makeCentralButton("BACK", this.width - 200, 100);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				systemControl.cancelMembershipCardInput();
				systemControl.goBackOnUI();
			}
		});
		this.addLayer(backButton, 100);
	}

}
