package swing.panes;

import javax.swing.*;

import com.diy.software.controllers.StationControl;

//import swing.panels.CustomerBagsPanel;
import swing.panels.CustomerItemsPanel;
import swing.panels.CustomerWalletPanel;
import swing.styling.GUI_Color_Palette;
import swing.panels.AttendantActionsPanel;
import swing.panels.CustomerCashPanel;

public class AttendantActionsPane {

	StationControl sc;
	JPanel container;
	int width = 990;
	int height = 200;

	AttendantActionsPanel aaPanel;
	
	public AttendantActionsPane(StationControl sc) {
		this.sc = sc;
		
		container = new JPanel();
		container.setBackground(GUI_Color_Palette.LIGHT_BLUE);
		aaPanel = new AttendantActionsPanel(sc);
		container.add(aaPanel);
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
