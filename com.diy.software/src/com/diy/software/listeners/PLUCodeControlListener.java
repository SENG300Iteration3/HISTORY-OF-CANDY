package com.diy.software.listeners;

import com.diy.software.controllers.PLUCodeControl;

public interface PLUCodeControlListener {
	
	void pluHasBeenUpdated(PLUCodeControl ppc, String pluCode);
}
