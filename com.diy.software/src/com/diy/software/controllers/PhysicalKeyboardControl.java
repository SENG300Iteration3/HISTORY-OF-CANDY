package com.diy.software.controllers;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.nightingale.Keyboard;
import com.jimmyselectronics.nightingale.KeyboardListener;

public class PhysicalKeyboardControl extends KeyboardControl implements KeyboardListener {
	private AttendantControl ac;
	
	public PhysicalKeyboardControl(AttendantControl ac) {
		super();
		this.ac = ac;
		this.ac.station.keyboard.register(this);
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
	public void keyPressed(Keyboard keyboard, String key) {
		// TODO Auto-generated method stub
		this.keyAction(key);
	}

	@Override
	public void keyReleased(Keyboard keyboard, String key) {
		// TODO Auto-generated method stub
	}
}
