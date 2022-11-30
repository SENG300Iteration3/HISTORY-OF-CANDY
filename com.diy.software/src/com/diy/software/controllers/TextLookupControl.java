package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.Product;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.listeners.TextLookupControlListener;
import com.diy.software.util.Tuple;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.Numeral;

/* 
 * GUI TODO's:
 * 1. Customer would notify via help button that they need assistance with currently selected item
 * 2. Customers station should turn a different color on attendant screen to indicate which
 * 	  station the assistance is required
 * 3. Name or description of the item should be displayed somewhere so attendant can enter 
 * 	  corresponding keyword
 * 4. Selection of "Search Products" button should bring up a search bar where each letter/number/symbol
 * 	  is shown as attendant types
 * 5. Results should appear in list format for attendant to choose from
 * 6. Choice is highlighted until attendant selects "Add Item" button
 * 7. Option of "Clear" button to start search over if results are unsatisfactory 
 * 8. Option of "Back" button to bring attendant to main screen where "Search Products" can be reselected
 * 8. GUI is updated on customers station after "Add Item" is selected
 * 
 * Logic TODO's:
 * - Break keyword and product descriptions into arrays of strings
 * - If any string in keyword array match a string in product description array, add to results
 * - Sort results by best match
 * - If selection is PLU or PLU/barcode is null, generate a random weight value and calculate price
 * - Add selected product to station
 */

public class TextLookupControl implements ActionListener{
	private StationControl sc;
	private ArrayList<TextLookupControlListener> listeners;
	
	// maybe use object instead so barcoded and PLU products can be stored together without losing PLU/barcode data
	private ArrayList<Product> results;	
	private double productWeight;
	private double productCost;
	
	public TextLookupControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
		this.results = new ArrayList<>();
	}
	
	public void addListener(TextLookupControlListener l) {
		listeners.add(l);
	}

	public void removeListener(TextLookupControlListener l) {
		listeners.remove(l);
	}
	
	private void findProduct(String keyword) {
		for (BarcodedProduct product : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()) {
			String[] splitDescription = product.getDescription().trim().split("\\s+");
			for (int i = 0; i < splitDescription.length; i++) {
				if (keyword.equals(splitDescription[i])){
			    	results.add(product);
			    	break;
			    }
			} 
		}
		for (PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
			String[] splitDescription = product.getDescription().trim().split("\\s+");
			for (int i = 0; i < splitDescription.length; i++) {
				if (keyword.equals(splitDescription[i])){
			    	results.add(product);
			    	break;
			    }
			}
		}
		//TODO: if nothing matches, display error and clear search to try again
	}
	
	public void addProduct(Product selection) {
		// match selection to corresponding result in array
		//ex. if barcoded product...
		BarcodedProduct itemToAdd = results.get(0);
		sc.getItemsControl().addItemToCheckoutList(new Tuple<String,Double>(itemToAdd.getDescription(), productCost));
	}
	
	private double calculatePrice(Product selection) {
		productCost = selection.getPrice();
		if (selection.isPerUnit() == false) {
			generateProductWeight();
			double weightInKilos = productWeight/1000;
			productCost *= weightInKilos;
		}
		return productCost;
	}
	
	private double generateProductWeight() {
		Random rand = new Random();
		productWeight = 100 + (1000 - 100) * rand.nextDouble();
		return productWeight;
	}
	
	public void placeProductInBaggingArea() {
		sc.blockStation();
		sc.updateWeightOfLastItemAddedToBaggingArea(productWeight);
		sc.updateExpectedCheckoutWeight(productWeight);
		for (TextLookupControlListener l : listeners) {
			l.awaitsBaggingOfItem(this);
		}
	}
	
	private void updateGUI() {
		for (TextLookupControlListener l: listeners) {
			l.checkoutHasBeenUpdated(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
