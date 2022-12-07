package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class RemoveItemScreen extends Screen {
	private JLabel promptLabel;
	private JButton enterButton;
	private JTextField numberField;

	public RemoveItemScreen(final StationControl systemControl, AttendantControl ac) {
		super(systemControl);

		this.promptLabel = new GUI_JLabel("Enter the number corrseponding to the item to be removed: ");
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		promptLabel.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(promptLabel, 0);
		
		numberField = new JTextField();
		numberField.setFont(GUI_Fonts.FRANKLIN_BOLD);
		numberField.setHorizontalAlignment(SwingConstants.CENTER);
		numberField.setPreferredSize(new Dimension(this.width - 200, 100));
		addLayer(numberField, 100);
		
		this.enterButton = makeCentralButton("ENTER", this.width - 200, 100);
		enterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				if (navigateToInitialScreen) {
//					systemControl.goToInitialScreenOnUI();
//				} else {
//					systemControl.goBackOnUI();
//				}
			}
		});
		this.addLayer(enterButton, 100);
	}
}
