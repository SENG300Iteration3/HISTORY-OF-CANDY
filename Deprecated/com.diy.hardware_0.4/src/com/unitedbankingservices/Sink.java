package com.unitedbankingservices;

/**
 * Represents an abstract device that can receive cash.
 * 
 * @param <T>
 *            The type of the cash to be transported.
 */
public interface Sink<T> {
	/**
	 * Places the indicated cash into the sink. Requires power.
	 * 
	 * @param cash
	 *            The cash to place in the sink.
	 * @throws TooMuchCashException
	 *             If the sink has no space for the cash.
	 * @throws DisabledException
	 *             If the sink is currently disabled.
	 */
	public void receive(T cash) throws TooMuchCashException, DisabledException;

	/**
	 * Returns whether the sink has space for at least one more item of cash.
	 * Requires power.
	 * 
	 * @return true if the sink can accept an item of cash; false otherwise.
	 */
	public boolean hasSpace();
}
