package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.listeners.BagDispenserControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.svenden.ReusableBagDispenser;
import com.jimmyselectronics.svenden.ReusableBagDispenserListener;

public class BagDispenserControl implements ActionListener {
	private StationControl sc;
	private ArrayList<BagDispenserControlListener> listeners;
	private int numBag;
	private String input = "";

	public BagDispenserControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("NUMBER_BAGS: ")) {
			input += c.split(" ")[1];
			System.out.println(input);
			for (BagDispenserControlListener l : listeners)
				l.numberFieldHasBeenUpdated(this, input);
		} else {
			switch (c) {
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
					//dispense
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

//	@Override
//	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void bagDispensed(ReusableBagDispenser dispenser) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void outOfBags(ReusableBagDispenser dispenser) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void bagsLoaded(ReusableBagDispenser dispenser, int count) {
//		// TODO Auto-generated method stub
//		
//	}
}