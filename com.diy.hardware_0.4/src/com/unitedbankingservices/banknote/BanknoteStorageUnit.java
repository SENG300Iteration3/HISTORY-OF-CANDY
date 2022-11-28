package com.unitedbankingservices.banknote;

import java.util.Arrays;
import java.util.List;

import com.unitedbankingservices.AbstractDevice;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents devices that store banknotes. They only receive banknotes, not
 * dispense them. To access the banknotes inside, a human operator needs to
 * physically remove the banknotes, simulated with the {@link #unload()} method.
 * A {@link #load(Banknote...)} method is provided for symmetry.
 */
public class BanknoteStorageUnit extends AbstractDevice<BanknoteStorageUnitObserver> implements Sink<Banknote> {
	private Banknote[] storage;
	private int nextIndex = 0;

	/**
	 * Creates a banknote storage unit that can hold the indicated number of
	 * banknotes.
	 * 
	 * @param capacity
	 *            The maximum number of banknotes that the unit can hold.
	 * @throws SimulationException
	 *             If the capacity is not positive.
	 */
	public BanknoteStorageUnit(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("The capacity must be positive.");

		storage = new Banknote[capacity];
	}

	/**
	 * Gets the maximum number of banknotes that this storage unit can hold.
	 * Disabling has no effect on this method. Does not require power.
	 * 
	 * @return The capacity.
	 */
	public int getCapacity() {
		return storage.length;
	}

	/**
	 * Gets the current count of banknotes contained in this storage unit. Disabling
	 * has no effect on this method. Requires power.
	 * 
	 * @return The current count.
	 */
	public synchronized int getBanknoteCount() {
		if(!isActivated())
			throw new NoPowerException();

		return nextIndex;
	}

	/**
	 * Allows a set of banknotes to be loaded into the storage unit directly.
	 * Existing banknotes in the dispenser are not removed. Announces
	 * "banknotesLoaded" event. Disabling has no effect on this method. Requires
	 * power.
	 * 
	 * @param banknotes
	 *            A sequence of banknotes to be added. Each cannot be null.
	 * @throws SimulationException
	 *             if the number of banknotes to be loaded exceeds the capacity of
	 *             the unit.
	 * @throws SimulationException
	 *             If the banknotes argument is null.
	 * @throws SimulationException
	 *             If any banknote is null.
	 * @throws TooMuchCashException
	 *             If too many banknotes are stuffed in the unit.
	 */
	public synchronized void load(Banknote... banknotes) throws SimulationException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(banknotes == null)
			throw new NullPointerSimulationException("banknotes");

		if(banknotes.length + nextIndex > storage.length)
			throw new TooMuchCashException("You tried to stuff too many banknotes in the storage unit.");

		for(Banknote banknote : banknotes)
			if(banknote == null)
				throw new NullPointerSimulationException("banknote instance");

		System.arraycopy(banknotes, 0, storage, nextIndex, banknotes.length);
		nextIndex += banknotes.length;

		notifyBanknotesLoaded();
	}

	/**
	 * Unloads banknotes from the storage unit directly. Announces
	 * "banknotesUnloaded" event. Disabling has no effect on this method. Requires
	 * power.
	 * 
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	public synchronized List<Banknote> unload() {
		if(!isActivated())
			throw new NoPowerException();

		List<Banknote> banknotes = Arrays.asList(storage);

		storage = new Banknote[storage.length];
		nextIndex = 0;
		notifyBanknotesUnloaded();

		return banknotes;
	}

	/**
	 * Causes the indicated banknote to be added to the storage unit. If successful,
	 * announces "banknoteAdded" event. If a successful banknote addition causes the
	 * unit to become full, announces "banknotesFull" event. Requires power.
	 * 
	 * @param banknote
	 *            The banknote to add.
	 * @throws DisabledException
	 *             If the unit is currently disabled.
	 * @throws SimulationException
	 *             If banknote is null.
	 * @throws TooMuchCashException
	 *             If the unit is already full.
	 */
	public synchronized void receive(Banknote banknote) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(nextIndex < storage.length) {
			storage[nextIndex++] = banknote;

			notifyBanknoteAdded();

			if(nextIndex == storage.length)
				notifyBanknotesFull();
		}
		else
			throw new TooMuchCashException();
	}

	@Override
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return nextIndex < storage.length;
	}

	private void notifyBanknotesLoaded() {
		for(BanknoteStorageUnitObserver observer : observers)
			observer.banknotesLoaded(this);
	}

	private void notifyBanknotesUnloaded() {
		for(BanknoteStorageUnitObserver observer : observers)
			observer.banknotesUnloaded(this);
	}

	private void notifyBanknotesFull() {
		for(BanknoteStorageUnitObserver l : observers)
			l.banknotesFull(this);
	}

	private void notifyBanknoteAdded() {
		for(BanknoteStorageUnitObserver l : observers)
			l.banknoteAdded(this);
	}
}
