package com.diy.software.controllers;


import com.diy.software.listeners.KeyboardControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.nightingale.Keyboard;
import com.jimmyselectronics.nightingale.KeyboardListener;


public class PhysicalKeyboardControl extends KeyboardControl implements KeyboardListener {
	
	// temp dummy constructor to avoid errors
	public PhysicalKeyboardControl() {
		super(null);
		// TODO Auto-generated constructor stub
	}

//	public PhysicalKeyboardControl(AttendantControl ac) {
//		ac.station.keyboard.register(this);
//	}
	

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
	public void keyPressed(Keyboard keyboard, String label) {
		keyAction(label);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, this.text, label, this.pointer);
		
	}

	@Override
	public void keyReleased(Keyboard keyboard, String label) {
		
		if (label.equals("Shift (Left)")) {
			keyAction(label);
		}
		
	}
}
