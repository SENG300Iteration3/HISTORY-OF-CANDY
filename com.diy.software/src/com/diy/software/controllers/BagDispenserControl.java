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
		while(numBag > 0) {
			try {
				// add the dispensed bag to checkout item list
				lastDispensedReusableBag = sc.station.reusableBagDispenser.dispense();
				
				// update station expected weight and add bag on the bagging area
				sc.addReusableBag(lastDispensedReusableBag);
				sc.station.baggingArea.add(lastDispensedReusableBag);
				
				// notify item control of new bag dispensed
				ic.addReusableBags(lastDispensedReusableBag);
				numBag--;
				
			} catch (EmptyException e) {}
		}
		sc.goBackOnUI();
	}
	
	public void checkBagInStock() {
		resetInput();
		if(sc.getBagInStock() >= numBag) {
			dispenseBag(); 
		}else if(sc.getBagInStock() == 0){
			// customer can choose "ask for more bag"
			sc.notifyNoBagsInStock();
		}else {
			sc.notifyNotEnoughBagsInStock(sc.getBagInStock());
		}
	}
	
	public void setNumBag(int value) {
		numBag = value;
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
				resetInput();
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
				if(input != "") {
					numBag = Integer.parseInt(input); 
					if(numBag > 0) {
						checkBagInStock();
					}
				}
				break;
			case "dispense remaining":
				setNumBag(sc.getBagInStock());
				dispenseBag();
				break;
			default:
				break;
			}
		}
	}
	
	public void addListener(BagDispenserControlListener l) {
		listeners.add(l);
	}
	
	public int getNumBag() {
		return numBag;
	}
}