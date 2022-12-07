package com.unitedbankingservices.coin;

import java.util.List;

import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.IDevice;
import com.unitedbankingservices.OutOfCashException;
import com.unitedbankingservices.TooMuchCashException;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * The base type of devices that dispense coins.
 */
public interface ICoinDispenser extends IDevice<CoinDispenserObserver> {
	/**
	 * Accesses the current number of coins in the dispenser. Requires power.
	 * 
	 * @return The number of coins currently in the dispenser.
	 */
	int size();

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
	void load(Coin... coins) throws SimulationException, TooMuchCashException;

	/**
	 * Unloads coins from the dispenser directly. On success, announces
	 * "coinsUnloaded" event. Requires power.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 */
	List<Coin> unload();

	/**
	 * Returns the maximum capacity of this coin dispenser. Does not require power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	int getCapacity();

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
	void emit() throws TooMuchCashException, OutOfCashException, DisabledException;

	/**
	 * The dispenser cannot accept rejected coins from its output sink, only from
	 * its input source. Requires power.
	 * 
	 * @param coin
	 *            The coin to reject.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws TooMuchCashException
	 *             If the device is already full.
	 */
	void reject(Coin coin) throws DisabledException, TooMuchCashException;
}