package com.diy.software.listeners;

import com.diy.software.controllers.AttendantControl;

public interface AttendantControlListener {
	public void attendantApprovedBags(AttendantControl ac);

	public void attendantPreventUse(AttendantControl ac);
	
	/**
	 * allowing attendant to add paper to the printer when the printer is out or low on paper
	 */
	public void addPaperState();
	
	/**
	 * allowing attendant to add ink to the printer when the printer is out or low on ink 
	 */
	public void addInkState();
	
	/**
	 * changes GUI state to match printer with not low ink and not low paper
	 */
	public void printerNotLowState();
	
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
	
	public void attendantPermitStationUse(AttendantControl ac);
	/** 
	 * attendant permits customer station use
	 */
		
}
