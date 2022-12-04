package com.diy.software.listeners;

import com.diy.hardware.PriceLookUpCode;
import com.diy.software.controllers.PLUCodeControl;

public interface PLUCodeControlListener {
	
	void pluHasBeenUpdated(String pluCode);

	void SubmittedPLUCode(PriceLookUpCode pluCode);
}
