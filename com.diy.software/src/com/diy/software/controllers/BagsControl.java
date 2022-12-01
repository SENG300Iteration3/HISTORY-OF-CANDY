package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.util.Tuple;
import com.diy.software.listeners.BagsControlListener;

public class BagsControl implements ActionListener {
	private StationControl sc;
	private ArrayList<BagsControlListener> listeners;
	private static final double abritraryWeightOfBags = 50;
	private static final double abritraryPriceOfBags = 3.5;
	
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
		sc.getItemsControl().addItemToCheckoutList("Reusable Bag",abritraryPriceOfBags);
		sc.updateExpectedCheckoutWeight(abritraryWeightOfBags,true);
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
