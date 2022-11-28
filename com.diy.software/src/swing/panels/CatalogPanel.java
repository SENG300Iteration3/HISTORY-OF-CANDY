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
	private Barcode barcode1, barcode2;
	private PriceLookUpCode plu1, plu2;
	private BarcodedProduct bp1, bp2;
	private PLUCodedProduct pcp1, pcp2;

	public void initializeInventory() {
		barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four });
		barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero });
		plu1 = new PriceLookUpCode("2718");
		plu2 = new PriceLookUpCode("31415");

		bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		ProductDatabases.INVENTORY.put(bp1, 10);
		bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		ProductDatabases.INVENTORY.put(bp2, 10);
		pcp1 = new PLUCodedProduct(plu1, "Rib Eye Steak", 350);
		ProductDatabases.INVENTORY.put(pcp1, 10);
		pcp2 = new PLUCodedProduct(plu2, "Cauliflower", 550);
		ProductDatabases.INVENTORY.put(pcp2, 10);
	}

	public CatalogPanel() {
		initializeInventory(); // FIXME: data will be initialized else where
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
