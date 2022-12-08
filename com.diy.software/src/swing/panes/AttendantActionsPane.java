package swing.panes;

import javax.swing.JPanel;

import com.diy.software.controllers.StationControl;

import swing.panels.AttendantActionsPanel;
import swing.panels.AttendantKeyboardPanel;
import swing.styling.GUI_Color_Palette;

public class AttendantActionsPane {

	StationControl sc;
	JPanel container;
	int width = 990;
	int height = 200;

	AttendantActionsPanel aaPanel;
	AttendantKeyboardPanel keyboardPanel;
	
	public AttendantActionsPane(StationControl sc) {
		this.sc = sc;
		
		container = new JPanel();
		container.setBackground(GUI_Color_Palette.LIGHT_BLUE);
		aaPanel = new AttendantActionsPanel(sc);
		container.add(aaPanel);
		
		keyboardPanel = new AttendantKeyboardPanel(sc);
		container.add(keyboardPanel);
	}

	public JPanel getRootPanel() {
		return this.container;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
