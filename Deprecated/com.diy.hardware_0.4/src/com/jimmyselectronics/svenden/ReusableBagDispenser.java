package com.jimmyselectronics.svenden;

import java.util.ArrayList;
import java.util.Arrays;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.EmptyException;
import com.jimmyselectronics.OverloadException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents the reusable-bag dispenser.
 * 
 * @author Jimmy's Electronics LLP
 */
public class ReusableBagDispenser extends AbstractDevice<ReusableBagDispenserListener> {
	private ArrayList<ReusableBag> bags = new ArrayList<ReusableBag>();
	private int capacity;

	/**
	 * Basic constructor permitting the capacity to be set.
	 * 
	 * @param capacity
	 *            The maximum number of bags that the dispenser can contain.
	 * @throws InvalidArgumentSimulationException
	 *             if capacity &le;0.
	 */
	public ReusableBagDispenser(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("The capacity must be a positive integer.");

		this.capacity = capacity;
	}

	/**
	 * Obtains the maximum capacity of the dispenser.
	 * 
	 * @return The maximum capacity.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Adds the indicated bags to the dispenser. Causes a "bags loaded" event to be
	 * announced. Requires power.
	 * 
	 * @param bags
	 *            The bags to be added.
	 * @throws OverloadException
	 *             if the new total number of bags would exceed the capacity of the
	 *             dispenser.
	 */
	public void load(ReusableBag... bags) throws OverloadException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(bags == null)
			throw new NullPointerSimulationException();

		if(this.bags.size() + bags.length > capacity)
			throw new OverloadException("You have tried to stuff the dispenser with too many bags");

		this.bags.addAll(Arrays.asList(bags));

		notifyBagsLoaded(bags.length);
	}

	/**
	 * Removes all the bags from the dispenser. Causes an "out of bags" event to be
	 * announced. Requires power.
	 * 
	 * @return The bags formerly in the dispenser.
	 */
	public ReusableBag[] unload() {
		if(!isPoweredUp())
			throw new NoPowerException();

		ReusableBag[] array = bags.toArray(new ReusableBag[bags.size()]);
		bags.clear();
		notifyOutOfBags();

		return array;
	}

	/**
	 * Dispenses one bag to the customer. Causes a "bag dispensed" event to be
	 * announced. May cause an "out of bags" event to also be announced, if the
	 * dispensed bag is the last one in the dispenser. Requires power.
	 * 
	 * @return The dispensed bag.
	 * @throws EmptyException
	 *             if the dispenser contained no bags when this method was called.
	 */
	public ReusableBag dispense() throws EmptyException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(bags.isEmpty())
			throw new EmptyException("Out of bags");

		ReusableBag bag = bags.remove(bags.size() - 1);
		notifyBagDispensed();

		if(bags.isEmpty())
			notifyOutOfBags();

		return bag;
	}

	private void notifyBagsLoaded(int count) {
		for(ReusableBagDispenserListener listener : listeners())
			listener.bagsLoaded(this, count);
	}

	private void notifyBagDispensed() {
		for(ReusableBagDispenserListener listener : listeners())
			listener.bagDispensed(this);
	}

	private void notifyOutOfBags() {
		for(ReusableBagDispenserListener listener : listeners())
			listener.outOfBags(this);
	}
}
