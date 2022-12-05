package com.diy.software.controllers;

import java.util.ArrayList;
import java.util.Random;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.listeners.TextLookupControlListener;
import com.diy.software.util.CodedProduct;
import com.diy.software.util.Tuple;

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
 * 9. GUI is updated on customers station after "Add Item" is selected
 * 
 * Functionality TODO's:
 * 1. Connect to keyboard through listeners
 */

public class TextLookupControl implements KeyboardControlListener{
	private AttendantControl ac;
	private StationControl sc;
	private ArrayList<TextLookupControlListener> listeners;
	
	// Can hold either Barcoded or PLUcoded product types without losing their different properties
	private ArrayList<CodedProduct> results;
	
	// Product info for selected result
	private double productWeight;
	private double productCost;
	private String productDescription;
	
	public TextLookupControl(AttendantControl ac, StationControl sc) {
		this.ac = ac;
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
			if (product.getDescription().contains(keyword)){
				CodedProduct match = new CodedProduct(product, null);
			    results.add(match);
			}
		}
		for (PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
			if (product.getDescription().contains(keyword)){
				CodedProduct match = new CodedProduct(null, product);
			    results.add(match);
			}
		}
		//TODO: if nothing matches, display error and clear search to try again
	}
	
	public void addProduct(int selectionIndex) {
		if (results.get(selectionIndex).getBarcodedProduct() == null) {
			PLUCodedProduct productToAdd = results.get(selectionIndex).getPLUCodedProduct();
			productWeight = generateProductWeight();
			productCost = calculatePrice(productToAdd, productWeight);
			productDescription = productToAdd.getDescription();
		}
		else {
			BarcodedProduct productToAdd = results.get(selectionIndex).getBarcodedProduct();
			productWeight = productToAdd.getExpectedWeight();
			productCost = productToAdd.getPrice();
			productDescription = productToAdd.getDescription();
		}
		sc.getItemsControl().addItemToCheckoutList(new Tuple<String,Double>(productDescription, productCost));
	}
	
	public void placeProductInBaggingArea() {
		sc.blockStation();
		sc.updateWeightOfLastItemAddedToBaggingArea(productWeight);
		sc.updateExpectedCheckoutWeight(productWeight);
		for (TextLookupControlListener l : listeners) {
			l.awaitsBaggingOfItem(this);
		}
	}
	
	// Generates a random weight for PLUCodedItems
	private double generateProductWeight() {
		double weight;
		Random rand = new Random();
		weight = 100 + (1000 - 100) * rand.nextDouble();
		return weight;
	}
	
	private double calculatePrice(PLUCodedProduct selection, double weight) {
		double price = selection.getPrice();
		if (selection.isPerUnit() == false) {
			double weightInKilos = weight/1000;
			price *= weightInKilos;
		}
		return price;
	}
	
	private void updateGUI() {
		for (TextLookupControlListener l: listeners) {
			l.checkoutHasBeenUpdated(this);
		}
	}
	
	@Override
	public void awaitingKeyboardInput(KeyboardControl kc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key) {
		
	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String text) {
		findProduct(text);
		System.out.println(results);
	}
	
}
