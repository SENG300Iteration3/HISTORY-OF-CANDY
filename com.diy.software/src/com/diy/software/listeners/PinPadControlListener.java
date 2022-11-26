package com.diy.software.listeners;

import com.diy.software.controllers.PinPadControl;

public interface PinPadControlListener {
	
	public void pinHasBeenUpdated(PinPadControl ppc, String pin);
}
