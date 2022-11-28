package swing.frames;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import swing.panels.CatalogPanel;
import swing.styling.GUI_Constants;
import swing.styling.GUI_JFrame;

public class CatalogPrototypeGUI {
	private JFrame frame = new JFrame("Catalog ProtoType");
	private JPanel container;
	CatalogPanel catalogPanel;

	public CatalogPrototypeGUI() {
		container = new JPanel();

		frame.setVisible(true);
		frame.setSize(820, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(container, BorderLayout.CENTER);

		catalogPanel = new CatalogPanel();

		container.add(catalogPanel);
	}
}
