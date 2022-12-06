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
	
	public void ownBagsPlacedInBaggingArea() {
		sc.blockStation("Please Wait For Attendant's Approval");
		for (BagsControlListener l : listeners) {
			l.awaitingAttendantToVerifyBagsPlacedInBaggingArea(this);
		}
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
			default:
				break;
			}
		} catch (Exception ex) {
			
		}
	}

	public double getArbitraryBagWeight() {
		return abritraryWeightOfBags;
	}

}