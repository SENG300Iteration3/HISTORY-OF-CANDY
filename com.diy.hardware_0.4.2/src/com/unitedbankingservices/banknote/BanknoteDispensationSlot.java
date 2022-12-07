package com.unitedbankingservices.banknote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unitedbankingservices.AbstractDevice;
import com.unitedbankingservices.DeviceFailureException;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a banknote slot device that can dispense one or more banknotes,
 * leaving them dangling until the customer removes it, via
 * {@link #removeDanglingBanknotes()}. The banknotes are accumulated one at a
 * time inside the device, and then they can be dispensed all at once.
 */
public final class BanknoteDispensationSlot extends AbstractDevice<BanknoteDispensationSlotObserver>
	implements Sink<Banknote> {
	private ArrayList<Banknote> danglingDispensedBanknotes = new ArrayList<>();
	private List<Banknote> banknotesToDispense = new ArrayList<>();
	private final int capacity;

	/**
	 * Creates a banknote slot.
	 * 
	 * @param capacity
	 *            The maximum number of banknotes this slot can accumulate before
	 *            dispensing them.
	 */
	public BanknoteDispensationSlot(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Receives the indicated banknote, adding it to the collection to be dispensed.
	 * Requires power.
	 * 
	 * @param banknote
	 *            The banknote to be received.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws SimulationException
	 *             If the argument is null.
	 * @throws TooMuchCashException
	 *             If the device has accumulated too many banknotes.
	 */
	public synchronized void receive(Banknote banknote) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(banknotesToDispense.size() == capacity)
			throw new TooMuchCashException("The slot cannot accumulate another banknote.");
		
		banknotesToDispense.add(banknote);
	}

	/**
	 * Causes the accumulated banknotes to be dispensed to the customer, left
	 * dangling until removed.
	 */
	public synchronized void dispense() {
		if(!isActivated())
			throw new NoPowerException();

		if(!danglingDispensedBanknotes.isEmpty())
			throw new DeviceFailureException("Attempt to dispense banknotes when the slot is already occupied.");

		danglingDispensedBanknotes.addAll(banknotesToDispense);
		banknotesToDispense.clear();

		notifyBanknotesDispensed(danglingDispensedBanknotes);
	}

	/**
	 * Simulates the user removing a banknote that is dangling from the slot.
	 * Announces "banknoteRemoved" event. Disabling has no effect on this method.
	 * Does not require power.
	 * 
	 * @return The formerly dangling banknote.
	 */
	public synchronized List<Banknote> removeDanglingBanknotes() {
		if(danglingDispensedBanknotes.isEmpty())
			throw new NullPointerSimulationException("danglingEjectedBanknote");

		List<Banknote> banknotes = new ArrayList<>(danglingDispensedBanknotes);
		danglingDispensedBanknotes.clear();
		notifyBanknotesRemoved();

		return banknotes;
	}

	/**
	 * Determines whether this slot has banknotes dangling from it. Does not require
	 * power.
	 * 
	 * @return true if there are dangling banknotes; otherwise, false.
	 */
	public synchronized boolean hasDanglingBanknotes() {
		return !danglingDispensedBanknotes.isEmpty();
	}

	/**
	 * Tests whether a banknote can be accepted by this slot for dispensation.
	 * Requires power.
	 * 
	 * @return True if the slot has space to accumulate one more banknote;
	 *             otherwise, false.
	 */
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			return false;

		return danglingDispensedBanknotes.isEmpty();
	}

	private void notifyBanknotesDispensed(List<Banknote> banknotes) {
		for(BanknoteDispensationSlotObserver observer : observers)
			observer.banknotesDispensed(this, Collections.unmodifiableList(banknotes));
	}

	private void notifyBanknotesRemoved() {
		for(BanknoteDispensationSlotObserver observer : observers)
			observer.banknotesRemoved(this);
	}
}
