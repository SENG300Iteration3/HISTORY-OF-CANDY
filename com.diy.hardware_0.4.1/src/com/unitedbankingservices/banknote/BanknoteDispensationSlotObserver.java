package com.unitedbankingservices.banknote;

import java.util.List;

import com.unitedbankingservices.AbstractDeviceObserver;

/**
 * Observes events emanating from a banknote dispensation slot.
 */
public interface BanknoteDispensationSlotObserver extends AbstractDeviceObserver {
	/**
	 * An event announcing that banknotes have been dispensed to the user, dangling
	 * from the slot.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 * @param banknotes
	 *            The banknotes that were dispensed. Cannot be null.
	 */
	default public void banknotesDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {}

	/**
	 * An event announcing that dangling banknotes have been removed by the user.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	default public void banknotesRemoved(BanknoteDispensationSlot slot) {}
}
