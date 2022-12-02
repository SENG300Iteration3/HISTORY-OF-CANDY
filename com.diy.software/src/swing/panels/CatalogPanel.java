package swing.panels;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

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

public class CatalogPanel extends JPanel implements ItemsControlListener{
	private static final long serialVersionUID = 1L;
	private ItemsControl ic;

	public CatalogPanel(StationControl sc) {
		super();
		ic = sc.getItemsControl();
		ic.addListener(this);
		
		Product product;
		String strProductName;

		JButton button;
		
		System.out.print("here");
		
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

			button = new JButton(strProductName);
			button.setActionCommand(strProductName);
			button.addActionListener(ic);
			this.add(button);
			button.setEnabled(true);

		}

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
}
