package com.unitedbankingservices.coin;

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
 * Represents devices that store coins. They only receive coins, not dispense
 * them. To access the coins inside, a human operator needs to physically remove
 * the coins, simulated with the {@link #unload()} method. A
 * {@link #load(Coin...)} method is provided for symmetry.
 */
public class CoinStorageUnit extends AbstractDevice<CoinStorageUnitObserver> implements Sink<Coin> {
	private Coin[] storage;
	private int nextIndex = 0;

	/**
	 * Creates a coin storage unit that can hold the indicated number of coins.
	 * 
	 * @param capacity
	 *            The maximum number of coins that the unit can hold.
	 * @throws SimulationException
	 *             If the capacity is not positive.
	 */
	public CoinStorageUnit(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("The capacity must be positive.");

		storage = new Coin[capacity];
	}

	/**
	 * Gets the maximum number of coins that this storage unit can hold. Does not
	 * require power.
	 * 
	 * @return The capacity.
	 */
	public int getCapacity() {
		return storage.length;
	}

	/**
	 * Gets the current count of coins contained in this storage unit. Requires
	 * power.
	 * 
	 * @return The current count.
	 */
	public synchronized int getCoinCount() {
		if(!isActivated())
			throw new NoPowerException();

		return nextIndex;
	}

	/**
	 * Allows a set of coins to be loaded into the storage unit directly. Existing
	 * coins in the dispenser are not removed. Announces "coinsLoaded" event.
	 * Disabling has no effect on loading/unloading. Requires power.
	 * 
	 * @param coins
	 *            A sequence of coins to be added. Each cannot be null.
	 * @throws SimulationException
	 *             if the number of coins to be loaded exceeds the capacity of the
	 *             unit.
	 * @throws SimulationException
	 *             If coins is null.
	 * @throws SimulationException
	 *             If any coin is null.
	 * @throws TooMuchCashException
	 *             If too many coins are loaded.
	 */
	public synchronized void load(Coin... coins) throws SimulationException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(coins == null)
			throw new NullPointerSimulationException("coins");

		if(coins.length + nextIndex > storage.length)
			throw new TooMuchCashException("You tried to stuff too many coins in the storage unit.");

		for(Coin coin : coins)
			if(coin == null)
				throw new NullPointerSimulationException("coin instance");

		System.arraycopy(coins, 0, storage, nextIndex, coins.length);
		nextIndex += coins.length;

		notifyCoinsLoaded();
	}

	/**
	 * Unloads coins from the storage unit directly. Announces "coinsUnloaded"
	 * event. Requires power.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 */
	public synchronized List<Coin> unload() {
		if(!isActivated())
			throw new NoPowerException();

		List<Coin> coins = Arrays.asList(storage);

		storage = new Coin[storage.length];
		nextIndex = 0;
		notifyCoinsUnloaded();

		return coins;
	}

	/**
	 * Causes the indicated coin to be added to the storage unit. If successful,
	 * announces "coinAdded" event. If a successful coin addition instead causes the
	 * unit to become full, announces "coinsFull" event. Requires power.
	 * 
	 * @param coin
	 *            The coin to add to this unit.
	 * @throws DisabledException
	 *             If the unit is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws TooMuchCashException
	 *             If the unit is already full.
	 */
	public synchronized void receive(Coin coin) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		if(nextIndex < storage.length) {
			storage[nextIndex++] = coin;

			notifyCoinAdded();

			if(nextIndex == storage.length)
				notifyCoinsFull();
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

	private void notifyCoinsLoaded() {
		for(CoinStorageUnitObserver observer : observers)
			observer.coinsLoaded(this);
	}

	private void notifyCoinsUnloaded() {
		for(CoinStorageUnitObserver observer : observers)
			observer.coinsUnloaded(this);
	}

	private void notifyCoinsFull() {
		for(CoinStorageUnitObserver observer : observers)
			observer.coinsFull(this);
	}

	private void notifyCoinAdded() {
		for(CoinStorageUnitObserver observer : observers)
			observer.coinAdded(this);
	}
}
