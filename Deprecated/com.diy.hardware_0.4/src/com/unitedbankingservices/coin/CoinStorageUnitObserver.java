package com.unitedbankingservices.coin;

import com.unitedbankingservices.IDeviceObserver;

/**
 * Observes events emanating from a coin storage unit.
 */
public interface CoinStorageUnitObserver extends IDeviceObserver {
	/**
	 * Announces that the indicated coin storage unit is full of coins.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	default public void coinsFull(CoinStorageUnit unit) {}

	/**
	 * Announces that a coin has been added to the indicated storage unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	default public void coinAdded(CoinStorageUnit unit) {}

	/**
	 * Announces that the indicated storage unit has been loaded with coins. Used to
	 * simulate direct, physical loading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	default public void coinsLoaded(CoinStorageUnit unit) {}

	/**
	 * Announces that the storage unit has been emptied of coins. Used to simulate
	 * direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	default public void coinsUnloaded(CoinStorageUnit unit) {}
}
