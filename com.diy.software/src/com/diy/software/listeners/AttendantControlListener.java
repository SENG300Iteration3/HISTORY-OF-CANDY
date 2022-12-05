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
	 * returns the attendant station to initial starting state when stations have no issues
	 */
	public void initialState();
	
	/** 
	 * attendant permits customer station use
	 */
	public void attendantPermitStationUse(AttendantControl ac);
	
	/**
	 * notify atendant to that coin to this unit is low, and allow atendant to change it
	 * @param unit
	 * 		the unit that needs refilling
	 * 
	 * @param amount
	 * 		the amount of max coins to fill (if 10 then fill coins to 10 each)
	 */
	public void coinIsLowState(CoinStorageUnit unit, int amount);
		
}
