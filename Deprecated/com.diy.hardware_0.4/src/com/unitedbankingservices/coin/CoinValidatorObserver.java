package com.unitedbankingservices.coin;

import com.unitedbankingservices.IDeviceObserver;

/**
 * Observes events emanating from a coin validator.
 */
public interface CoinValidatorObserver extends IDeviceObserver {
	/**
	 * An event announcing that the indicated coin has been detected and determined
	 * to be valid.
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 * @param value
	 *            The value of the coin.
	 */
	default public void validCoinDetected(CoinValidator validator, long value) {}

	/**
	 * An event announcing that a coin has been detected and determined to be
	 * invalid.
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 */
	default public void invalidCoinDetected(CoinValidator validator) {}
}
