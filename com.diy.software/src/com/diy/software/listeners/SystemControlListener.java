package com.diy.software.listeners;

import com.diy.software.controllers.SystemControl;
import com.jimmyselectronics.opeechee.Card.CardData;

public interface SystemControlListener {
	
	/**
	 * An event announcing the lock state of the system
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param isLocked
	 * 			Boolean value signifying locked status
	 */
	public void systemControlLocked(SystemControl systemControl, boolean isLocked);
	
	/**
	 * An event announcing a payment has been made
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param cardData
	 * 			CardData value for card that made payment
	 */
	public void paymentHasBeenMade(SystemControl systemControl, CardData cardData);
	
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
	public void paymentHasBeenCanceled(SystemControl systemControl, CardData cardData, String reason);
	
	/**
	 * An event announcing the system has entered the payment section has been made
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void paymentsHaveBeenEnabled(SystemControl systemControl);
	
	
	/**
	 * An event meant to prompt the user for pin input for when a card has been inserted
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 * 
	 * @param kind
	 * 			Kind of card the pin pad is expecting (i.e. debit vs credit)
	 */
	public void initiatePinInput(SystemControl systemControl, String kind);
	
	/**
	 * An event specifically for the main gui to perform a backtrack of screens
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerPanelBack(SystemControl systemControl);
	
	
	/**
	 * An event specifically for the main gui to perform a backtrack to the initial screen
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerInitialScreen(SystemControl systemControl);
	
	/**
	 * An event specifically for the main gui to perform a screen push to the payment workflow
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerPaymentWorkflow(SystemControl systemControl);

	/**
	 * An event specifically for the main gui to perform a screen push to the membership workflow\
	 * 
	 * @param systemControl
	 * 			The default SystemControl unit for the current instance
	 */
	public void triggerMembershipWorkflow(SystemControl systemControl);
	
}
