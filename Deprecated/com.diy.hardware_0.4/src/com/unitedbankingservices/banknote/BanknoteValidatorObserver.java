package com.unitedbankingservices.banknote;

import java.util.Currency;

import com.unitedbankingservices.IDeviceObserver;

/**
 * Observes events emanating from a banknote validator.
 */
public interface BanknoteValidatorObserver extends IDeviceObserver {
	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be valid.
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 * @param currency
	 *            The kind of currency of the inserted banknote.
	 * @param value
	 *            The value of the inserted banknote.
	 */
	default public void validBanknoteDetected(BanknoteValidator validator, Currency currency, long value) {}

	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be invalid.
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 */
	default public void invalidBanknoteDetected(BanknoteValidator validator) {}
}
