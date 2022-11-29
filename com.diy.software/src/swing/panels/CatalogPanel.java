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
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;

public class CatalogPanel extends JPanel {


	public CatalogPanel() {
		Product product;
		String strProductName;

		JButton button;

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
//			button.addActionListener(ic);
			this.add(button);
			button.setEnabled(true);

		}

	}
}