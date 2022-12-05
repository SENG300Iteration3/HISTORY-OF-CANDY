package swing.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.Product;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.ItemsControlListener;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Constants;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;

public class CatalogPanel extends JPanel implements ItemsControlListener {
	private static final long serialVersionUID = 1L;
	private ItemsControl ic;
	private GUI_JButton cancelBtn;

	public CatalogPanel(StationControl sc) {
		super();
		ic = sc.getItemsControl();
		ic.addListener(this);

		Product product;
		String strProductName;

//		JButton button;
		GUI_JButton button;
		
		JLabel selectItemLabel = new JLabel("SELECT ITEM FROM CATALOGUE");
		selectItemLabel.setFont(GUI_Fonts.TITLE);
		selectItemLabel.setForeground(GUI_Color_Palette.WHITE);
		JPanel catalogPanel = new JPanel(new BorderLayout());
		catalogPanel.setBackground(Color.RED);
		catalogPanel.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT - 100));

        JPanel selectItemTextPanel = new JPanel(new GridBagLayout());
        selectItemTextPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectItemTextPanel.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH, 70));
        selectItemTextPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
        selectItemTextPanel.add(selectItemLabel);

        catalogPanel.add(selectItemTextPanel, BorderLayout.PAGE_START);


        this.add(catalogPanel);

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 2));
        //buttonsPanel.setPreferredSize(new Dimension(GUI_Constants.SCREEN_WIDTH, 200));
        //catalogPanel.add(buttonsPanel);

        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 50, 100));
        
		// Iterate through Inventory to Create Buttons
		for (Entry<Product, Integer> entry : ProductDatabases.INVENTORY.entrySet()) {
			product = entry.getKey();

			if (product instanceof BarcodedProduct) {
				strProductName = ((BarcodedProduct) product).getDescription();

				System.out.println("BarcodedProduct: " + strProductName);
			} else {
				// Assumes that there are only two types of Products: (1) BarcodedProduct and
				// (2) PLUCodedProduct
				strProductName = ((PLUCodedProduct) product).getDescription();

				System.out.println("PLUCodedProduct: " + strProductName);
			}

	        JPanel panel = new JPanel(new GridLayout());
            button = makeButton(strProductName, panel);
			button.setActionCommand(strProductName);
			button.addActionListener(ic);
            buttonsPanel.add(panel);
		}

        JScrollPane scrollPane = new JScrollPane(buttonsPanel);
        catalogPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel cancelPanel = new JPanel(new GridLayout());
        cancelPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));
        cancelBtn = makeButton("Cancel", cancelPanel);
        cancelBtn.setActionCommand("cancel catalog");
        cancelBtn.addActionListener(ic);
        catalogPanel.add(cancelPanel, BorderLayout.PAGE_END);

	}

	// This code is from AddItemsScreen.java
	private GUI_JButton makeButton(String text, JPanel parent) {

		GUI_JButton btn = new GUI_JButton(text.toUpperCase());
		btn.setFont(GUI_Fonts.TITLE);
		btn.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, GUI_Color_Palette.DARK_BLUE));
		btn.setBackground(GUI_Color_Palette.DARK_BROWN);
		btn.setOpaque(true);
		parent.add(btn);

		return btn;
	}

	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemWasSelected(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsHaveBeenUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void productSubtotalUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBePlacedInScanningArea(ItemsControl itemsControl) {
		// TODO Auto-generated method stub
		
	}
}
