package com.unitedbankingservices.coin;

import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.unitedbankingservices.AbstractDevice;
import com.unitedbankingservices.DeviceFailureException;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.PassiveSource;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a device for optically and/or physically validating coins. Coins
 * deemed valid are moved to storage; coins deemed invalid are ejected.
 */
public final class CoinValidator extends AbstractDevice<CoinValidatorObserver>
	implements Sink<Coin>, PassiveSource<Coin> {
	/**
	 * Represents the kind of currency supported by this device.
	 */
	public final Currency currency;
	private List<Long> denominations;
	/**
	 * Represents the output sink of this device when a coin is rejected.
	 */
	public Sink<Coin> rejectionSink;
	/**
	 * Represents the output sink of this device when a valid coin cannot be stored
	 * its standard sink.
	 */
	public Sink<Coin> overflowSink;
	/**
	 * Represents the set of output sinks to which to route valid coins, indexed by
	 * the supported denomination of the coins.
	 */
	public final Map<Long, Sink<Coin>> standardSinks;

	/**
	 * Creates a coin validator that recognizes coins of the specified denominations
	 * (i.e., values) and currency.
	 * 
	 * @param currency
	 *            The kind of currency to accept.
	 * @param coinDenominations
	 *            An array of the valid coin denominations (like $0.05, $0.10, etc.)
	 *            to accept. Each value must be &gt;0 and unique in this array.
	 * @throws SimulationException
	 *             If either argument is null.
	 * @throws SimulationException
	 *             If the denominations array does not contain at least one value.
	 * @throws SimulationException
	 *             If any value in the denominations array is non-positive.
	 * @throws SimulationException
	 *             If any value in the denominations array is non-unique.
	 */
	public CoinValidator(Currency currency, List<Long> coinDenominations) {
		if(currency == null)
			throw new NullPointerSimulationException("currency");

		if(coinDenominations == null)
			throw new NullPointerSimulationException("denominations");

		if(coinDenominations.size() < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		this.currency = currency;
		Collections.sort(coinDenominations);

		standardSinks = new HashMap<>();

		for(Long denomination : coinDenominations) {
			if(denomination == null)
				throw new NullPointerSimulationException("denomination instance");

			if(denomination <= 0)
				throw new InvalidArgumentSimulationException(
					"Non-positive denomination detected: " + denomination + ".");

			if(standardSinks.containsKey(denomination))
				throw new InvalidArgumentSimulationException(
					"Each denomination must be unique, but " + denomination + " is repeated.");

			standardSinks.put(denomination, null);
		}

		this.denominations = coinDenominations;
	}

	/**
	 * Connects input and output channels to the coin slot. Causes no events. Does
	 * not require power.
	 * 
	 * @param rejectionSink
	 *            The channel to which rejected coins are routed.
	 * @param overflowSink
	 *            The channel to which valid coins are routed when the normal sink
	 *            is full.
	 * @param standardSinks
	 *            The channels to which valid coins are normally routed. There must
	 *            be one sink to correspond to each valid currency denomination, and
	 *            they must be in the same order as the valid denominations.
	 * @throws SimulationException
	 *             If any argument is null.
	 * @throws SimulationException
	 *             If any standard sink is null.
	 * @throws SimulationException
	 *             If the number of standard sinks differs from the number of
	 *             denominations.
	 * @throws SimulationException
	 *             If any sink is used in more than one position.
	 */
	public void setup(Sink<Coin> rejectionSink, Map<Long, Sink<Coin>> standardSinks, Sink<Coin> overflowSink) {
		if(rejectionSink == null)
			throw new NullPointerSimulationException("rejectionSink");

		if(overflowSink == null)
			throw new NullPointerSimulationException("overflowSink");

		if(standardSinks == null)
			throw new NullPointerSimulationException("standardSinks");

		if(standardSinks.keySet().size() != denominations.size())
			throw new InvalidArgumentSimulationException(
				"The number of standard sinks must equal the number of denominations.");

		this.rejectionSink = rejectionSink;
		this.overflowSink = overflowSink;

		HashSet<Sink<Coin>> set = new HashSet<>();

		for(Long denomination : standardSinks.keySet()) {
			Sink<Coin> sink = standardSinks.get(denomination);
			if(sink == null)
				throw new NullPointerSimulationException("sink for denomination " + denomination);
			else {
				if(set.contains(sink))
					throw new InvalidArgumentSimulationException("Each channel must be unique.");

				set.add(sink);
			}
		}

		if(set.contains(rejectionSink))
			throw new InvalidArgumentSimulationException("Each channel must be unique.");
		else
			set.add(rejectionSink);

		if(set.contains(overflowSink))
			throw new InvalidArgumentSimulationException("Each channel must be unique.");

		this.standardSinks.putAll(standardSinks);
		this.overflowSink = overflowSink;
	}

	private final Random pseudoRandomNumberGenerator = new Random();
	private static final int PROBABILITY_OF_FALSE_REJECTION = 1; /* out of 100 */

	private boolean isValid(Coin coin) {
		if(currency.equals(coin.getCurrency()))
			for(Long denomination : denominations)
				if(denomination.equals(coin.getValue()))
					return pseudoRandomNumberGenerator.nextInt(100) >= PROBABILITY_OF_FALSE_REJECTION;

		return false;
	}

	/**
	 * Tells the coin validator that the indicated coin is being inserted. If the
	 * coin is valid, announces "validCoinDetected" event; otherwise, announces
	 * "invalidCoinDetected" event. Requires power.
	 * <p>
	 * If there is space in the machine to store a valid coin, it is passed to the
	 * sink channel corresponding to the denomination of the coin.
	 * </p>
	 * <p>
	 * If there is no space in the machine to store it or the coin is invalid, the
	 * coin is ejected to the source.
	 * </p>
	 * 
	 * @param coin
	 *            The coin to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the coin validator is currently disabled.
	 * @throws TooMuchCashException
	 *             if the needed sink cannot accept the coin.
	 * @throws SimulationException
	 *             If the coin is null.
	 * @throws SimulationException
	 *             If the coin cannot be delivered.
	 */
	public synchronized void receive(Coin coin) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		if(isValid(coin)) {
			notifyValidCoinDetected(coin);

			Sink<Coin> sink = standardSinks.get(coin.getValue());

			if(sink.hasSpace()) {
				try {
					sink.receive(coin);
				}
				catch(TooMuchCashException e) {
					// Should never happen
					throw e;
				}
			}
			else {
				try {
					overflowSink.receive(coin);
				}
				catch(TooMuchCashException e) {
					// Should never happen
					throw e;
				}
			}
		}
		else {
			notifyInvalidCoinDetected(coin);

			try {
				rejectionSink.receive(coin);
			}
			catch(TooMuchCashException e) {
				// Should never happen
				throw e;
			}
		}
	}

	@Override
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return true; // Since we cannot know yet where a coin will route, assume that it is OK.
	}

	@Override
	public synchronized void reject(Coin cash) {
		if(!isActivated())
			throw new NoPowerException();

		throw new DeviceFailureException(
			"This device cannot reject coins by accepting them from its sinks.  It is now damaged.");
	}

	private void notifyValidCoinDetected(Coin coin) {
		for(CoinValidatorObserver observer : observers)
			observer.validCoinDetected(this, coin.getValue());
	}

	private void notifyInvalidCoinDetected(Coin coin) {
		for(CoinValidatorObserver observer : observers)
			observer.invalidCoinDetected(this);
	}
}
