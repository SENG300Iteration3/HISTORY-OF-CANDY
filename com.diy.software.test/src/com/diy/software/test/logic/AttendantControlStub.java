package com.diy.software.test.logic;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.listeners.AttendantControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

public class AttendantControlStub implements AttendantControlListener{

	boolean loggedIn;

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendantPreventUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBagRequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loggedIn(boolean isLoggedIn) {
		loggedIn = isLoggedIn;
		
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowInk(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowPaper(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outOfInk(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outOfPaper(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinIsLowState(CoinStorageUnit unit, int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendantApprovedItemRemoval(AttendantControl bc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknotesInStorageLowState() {
    // TODO Auto-generated method stub
    
  }
  
	public void itemBagged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerItemSearchScreen(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitTextSearchScreen(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printerNotLowInkState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printerNotLowPaperState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTooMuchInkState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTooMuchPaperState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printerNotLowState() {
		// TODO Auto-generated method stub
		
	}
	
}
