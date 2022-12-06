package com.unitedbankingservices;

/**
 * A simple interface for devices that emit cash, not on demand but when some
 * external action has caused it.
 * 
 * @param <T>
 *            The type of the cash to emit.
 */
public interface PassiveSource<T> {
	/**
	 * Allows a device to reject an item of cash, forcing it back to the source. Not
	 * all passive sources may support this. Requires power.
	 * 
	 * @param cash
	 *            The item of cash to reject.
	 * @throws DisabledException
	 *             If the device at the end of the channel receiving the cash is
	 *             disabled.
	 * @throws DeviceFailureException
	 *             If the device at the end of the channel receiving the cash is not
	 *             capable of rejecting cash.
	 * @throws TooMuchCashException
	 *             If the device at the end of the channel receiving the cash is too
	 *             full.
	 */
	public void reject(T cash) throws TooMuchCashException, DisabledException, DeviceFailureException;
}
