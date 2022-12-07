package com.unitedbankingservices.coin;

import com.unitedbankingservices.IDeviceObserver;

/**
 * Observes events emanating from a coin slot.
 */
public interface CoinSlotObserver extends IDeviceObserver {
	/**
	 * An event announcing that a coin has been inserted.
	 * 
	 * @param slot
	 *             The device on which the event occurred.
	 */
	void coinInserted(CoinSlot slot);
}
