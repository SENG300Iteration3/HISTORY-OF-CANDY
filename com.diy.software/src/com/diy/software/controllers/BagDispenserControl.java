package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.listeners.MembershipControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.svenden.ReusableBagDispenser;
import com.jimmyselectronics.svenden.ReusableBagDispenserListener;

public class BagDispenserControl implements ActionListener, ReusableBagDispenserListener {
	private StationControl sc;
	private ArrayList<ReusableBagDispenserListener> listeners;
	private int numBag;

	public BagDispenserControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("MEMBER_INPUT_BUTTON: ")) {
			memberNumber += c.split(" ")[1];
			for (MembershipControlListener l : listeners)
				l.memberFieldHasBeenUpdated(this, memberNumber);
		}
	}
	
	public void addListener(ReusableBagDispenserListener l) {
		listeners.add(l);
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bagDispensed(ReusableBagDispenser dispenser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outOfBags(ReusableBagDispenser dispenser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bagsLoaded(ReusableBagDispenser dispenser, int count) {
		// TODO Auto-generated method stub
		
	}
}