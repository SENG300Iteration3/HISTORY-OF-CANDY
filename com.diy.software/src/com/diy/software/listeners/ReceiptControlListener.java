package com.diy.software.listeners;

import com.diy.software.controllers.ReceiptControl;

public interface ReceiptControlListener {
	
	/**
	 * used to notify customer why the station is locked
	 */
	public void outOfInkOrPaper(ReceiptControl rc, String message);
	
	/**
	 * used to pass a list of checked out items
	 */
	public void setCheckedoutItems(ReceiptControl rc, String message);
	
	/**
	 * use to pass total cost of checked out items
	 */
	public void setTotalCost(ReceiptControl rc, String totalCost);
	
	/**
	 * used to pass the date and time receipt is printed
	 */
	public void setDateandTime(ReceiptControl rc, String dateTime);
	
	/**
	 * thank you message
	 */
	public void setThankyouMessage(ReceiptControl rc, String dateTime);
	
	/**
	 * successfully printed receipt and activates take receipt button
	 */
	public void setTakeReceiptState(ReceiptControl rc);
	
	/**
	 * turn off take receipt button 
	 */
	public void setNoReceiptState(ReceiptControl rc);
	
	
}
