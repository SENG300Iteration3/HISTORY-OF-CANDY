package com.unitedbankingservices;

/**
 * Issued when a device cannot perform its usual functions because it has
 * malfunctioned.
 */
public class DeviceFailureException extends RuntimeException {
	private static final long serialVersionUID = -7908036729842917892L;

	/**
	 * Default constructor.
	 */
	public DeviceFailureException() {
		super();
	}

	/**
	 * Constructor permitting a message to be specified.
	 * 
	 * @param message
	 *            The message to pass on.
	 */
	public DeviceFailureException(String message) {
		super(message);
	}
}
