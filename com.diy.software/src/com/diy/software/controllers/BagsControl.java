package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.util.Tuple;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.BagsControlListener;

public class BagsControl implements ActionListener {
	private StationControl sc;
	private ArrayList<BagsControlListener> listeners;
	private static final double abritraryWeightOfBags = 50;
	private static final double abritraryPriceOfBags = 3.5;
	private static Barcode purchasableBagBarcode;
	private static BarcodedItem purchasableBagItem;
	private static BarcodedProduct purchasableBagProduct;
	
	public BagsControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
		Barcode purchasableBagBarcode = new Barcode(new Numeral[] { Numeral.one, Numeral.one, Numeral.one, Numeral.one }); 
		BarcodedItem purchasableBagItem = new BarcodedItem(purchasableBagBarcode, 50); 	
		BarcodedProduct purchasableBagProduct = new BarcodedProduct(purchasableBagBarcode, "Purchasable Bag", 350, 50);
		FakeDataInitializer.BARCODED_ITEM_DATABASE.put(purchasableBagBarcode, purchasableBagItem);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(purchasableBagBarcode, purchasableBagProduct);
		ProductDatabases.INVENTORY.put(purchasableBagProduct, 1000);
	}
	
	public void addListener(BagsControlListener l) {
		listeners.add(l);
	}

	public void removeListener(BagsControlListener l) {
		listeners.remove(l);
	}
	
	
	public void placeBagsInBaggingArea() {
		sc.blockStation();
		sc.updateWeightOfLastItemAddedToBaggingArea(abritraryWeightOfBags);
		sc.updateExpectedCheckoutWeight(abritraryWeightOfBags,true);
		for (BagsControlListener l : listeners) {
			l.awaitingCustomerToFinishPlacingBagsInBaggingArea(this);
		}
	}
	
	// FIXME: where is the bag dispensery in the hardware mentioned in the use case
	public void ownBagsPlacedInBaggingArea() {
		sc.blockStation();
		for (BagsControlListener l : listeners) {
			l.awaitingAttendantToVerifyBagsPlacedInBaggingArea(this);
		}
	}
	
	// FIXME: need to update price
	public void placePurchasedBagsInBaggingArea() {
		sc.updateWeightOfLastItemAddedToBaggingArea(abritraryWeightOfBags);
		sc.getItemsControl().setWeighSuccess(true);
		sc.updateExpectedCheckoutWeight(abritraryWeightOfBags,true);
		sc.station.baggingArea.add(purchasableBagItem);
		sc.getItemsControl().addItemToCheckoutList(this.purchasableBagBarcode,abritraryPriceOfBags);
		sc.getItemsControl().updateCheckoutTotal(abritraryPriceOfBags);
		sc.unblockStation(); // call this to update total on gui
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "add bags":
				System.out.println("Customer has their own bags");
				placeBagsInBaggingArea();
				break;
			case "done adding bags":
				System.out.println("Customer has placed their bags in bagging area");
				ownBagsPlacedInBaggingArea();
				break;
			case "purchase bags":
				System.out.println("Customer has purchased reusable bags and added it to the bagging area");
				placePurchasedBagsInBaggingArea();
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			
		}
	}

	public double getArbitraryBagPrice() {
		return abritraryPriceOfBags;
	}
	public double getArbitraryBagWeight() {
		return abritraryWeightOfBags;
	}
}
