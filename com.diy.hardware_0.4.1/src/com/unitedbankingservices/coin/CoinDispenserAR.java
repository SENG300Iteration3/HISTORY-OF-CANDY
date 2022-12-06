package com.unitedbankingservices.coin;

import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.PassiveSource;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a device that stores coins of a particular denomination to
 * dispense them as change. This device can be automatically refilled from its
 * source.
 */
public final class CoinDispenserAR extends AbstractCoinDispenser implements Sink<Coin> {
	/**
	 * Represents the input source of this dispenser.
	 */
	public PassiveSource<Coin> source;
	
	/**
	 * Creates a coin dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of coins that can be stored in the dispenser.
	 *            Must be positive.
	 * @throws SimulationException
	 *             if capacity is not positive.
	 */
	public CoinDispenserAR(int capacity) {
		super(capacity);
	}

	/**
	 * Causes the indicated coin to be added into the dispenser. If successful,
	 * announces "coinAdded" event. If a successful coin addition causes the
	 * dispenser to become full, announces "coinsFull" event. Requires power.
	 * 
	 * @throws DisabledException
	 *             If the coin dispenser is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws TooMuchCashException
	 *             If the coin dispenser is already full.
	 */
	@Override
	public synchronized void receive(Coin coin) throws TooMuchCashException, DisabledException {
		super.receive(coin);
	}

	/**
	 * Returns whether this coin dispenser has enough space to accept at least one
	 * more coin. Announces no events. Requires power.
	 */
	@Override
	public synchronized boolean hasSpace() {
		return super.hasSpace();
	}

	@Override
	public synchronized void reject(Coin cash) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();
		
		source.reject(cash);
	}
}
