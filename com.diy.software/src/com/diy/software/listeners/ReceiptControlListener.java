package com.diy.software.listeners;

import com.diy.software.controllers.ReceiptControl;

public interface ReceiptControlListener {
	
	/**
	 * used to notify out of ink state
	 */
	public String outOfInk(ReceiptControl rc, String message);
	
	/**
	 * used to notify low ink state
	 */
	public String lowInk(ReceiptControl rc, String message);
	
	/**
	 * used to notify out of paper state
	 */
	public String outOfPaper(ReceiptControl rc, String message);
	
	/**
	 * used to notify low paper state
	 */
	public String lowPaper(ReceiptControl rc, String dateTime);
	
	/**
	 * allowing attendant to add paper to the printer when the printer is out or low on paper
	 */
	public void addPaperState();
	
	/**
	 * allowing attendant to add ink to the printer when the printer is out or low on ink 
	 */
	public void addInkState();
	
	/**
	 * changes GUI state to match printer not in low ink and not low paper
	 */
	public void printerNotLowState();
	
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
	
	
}
