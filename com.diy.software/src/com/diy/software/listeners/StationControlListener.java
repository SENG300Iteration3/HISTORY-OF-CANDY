package com.diy.software.listeners;

import com.diy.software.controllers.StationControl;
import com.jimmyselectronics.opeechee.Card.CardData;

public interface StationControlListener {
	
	/**
	 * An event announcing the lock state of the system
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param isLocked
	 * 			Boolean value signifying locked status
	 */
	public void systemControlLocked(StationControl systemControl, boolean isLocked);
	

	public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason);
	
	/**
	 * An event announcing a payment has been made
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param cardData
	 * 			CardData value for card that made payment
	 */
	public void paymentHasBeenMade(StationControl systemControl, CardData cardData);
	
	/**
	 * An event announcing a payment has been made
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param cardData
	 * 			CardData value for card that made failed the payment
	 * 
	 * @param reason
	 * 			String type reason for failure
	 */
	public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason);
	
	/**
	 * An event announcing the system has entered the payment section has been made
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void paymentsHaveBeenEnabled(StationControl systemControl);
	
	
	public void startMembershipCardInput(StationControl systemControl);
	
	public void membershipCardInputFinished(StationControl systemControl);
	
	public void membershipCardInputCanceled(StationControl systemControl, String reason);
	
	/**
	 * An event meant to prompt the user for pin input for when a card has been inserted
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param kind
	 * 			Kind of card the pin pad is expecting (i.e. debit vs credit)
	 */
	public void initiatePinInput(StationControl systemControl, String kind);
	
	/**
	 * An event specifically for the main gui to perform a backtrack of screens
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerPanelBack(StationControl systemControl);
	
	
	/**
	 * An event specifically for the main gui to perform a backtrack to the initial screen
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerInitialScreen(StationControl systemControl);
	
	/**
	 * An event specifically for the main gui to perform a screen push to the payment workflow
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerPaymentWorkflow(StationControl systemControl);

	/**
	 * An event specifically for the main gui to perform a screen push to the membership workflow\
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerMembershipWorkflow(StationControl systemControl);
	
	/**
	 * An event specifically for the main gui to perform a screen push to the purchase bag workflow\
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerPurchaseBagsWorkflow(StationControl systemControl);
	
	public void noBagsInStock(StationControl systemControl);
	
	public void notEnoughBagsInStock(StationControl systemControl, int numBag);
	
}
