package com.diy.software.test.logic;


import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

public class StubSystem implements StationControlListener{
	boolean locked = false;
	boolean paymentStatus = false;
	boolean triggerPaymentWorkflow = false;
	public String paymentType;
	boolean membershipCardInput = false;
	

	@Override
	public void systemControlLocked(StationControl systemControl, boolean isLocked) {
		this.locked = isLocked;
		
	}

	@Override
	public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {
		paymentStatus = true;
		
	}


	@Override
	public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {
		paymentStatus = false;
		
	}


	@Override
	public void paymentsHaveBeenEnabled(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initiatePinInput(StationControl systemControl, String kind) {
		paymentType = kind;
		
	}


	@Override
	public void triggerPanelBack(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void triggerInitialScreen(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void triggerPaymentWorkflow(StationControl systemControl) {
		triggerPaymentWorkflow  = true;
		
	}

	@Override
	public void triggerMembershipWorkflow(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startMembershipCardInput(StationControl systemControl) {
		membershipCardInput = true;
		
	}

	@Override
	public void membershipCardInputFinished(StationControl systemControl) {
		membershipCardInput = false;
		
	}

	@Override
	public void membershipCardInputCanceled(StationControl systemControl, String reason) {
		membershipCardInput = false;
		
	}

	@Override
	public void triggerPurchaseBagsWorkflow(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBagsInStock(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerPurchaseBagsWorkflow(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBagsInStock(StationControl systemControl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
		// TODO Auto-generated method stub
		
	}
	
}
