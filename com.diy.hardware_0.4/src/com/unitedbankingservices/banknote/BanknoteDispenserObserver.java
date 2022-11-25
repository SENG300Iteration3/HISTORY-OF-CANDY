package com.unitedbankingservices.banknote;

import com.unitedbankingservices.IDeviceObserver;

/**
 * Observes events emanating from a banknote dispenser.
 */
public interface BanknoteDispenserObserver extends IDeviceObserver {
	/**
	 * Called to announce that the indicated banknote dispenser is full of
	 * banknotes.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 */
	default public void moneyFull(IBanknoteDispenser dispenser) {}

	/**
	 * Called to announce that the indicated banknote dispenser is empty of
	 * banknotes.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 */
	default public void banknotesEmpty(IBanknoteDispenser dispenser) {}

	/**
	 * Called to announce that the indicated banknote has been added to the
	 * indicated banknote dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param banknote
	 *            The banknote that was added.
	 */
	default public void billAdded(IBanknoteDispenser dispenser, Banknote banknote) {}

	/**
	 * Called to announce that the indicated banknote has been added to the
	 * indicated banknote dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param banknote
	 *            The banknote that was removed.
	 */
	default public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {}

	/**
	 * Called to announce that the indicated sequence of banknotes has been added to
	 * the indicated banknote dispenser. Used to simulate direct, physical loading
	 * of the dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param banknotes
	 *            The banknotes that were loaded.
	 */
	default public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {}

	/**
	 * Called to announce that the indicated sequence of banknotes has been removed
	 * to the indicated banknote dispenser. Used to simulate direct, physical
	 * unloading of the dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param banknotes
	 *            The banknotes that were unloaded.
	 */
	default public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {}
}
