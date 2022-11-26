package com.diy.software.test.logic;


import com.diy.software.controllers.SystemControl;
import com.diy.software.listeners.SystemControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

public class StubSystem implements SystemControlListener{
	boolean locked = false;
	boolean paymentStatus = false;
	boolean triggerPaymentWorkflow = false;
	public String paymentType;
	

	@Override
	public void systemControlLocked(SystemControl systemControl, boolean isLocked) {
		this.locked = isLocked;
		
	}

	@Override
	public void paymentHasBeenMade(SystemControl systemControl, CardData cardData) {
		paymentStatus = true;
		
	}


	@Override
	public void paymentHasBeenCanceled(SystemControl systemControl, CardData cardData, String reason) {
		paymentStatus = false;
		
	}


	@Override
	public void paymentsHaveBeenEnabled(SystemControl systemControl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initiatePinInput(SystemControl systemControl, String kind) {
		paymentType = kind;
		
	}


	@Override
	public void triggerPanelBack(SystemControl systemControl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void triggerInitialScreen(SystemControl systemControl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void triggerPaymentWorkflow(SystemControl systemControl) {
		triggerPaymentWorkflow  = true;
		
	}

	@Override
	public void triggerMembershipWorkflow(SystemControl systemControl) {
		// TODO Auto-generated method stub
		
	}
	
}
