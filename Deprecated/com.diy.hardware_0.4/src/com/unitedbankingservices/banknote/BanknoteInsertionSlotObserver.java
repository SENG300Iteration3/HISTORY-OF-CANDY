package com.unitedbankingservices.banknote;

import com.unitedbankingservices.AbstractDeviceObserver;

/**
 * Observes events emanating from a banknote slot.
 */
public interface BanknoteInsertionSlotObserver extends AbstractDeviceObserver {
	/**
	 * An event announcing that a banknote has been inserted.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	default public void banknoteInserted(BanknoteInsertionSlot slot) {}

	/**
	 * An event announcing that a banknote has been returned to the user, dangling
	 * from the slot.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	default public void banknoteEjected(BanknoteInsertionSlot slot) {}

	/**
	 * An event announcing that a dangling banknote has been removed by the user.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	default public void banknoteRemoved(BanknoteInsertionSlot slot) {}
}
