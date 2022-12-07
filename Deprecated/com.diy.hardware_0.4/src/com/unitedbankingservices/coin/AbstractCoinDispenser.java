package com.unitedbankingservices.coin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.unitedbankingservices.AbstractDevice;
import com.unitedbankingservices.DeviceFailureException;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.OutOfCashException;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.Source;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a device that stores coins of a particular denomination to
 * dispense them as change.
 * <p>
 * Coin dispensers can receive coins from other sources. To simplify the
 * simulation, no check is performed on the value of each coin, meaning it is an
 * external responsibility to ensure the correct routing of coins.
 * </p>
 */
public abstract class AbstractCoinDispenser extends AbstractDevice<CoinDispenserObserver>
	implements Source<Coin>, ICoinDispenser {
	private int maxCapacity;
	private Queue<Coin> queue = new LinkedList<Coin>();
	/**
	 * Represents the output sink of this device.
	 */
	public Sink<Coin> sink;

	/**
	 * Creates a coin dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of coins that can be stored in the dispenser.
	 *            Must be positive.
	 * @throws SimulationException
	 *             if capacity is not positive.
	 */
	public AbstractCoinDispenser(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("Capacity must be positive: " + capacity);

		this.maxCapacity = capacity;
	}

	/**
	 * Accesses the current number of coins in the dispenser. Requires power.
	 * 
	 * @return The number of coins currently in the dispenser.
	 */
	@Override
	public synchronized int size() {
		if(!isActivated())
			throw new NoPowerException();

		return queue.size();
	}

	/**
	 * Allows a set of coins to be loaded into the dispenser directly. Existing
	 * coins in the dispenser are not removed. On success, announces "coinsLoaded"
	 * event. Requires power.
	 * 
	 * @param coins
	 *            A sequence of coins to be added. Each cannot be null.
	 * @throws TooMuchCashException
	 *             if the number of coins to be loaded exceeds the capacity of the
	 *             dispenser.
	 * @throws SimulationException
	 *             If any coin is null.
	 */
	@Override
	public synchronized void load(Coin... coins) throws SimulationException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(maxCapacity < queue.size() + coins.length)
			throw new TooMuchCashException("Capacity of dispenser is exceeded by load");

		for(Coin coin : coins)
			if(coin == null)
				throw new NullPointerSimulationException("coin instance");
			else
				queue.add(coin);

		notifyLoad(coins);
	}

	private void notifyLoad(Coin[] coins) {
		for(CoinDispenserObserver observer : observers)
			observer.coinsLoaded(this, coins);
	}

	/**
	 * Unloads coins from the dispenser directly. On success, announces
	 * "coinsUnloaded" event. Requires power.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 */
	@Override
	public synchronized List<Coin> unload() {
		if(!isActivated())
			throw new NoPowerException();

		List<Coin> result = new ArrayList<>(queue);
		queue.clear();

		notifyUnload(result.toArray(new Coin[result.size()]));

		return result;
	}

	private void notifyUnload(Coin[] coins) {
		for(CoinDispenserObserver observer : observers)
			observer.coinsUnloaded(this, coins);
	}

	/**
	 * Returns the maximum capacity of this coin dispenser. Does not require power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	@Override
	public int getCapacity() {
		return maxCapacity;
	}

	/**
	 * Receives and stores the indicated coin. Since not all dispensers support this
	 * operation, it is protected.
	 * 
	 * @param coin
	 *            The coin to store.
	 * @throws TooMuchCashException
	 *             If this device is already full.
	 * @throws DisabledException
	 *             If this device is disabled.
	 * @throws NullPointerSimulationException
	 *             If the indicated coin is null.
	 */
	protected synchronized void receive(Coin coin) throws TooMuchCashException, DisabledException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		if(queue.size() >= maxCapacity)
			throw new TooMuchCashException();

		queue.add(coin);
		notifyCoinAdded(coin);

		if(queue.size() >= maxCapacity)
			notifyCoinsFull();
	}

	/**
	 * Releases a single coin from this coin dispenser. If successful, announces
	 * "coinRemoved" event. If a successful coin removal causes the dispenser to
	 * become empty, announces "coinsEmpty" event. Requires power.
	 * 
	 * @throws TooMuchCashException
	 *             If the output channel is unable to accept another coin.
	 * @throws OutOfCashException
	 *             If no coins are present in the dispenser to release.
	 * @throws DisabledException
	 *             If the dispenser is currently disabled.
	 */
	@Override
	public synchronized void emit() throws TooMuchCashException, OutOfCashException, DisabledException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(queue.size() == 0)
			throw new OutOfCashException();

		Coin coin = queue.remove();

		notifyCoinRemoved(coin);
		sink.receive(coin);

		if(queue.isEmpty())
			notifyCoinsEmpty();
	}

	/**
	 * Checks whether this device has the space available to store one more coin.
	 * Since not all dispensers support the storage of coins in this manner, this
	 * operation is protected. Requires power.
	 * 
	 * @return true if the device has space for one more coin; false, otherwise.
	 */
	protected synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return queue.size() < maxCapacity;
	}

	/**
	 * The dispenser cannot accept rejected coins from its output sink, only from
	 * its input source. Requires power.
	 */
	@Override
	public synchronized void reject(Coin cash) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		throw new DeviceFailureException();
	}

	private void notifyCoinAdded(Coin coin) {
		for(CoinDispenserObserver observer : observers)
			observer.coinAdded(this, coin);
	}

	private void notifyCoinRemoved(Coin coin) {
		for(CoinDispenserObserver observer : observers)
			observer.coinRemoved(this, coin);
	}

	private void notifyCoinsFull() {
		for(CoinDispenserObserver observer : observers)
			observer.coinsFull(this);
	}

	private void notifyCoinsEmpty() {
		for(CoinDispenserObserver observer : observers)
			observer.coinsEmpty(this);
	}
}
