package com.diy.software.listeners;

import com.diy.software.controllers.PLUCodeControl;

public interface PLUCodeControlListener {
	
	void pluHasBeenUpdated(PLUCodeControl pcc, String pluCode);

	void pluCodeEntered(PLUCodeControl pcc, String pluCode);

	void pluErrorMessageUpdated(PLUCodeControl pcc, String errorMessage);
}
