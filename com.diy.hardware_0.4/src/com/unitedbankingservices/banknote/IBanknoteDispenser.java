package com.unitedbankingservices.banknote;

import java.util.List;

import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.IDevice;
import com.unitedbankingservices.OutOfCashException;
import com.unitedbankingservices.TooMuchCashException;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * The base type of devices that dispense banknotes.
 */
public interface IBanknoteDispenser extends IDevice<BanknoteDispenserObserver>{
	/**
	 * Accesses the current number of banknotes in the dispenser. Requires power.
	 * 
	 * @return The number of banknotes currently in the dispenser.
	 */
	int size();

	/**
	 * Allows a set of banknotes to be loaded into the dispenser directly. Existing
	 * banknotes in the dispenser are not removed. Announces "banknotesLoaded"
	 * event. Requires power.
	 * 
	 * @param banknotes
	 *            A sequence of banknotes to be added. Each may not be null.
	 * @throws TooMuchCashException
	 *             if the number of banknotes to be loaded exceeds the capacity of
	 *             the dispenser.
	 * @throws SimulationException
	 *             If any banknote is null.
	 */
	void load(Banknote... banknotes) throws TooMuchCashException;

	/**
	 * Unloads banknotes from the dispenser directly. Announces "banknotesUnloaded"
	 * event. Requires power.
	 * 
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	List<Banknote> unload();

	/**
	 * Returns the maximum capacity of this banknote dispenser. Does not require
	 * power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	int getCapacity();

	/**
	 * Emits a single banknote from this banknote dispenser. If successful,
	 * announces "banknoteRemoved" event. If a successful banknote removal causes
	 * the dispenser to become empty, announces "banknotesEmpty" event. Requires
	 * power.
	 * 
	 * @throws TooMuchCashException
	 *             if the output channel is unable to accept another banknote.
	 * @throws OutOfCashException
	 *             if no banknotes are present in the dispenser to release.
	 * @throws DisabledException
	 *             if the dispenser is currently disabled.
	 */
	void emit() throws OutOfCashException, DisabledException, TooMuchCashException;
}