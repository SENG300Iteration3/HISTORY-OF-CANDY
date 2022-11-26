package com.diy.software.listeners;

import com.diy.software.controllers.BagsControl;

public interface BagsControlListener {
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc);

	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc);
	
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc);
}
