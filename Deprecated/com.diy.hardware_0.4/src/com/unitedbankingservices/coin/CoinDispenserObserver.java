package com.unitedbankingservices.coin;

import com.unitedbankingservices.IDeviceObserver;

/**
 * Observes events emanating from a coin dispenser.
 */
public interface CoinDispenserObserver extends IDeviceObserver {
	/**
	 * Announces that the indicated coin dispenser is full of coins.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 */
	default public void coinsFull(ICoinDispenser dispenser) {}

	/**
	 * Announces that the indicated coin dispenser is empty of coins.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 */
	default public void coinsEmpty(ICoinDispenser dispenser) {}

	/**
	 * Announces that the indicated coin has been added to the indicated coin
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coin
	 *            The coin that was added.
	 */
	default public void coinAdded(ICoinDispenser dispenser, Coin coin) {}

	/**
	 * Announces that the indicated coin has been added to the indicated coin
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coin
	 *            The coin that was removed.
	 */
	default public void coinRemoved(ICoinDispenser dispenser, Coin coin) {}

	/**
	 * Announces that the indicated sequence of coins has been added to the
	 * indicated coin dispenser. Used to simulate direct, physical loading of the
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coins
	 *            The coins that were loaded.
	 */
	default public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {}

	/**
	 * Announces that the indicated sequence of coins has been removed to the
	 * indicated coin dispenser. Used to simulate direct, physical unloading of the
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coins
	 *            The coins that were unloaded.
	 */
	default public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {}
}
