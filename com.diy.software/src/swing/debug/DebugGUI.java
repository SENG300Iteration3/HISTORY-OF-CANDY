package swing.debug;

import javax.swing.*;

import com.diy.software.controllers.SystemControl;

import swing.GUI_Color_Palette;
import swing.panels.DebugBagsPanel;
import swing.panels.DebugItemsPanel;
import swing.panels.DebugWalletPanel;
import swing.panels.DebugCashPanel;

import java.awt.*;

public class DebugGUI {

	SystemControl sc;
	JFrame frame;
	JPanel container;
	int width = 990;
	int height = 200;

	DebugItemsPanel itemsPanel;
	DebugWalletPanel walletPanel;
	DebugBagsPanel bagsPanel;
	DebugCashPanel cashPanel;

	public DebugGUI(SystemControl sc) {
		this.sc = sc;
		this.frame = new JFrame("DEBUG GUI");

		container = new JPanel();
		container.setBackground(GUI_Color_Palette.LIGHT_BLUE);

		frame.setVisible(true);
		frame.setSize(width, height);
		frame.setLocation(790, 745);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(container, BorderLayout.CENTER);

		walletPanel = new DebugWalletPanel(sc);
		itemsPanel = new DebugItemsPanel(sc);
		bagsPanel = new DebugBagsPanel(sc);
		cashPanel = new DebugCashPanel(sc);

		container.add(walletPanel);
		container.add(itemsPanel);
		container.add(bagsPanel);
		container.add(cashPanel);
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
