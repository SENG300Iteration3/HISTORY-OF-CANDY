package com.unitedbankingservices.banknote;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.unitedbankingservices.AbstractDevice;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.OutOfCashException;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * The abstract base class of devices that dispense banknotes.
 */
public abstract class AbstractBanknoteDispenser extends AbstractDevice<BanknoteDispenserObserver>
	implements IBanknoteDispenser {
	/**
	 * Represents the maximum number of banknotes that this device can contain.
	 */
	protected int maxCapacity;
	private Queue<Banknote> queue = new LinkedList<Banknote>();
	/**
	 * Represents the output sink of this dispenser.
	 */
	public Sink<Banknote> sink;

	/**
	 * Basic constructor.
	 * 
	 * @param capacity
	 *            The maximum number of banknotes that this device can contain.
	 * @throws InvalidArgumentSimulationException
	 *             If the capacity is non-positive.
	 */
	protected AbstractBanknoteDispenser(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("Capacity must be positive: " + capacity);

		this.maxCapacity = capacity;
	}

	/**
	 * Accesses the current number of banknotes in the dispenser. Requires power.
	 * 
	 * @return The number of banknotes currently in the dispenser.
	 */
	@Override
	public synchronized int size() {
		if(!isActivated())
			throw new NoPowerException();

		return queue.size();
	}

	/**
	 * Receives and stores the indicated banknote. Since not all dispensers support
	 * this operation, it is protected.
	 * 
	 * @param banknote
	 *            The banknote to store.
	 * @throws TooMuchCashException
	 *             If this device is already full.
	 * @throws DisabledException
	 *             If this device is disabled.
	 * @throws NullPointerSimulationException
	 *             If the indicated banknote is null.
	 */
	protected void receive(Banknote banknote) throws TooMuchCashException, DisabledException {
		if(!isActivated())
			throw new NoPowerException();

		if(banknote == null)
			throw new NullPointerSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(queue.size() + 1 > maxCapacity)
			throw new TooMuchCashException();

		queue.add(banknote);

		notifyBillAdded(banknote);

		if(!hasSpace())
			notifyMoneyFull();
	}

	/**
	 * Checks whether this device has the space available to store one more
	 * banknote. Since not all dispensers support the storage of banknotes in this
	 * manner, this operation is protected. Requires power.
	 * 
	 * @return true if the device has space for one more banknote; false, otherwise.
	 */
	protected boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return queue.size() < maxCapacity;
	}

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
	@Override
	public synchronized void load(Banknote... banknotes) throws TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(maxCapacity < queue.size() + banknotes.length)
			throw new TooMuchCashException("Capacity of dispenser is exceeded by load");

		for(Banknote banknote : banknotes)
			if(banknote == null)
				throw new NullPointerSimulationException("banknote instance");
			else
				queue.add(banknote);

		notifyBanknotesLoaded(banknotes);
	}

	/**
	 * Unloads banknotes from the dispenser directly. Announces "banknotesUnloaded"
	 * event. Requires power.
	 * 
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	@Override
	public synchronized List<Banknote> unload() {
		if(!isActivated())
			throw new NoPowerException();

		List<Banknote> result = new ArrayList<>(queue);
		queue.clear();

		notifyBanknotesUnoaded(result.toArray(new Banknote[result.size()]));

		return result;
	}

	/**
	 * Returns the maximum capacity of this banknote dispenser. Does not require
	 * power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	@Override
	public int getCapacity() {
		return maxCapacity;
	}

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
	@Override
	public synchronized void emit() throws OutOfCashException, DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(queue.size() == 0)
			throw new OutOfCashException();

		Banknote banknote = queue.remove();

		if(sink.hasSpace())
			try {
				sink.receive(banknote);
			}
			catch(TooMuchCashException e) {
				// Should never happen
				throw e;
			}
		else
			throw new TooMuchCashException("The sink is full.");

		notifyBanknoteRemoved(banknote);

		if(queue.isEmpty())
			notifyBanknotesEmpty();
	}

	/**
	 * Convenience operation that notifies all registered observers that a
	 * "banknoteRemoved" event has occurred on this device.
	 * 
	 * @param banknote
	 *            The banknote that has been removed.
	 */
	protected void notifyBanknoteRemoved(Banknote banknote) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknoteRemoved(this, banknote);
	}

	/**
	 * Convenience operation that notifies all registered observers that a
	 * "banknotesEmpty" event has occurred on this device.
	 */
	protected void notifyBanknotesEmpty() {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesEmpty(this);
	}

	/**
	 * Convenience operation that notifies all registered observers that a
	 * "banknotesLoaded" event has occurred on this device.
	 * 
	 * @param banknotes
	 *            The banknotes that have been loaded.
	 */
	protected void notifyBanknotesLoaded(Banknote[] banknotes) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesLoaded(this, banknotes);
	}

	/**
	 * Convenience operation that notifies all registered observers that a
	 * "banknotesUnloaded" event has occurred on this device.
	 * 
	 * @param banknotes
	 *            The banknotes that have been removed.
	 */
	protected void notifyBanknotesUnoaded(Banknote[] banknotes) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesUnloaded(this, banknotes);
	}

	/**
	 * Convenience operation that notifies all registered observers that a
	 * "billAdded" event has occurred on this device.
	 * 
	 * @param banknote
	 *            The banknote that has been removed.
	 */
	protected void notifyBillAdded(Banknote banknote) {
		for(BanknoteDispenserObserver observer : observers)
			observer.billAdded(this, banknote);
	}

	/**
	 * Convenience operation that notifies all registered observers that a
	 * "moneyFull" event has occurred on this device.
	 */
	protected void notifyMoneyFull() {
		for(BanknoteDispenserObserver observer : observers)
			observer.moneyFull(this);
	}
}
