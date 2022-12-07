package com.diy.software.listeners;

import com.diy.software.controllers.AttendantControl;
import com.unitedbankingservices.coin.CoinStorageUnit;
import com.diy.software.controllers.ReceiptControl;

public interface AttendantControlListener {
	public void attendantApprovedBags(AttendantControl ac);

	public void attendantPreventUse(AttendantControl ac);
	
	/**
	 * used to notify low ink state
	 */
	public void lowInk(AttendantControl ac, String message);
	
	/**
	 * used to notify low paper state
	 */
	public void lowPaper(AttendantControl ac, String message);
	
	/**
	 * changes GUI state to match printer with not low ink and not low paper
	 */
	public void printerNotLowState();
	
	/**
	 * used to notify out of ink state
	 */
	public void outOfInk(AttendantControl ac, String message);

	/**
	 * used to notify out of paper state
	 */
	public void outOfPaper(AttendantControl ac, String message);
	
	/**
	 * allowing attendant to add ink to the printer when the printer is out or low on ink 
	 */
	public void addInkState();
	
	/**
	 * allowing attendant to add paper to the printer when the printer is out or low on paper
	 */
	public void addPaperState();
	
	/**
	 * changes GUI to show weight discrepancy message
	 */
	public void signalWeightDescrepancy(String updateMessage);
	
	/**
	 * state to approve no bagging request
	 */
	public void noBagRequest();
	
	/**
	 * state to indicate item is bagged
	 * cancel noBagRequest if needed
	 */
	public void itemBagged();
	
	/**
	 * returns the attendant station to initial starting state when stations have no issues
	 */
	public void initialState();
	

	/*
	 * allows attendant to load banknotes to storage when amount of banknotes is low
	 */
	public void banknotesInStorageLowState();
	
	/** 
	 * attendant permits customer station use
	 */
	public void attendantPermitStationUse(AttendantControl ac);
	
	/**
	 * notify atendant to that coin to this unit is low, and allow atendant to change it
	 * 
	 * @param amount
	 * 		the amount of max coins to fill (if 10 then fill coins to 10 each)
	 */
	public void coinIsLowState(int amount);

	/**
	 * @param isLoggedIn true if the attendant is logged in, false otherwise
	 */
	public void loggedIn(boolean isLoggedIn);

	/**
	 * Notifies that the banknote dispenser is no longer low
	 */
	public void banknotesNotLowState();

	/**
	 * Notifies that the coin dispenser is no longer in a low state
	 */
	public void coinsNotLowState();

}
