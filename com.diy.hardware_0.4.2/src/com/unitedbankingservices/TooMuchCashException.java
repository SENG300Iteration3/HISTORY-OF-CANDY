package com.unitedbankingservices;

/**
 * Represents situations where a device has been overloaded with cash.
 */
public class TooMuchCashException extends Exception {
	private static final long serialVersionUID = -242272065739972914L;

	/**
	 * Create an exception without an error message.
	 */
	public TooMuchCashException() {}

	/**
	 * Create an exception with an error message.
	 * 
	 * @param message
	 *            The error message to use.
	 */
	public TooMuchCashException(String message) {
		super(message);
	}
}
