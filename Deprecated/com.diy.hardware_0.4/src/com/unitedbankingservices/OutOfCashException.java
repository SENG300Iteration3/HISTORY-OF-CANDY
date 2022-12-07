package com.unitedbankingservices;

/**
 * Represents the situation when a device is emptied of cash but an attempt is
 * made to remove some more.
 */
public class OutOfCashException extends Exception {
	private static final long serialVersionUID = 2260173203212764033L;

	/**
	 * Default constructor.
	 */
	public OutOfCashException() {}
}
