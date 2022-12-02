package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.listeners.BagDispenserControlListener;
import com.jimmyselectronics.EmptyException;
import com.jimmyselectronics.svenden.ReusableBag;

public class BagDispenserControl implements ActionListener {
	private StationControl sc;
	private ItemsControl ic;
	private ArrayList<BagDispenserControlListener> listeners;
	private int numBag;
	private String input = "";
	private ReusableBag lastDispensedReusableBag;

	public BagDispenserControl(StationControl sc) {
		this.sc = sc;
		this.ic = sc.getItemsControl();
		this.listeners = new ArrayList<>();
	}
	
	private void dispenseBag() {
		resetInput();
		
		while(numBag > 0) {
			try {
				System.out.println("purchase: " + numBag);
				// add the dispensed bag to checkout item list
				lastDispensedReusableBag = sc.station.reusableBagDispenser.dispense();
				
				// update station expected weight and add bag on the bagging area
				sc.addReusableBag(lastDispensedReusableBag);
				sc.station.baggingArea.add(lastDispensedReusableBag);
				
				// notify item control of new bag dispensed
				ic.addReusableBags(lastDispensedReusableBag);
				numBag--;
				
			} catch (EmptyException e) {
//				System.out.println("Not enough bag. Only " + (numBag - i) + " bag available.");
			}
		}
		sc.goBackOnUI();
	}
	
	private void resetInput() {
		for (BagDispenserControlListener l : listeners)
			l.numberFieldHasBeenUpdated(this, "");
		input = "";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("NUMBER_BAGS: ")) {
			input += c.split(" ")[1];
			for (BagDispenserControlListener l : listeners)
				l.numberFieldHasBeenUpdated(this, input);
		} else {
			switch (c) {
			case "purchase bags":
				sc.startPurchaseBagsWorkflow();
				break;
			case "cancel":
				sc.goBackOnUI();
				break;
			case "correct":
				if (input.length() > 0) {
					input = input.substring(0, input.length() - 1);
					for (BagDispenserControlListener l : listeners)
						l.numberFieldHasBeenUpdated(this, input);
				}
				break;
			case "submit":
				numBag = Integer.parseInt(input); 
				if(numBag > 0) {
					dispenseBag();
				}
				break;
			default:
				break;
			}
		}
	}
	
	public void addListener(BagDispenserControlListener l) {
		listeners.add(l);
	}
}