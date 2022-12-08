package com.diy.software.listeners;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.unitedbankingservices.coin.CoinStorageUnit;
import com.diy.software.controllers.ReceiptControl;

public interface AttendantControlListener {
	public void attendantApprovedBags(AttendantControl ac);

	public void attendantPreventUse(AttendantControl ac);;
	
	/**
	 *	Changes GUI to allow scanning of items
	 */
	public void attendantApprovedItemRemoval(AttendantControl bc);
	
	/**
	 * allowing attendant to add paper to the printer when the printer is out or low on paper
	 * used to notify low ink state
	 */
	public void lowInk(AttendantControl ac, String message);
	
	/**
	 * used to notify low paper state
	 */
	public void lowPaper(AttendantControl ac, String message);
	
	/**
	 * changes GUI state to match printer with not low ink
	 */
	public void printerNotLowInkState();
	
	/**
	 * changes GUI state to match printer with not low paper
	 */
	public void printerNotLowPaperState();
	
	/**
	 * used to notify out of ink state
	 */
	public void outOfInk(AttendantControl ac, String message);

	/**
	 * used to notify out of paper state
	 */
	public void outOfPaper(AttendantControl ac, String message);
	
	/**
	 * when attendant adds too much ink, simulate not being able to put in anymore ink by turning off refill ink button
	 */
	public void addTooMuchInkState();
	
	/**
	 * when attendant adds too much paper, simulate not being able to put in anymore paper by turning off refill paper button
	 */
	public void addTooMuchPaperState();
	
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

	public void triggerItemSearchScreen(AttendantControl ac);
	
	public void exitTextSearchScreen(AttendantControl ac);

	void printerNotLowState();
}
