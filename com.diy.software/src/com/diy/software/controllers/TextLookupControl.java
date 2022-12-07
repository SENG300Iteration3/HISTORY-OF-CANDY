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
	private CodedProduct selection;
	
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
	
	public void readyToSearch() {
		for (TextLookupControlListener l: listeners) {
			l.searchQueryWasEntered(this);
		}
	}
	
	public void findProduct(String keyword) {
		for (BarcodedProduct product : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()) {
			//System.out.println("Product: " + product.getDescription() + "   Keyword: " + keyword);
			if (product.getDescription().toLowerCase().contains(keyword.toLowerCase())){
				CodedProduct match = new CodedProduct(product, null);
			    results.add(match);
			}
		}
		for (PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
			if (product.getDescription().toLowerCase().contains(keyword.toLowerCase())){
				CodedProduct match = new CodedProduct(null, product);
			    results.add(match);
			}
		}
		if(results.size() == 0) {
			for (TextLookupControlListener l: listeners) {
				l.noResultsWereFound(this);
			}
		}
		else {
			for (TextLookupControlListener l: listeners) {
				l.resultsWereFound(this);
			}
		}
	}
	
	public CodedProduct getResult(int selectionIndex) {
		CodedProduct result;
		if (selectionIndex >= results.size() || selectionIndex < 0) {
			result = null;
		}
		else {
			result = results.get(selectionIndex);
			for (TextLookupControlListener l : listeners) {
				l.resultWasChosen(this);
			}
		}
		return result;
	}
	
	public ArrayList<CodedProduct> getResults() {
		return results;
	}
	
	public void addProduct(int selectionIndex) {
		sc.blockStation();
		//ac.preventStationUse();
		selection = getResult(selectionIndex);
		if (selection.getBarcodedProduct() == null) {
			PLUCodedProduct productToAdd = selection.getPLUCodedProduct();
			sc.getItemsControl().addItemToCheckoutList(productToAdd.getPLUCode());
			productWeight = generateProductWeight();
			productCost = calculatePrice(productToAdd, productWeight);
			productDescription = productToAdd.getDescription();
		}
		else {
			BarcodedProduct productToAdd = selection.getBarcodedProduct();
			sc.getItemsControl().addItemToCheckoutList(productToAdd.getBarcode());
			productWeight = productToAdd.getExpectedWeight();
			productCost = (double)productToAdd.getPrice();
			productDescription = productToAdd.getDescription();
		}
		sc.getItemsControl().updateCheckoutTotal(productCost);
		for (TextLookupControlListener l : listeners) {
			l.itemHasBeenAddedToCheckout(this);
		}
	}

	public void placeProductInBaggingArea() {
		sc.updateWeightOfLastItemAddedToBaggingArea(productWeight);
		sc.updateExpectedCheckoutWeight(productWeight);
		ac.permitStationUse();
		for (TextLookupControlListener l : listeners) {
			l.itemHasBeenBagged(this);
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
		double price = (double) selection.getPrice();
		if (selection.isPerUnit() == false) {
			double weightInKilos = weight/1000;
			price *= weightInKilos;
		}
		return price;
	}
	
	public void clearSearch() {
		results = new ArrayList<>();
		selection = null;
		productDescription = null;
		productCost = 0;
		productWeight = 0;
		for (TextLookupControlListener l : listeners) {
			l.searchHasBeenCleared(this);
		}
	}
	
	public double getProductCost() {
		return productCost;
	}
	
	public double getProductWeight() {
		return productWeight;
	}
	
	public String getProductDescription() {
		return productDescription;
	}

	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
		
	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String query) {
		readyToSearch();
		findProduct(query);
	}
	
}