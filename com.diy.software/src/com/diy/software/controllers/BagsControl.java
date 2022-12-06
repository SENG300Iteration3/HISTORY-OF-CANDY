package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.util.Tuple;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.BagsControlListener;

public class BagsControl implements ActionListener {
	private StationControl sc;
	private ArrayList<BagsControlListener> listeners;
	private static final double abritraryWeightOfBags = 50;
	private static final double abritraryPriceOfBags = 3;
	private static final Barcode purchasableBagBarcode = new Barcode(new Numeral[] { Numeral.one, Numeral.one, Numeral.one, Numeral.nine});  
	
	public BagsControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
		
	}
	
	public void addListener(BagsControlListener l) {
		listeners.add(l);
	}

	public void removeListener(BagsControlListener l) {
		listeners.remove(l);
	}
	
	
	public void placeBagsInBaggingArea() {
		sc.blockStation("use own bags");
		sc.updateWeightOfLastItemAddedToBaggingArea(abritraryWeightOfBags);
		sc.updateExpectedCheckoutWeight(abritraryWeightOfBags,true);
		for (BagsControlListener l : listeners) {
			l.awaitingCustomerToFinishPlacingBagsInBaggingArea(this);
		}
	}
	
	// FIXME: where is the bag dispensery in the hardware mentioned in the use case
	public void ownBagsPlacedInBaggingArea() {
		sc.blockStation("Please Wait For Attendant's Approval");
		for (BagsControlListener l : listeners) {
			l.awaitingAttendantToVerifyBagsPlacedInBaggingArea(this);
		}
	}
	
	// FIXME: need to update price
	public void placePurchasedBagsInBaggingArea() {
		sc.updateWeightOfLastItemAddedToBaggingArea(abritraryWeightOfBags);
		sc.updateExpectedCheckoutWeight(abritraryWeightOfBags,false);
		BarcodedItem purchasableBag = new BarcodedItem(purchasableBagBarcode, 50); 
		sc.station.baggingArea.add(purchasableBag);
		sc.barcodedItems.put(purchasableBagBarcode, purchasableBag);
		sc.getItemsControl().addItemToCheckoutList(purchasableBagBarcode, null);
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