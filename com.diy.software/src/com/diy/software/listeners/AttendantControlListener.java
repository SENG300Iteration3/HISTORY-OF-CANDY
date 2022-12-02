package com.diy.software.listeners;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.ReceiptControl;

public interface AttendantControlListener {
	public void attendantApprovedBags(AttendantControl ac);

	public void attendantPreventUse(AttendantControl ac);
	
	/**
	 * used to notify low ink state
	 */
	public String lowInk(ReceiptControl rc, String message);
	
	/**
	 * used to notify low paper state
	 */
	public String lowPaper(ReceiptControl rc, String dateTime);
	
	/**
	 * changes GUI state to match printer with not low ink and not low paper
	 */
	public void printerNotLowState();
	
	/**
	 * used to notify out of ink state
	 */
	public String outOfInk(ReceiptControl rc, String message);

	/**
	 * used to notify out of paper state
	 */
	public String outOfPaper(ReceiptControl rc, String message);
	
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
	 * returns the attendant station to initial starting state when stations have no issues
	 */
	public void initialState();
		
}
