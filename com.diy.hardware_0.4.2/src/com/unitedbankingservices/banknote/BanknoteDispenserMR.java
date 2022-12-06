package com.unitedbankingservices.banknote;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a device that stores banknotes (as known as banknotes, paper money,
 * etc.) of a particular denomination to dispense them as change. This device
 * cannot receive additional banknotes automatically, but must be manually
 * reloaded.
 */
public final class BanknoteDispenserMR extends AbstractBanknoteDispenser {
	/**
	 * Creates a banknote dispenser that cannot be automatically refilled, with the
	 * indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of banknotes that can be stored in the
	 *            dispenser. Must be positive.
	 * @throws SimulationException
	 *             If capacity is not positive.
	 */
	public BanknoteDispenserMR(int capacity) {
		super(capacity);
	}
}
