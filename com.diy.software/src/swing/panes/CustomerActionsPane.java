package swing.panes;

import javax.swing.*;

import com.diy.software.controllers.StationControl;

//import swing.panels.CustomerBagsPanel;
import swing.panels.CustomerItemsPanel;
import swing.panels.CustomerWalletPanel;
import swing.styling.GUI_Color_Palette;
import swing.panels.CustomerCashPanel;

public class CustomerActionsPane {

	StationControl sc;
	JPanel container;
	int width = 990;
	int height = 200;

	CustomerItemsPanel itemsPanel;
	CustomerWalletPanel walletPanel;
	//CustomerBagsPanel bagsPanel;
	CustomerCashPanel cashPanel;

	public CustomerActionsPane(StationControl sc) {
		this.sc = sc;
		
		container = new JPanel();
		container.setBackground(GUI_Color_Palette.LIGHT_BLUE);

		walletPanel = new CustomerWalletPanel(sc);
		itemsPanel = new CustomerItemsPanel(sc);
		//bagsPanel = new CustomerBagsPanel(sc);
		cashPanel = new CustomerCashPanel(sc);

		container.add(walletPanel);
		container.add(itemsPanel);
		//container.add(bagsPanel);
		container.add(cashPanel);
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
