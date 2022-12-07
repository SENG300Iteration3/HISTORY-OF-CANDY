package com.unitedbankingservices;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * A simple interface for devices that emit cash.
 * 
 * @param <T>
 *            The type of the cash to emit.
 */
public interface Source<T> {
	/**
	 * Instructs the device to emit one arbitrary item of cash, meaning that the
	 * device stores a set of items of cash and one of them is to be emitted.
	 * Requires power.
	 * 
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws OutOfCashException
	 *             If the device is empty and cannot emit.
	 * @throws TooMuchCashException
	 *             If the receiving device is already full.
	 */
	public void emit() throws DisabledException, OutOfCashException, TooMuchCashException;

	/**
	 * Instructs the device to pass one specific item of cash backwards. Requires
	 * power.
	 * 
	 * @param cash
	 *            The item of cash to be emitted.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws SimulationException
	 *             If the item of cash is null.
	 * @throws TooMuchCashException
	 *             If the receiving device is already full.
	 */
	public void reject(T cash) throws DisabledException, TooMuchCashException;
}
