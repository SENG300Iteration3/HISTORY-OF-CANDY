package com.diy.software.listeners;

import com.diy.hardware.PriceLookUpCode;
import com.diy.software.controllers.PLUCodeControl;

public interface PLUCodeControlListener {
	
	void pluHasBeenUpdated(PLUCodeControl ppc, String pluCode);

	void pluCodeEntered(PriceLookUpCode pluCode);
}
