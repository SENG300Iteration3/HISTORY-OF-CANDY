package com.unitedbankingservices.banknote;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
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
 * Represents a device for optically and/or magnetically validating banknotes.
 * Banknotes deemed valid are moved to storage; banknotes deemed invalid are
 * ejected.
 */
public final class BanknoteValidator extends AbstractDevice<BanknoteValidatorObserver>
	implements Sink<Banknote>, PassiveSource<Banknote> {
	/**
	 * Represents the kind of currency supported by this device.
	 */
	public final Currency currency;
	private final int[] denominations;
	/**
	 * Represents the input source for the validator.
	 */
	public PassiveSource<Banknote> source;
	/**
	 * Represents the output sink for the validator.
	 */
	public Sink<Banknote> sink;

	/**
	 * Creates a banknote validator that recognizes banknotes of the specified
	 * denominations (i.e., values) and currency.
	 * 
	 * @param currency
	 *            The kind of currency to accept.
	 * @param denominations
	 *            An array of the valid banknote denominations (like $5, $10, etc.)
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
	public BanknoteValidator(Currency currency, int[] denominations) {
		if(currency == null)
			throw new NullPointerSimulationException("currency");

		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		this.currency = currency;
		Arrays.sort(denominations);

		HashSet<Integer> set = new HashSet<>();

		for(int denomination : denominations) {
			if(denomination <= 0)
				throw new InvalidArgumentSimulationException(
					"Non-positive denomination detected: " + denomination + ".");

			if(set.contains(denomination))
				throw new InvalidArgumentSimulationException(
					"Each denomination must be unique, but " + denomination + " is repeated.");

			set.add(denomination);
		}

		this.denominations = denominations;
	}

	private final Random pseudoRandomNumberGenerator = new Random();
	private static final int PROBABILITY_OF_FALSE_REJECTION = 1; /* out of 100 */

	private synchronized boolean isValid(Banknote banknote) {
		if(currency.equals(banknote.getCurrency()))
			for(int denomination : denominations)
				if(denomination == banknote.getValue())
					return pseudoRandomNumberGenerator.nextInt(100) >= PROBABILITY_OF_FALSE_REJECTION;

		return false;
	}

	/**
	 * Tells the banknote validator that the indicated banknote is being inserted.
	 * If the banknote is valid, announces "validBanknoteDetected" event; otherwise,
	 * announces "invalidBanknoteDetected" event. Requires power.
	 * <p>
	 * If there is space in the machine to store a valid banknote, it is passed to
	 * the sink channel.
	 * </p>
	 * <p>
	 * If there is no space in the machine to store it or the banknote is invalid,
	 * the banknote is ejected to the source.
	 * </p>
	 * 
	 * @param banknote
	 *            The banknote to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the banknote validator is currently disabled.
	 * @throws SimulationException
	 *             If the banknote is null.
	 */
	@Override
	public synchronized void receive(Banknote banknote) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(isValid(banknote)) {
			notifyValidBanknoteDetected(banknote);

			if(sink.hasSpace()) {
				try {
					sink.receive(banknote);
				}
				catch(TooMuchCashException e) {
					// Should never happen
					throw e;
				}
			}
			else {
				try {
					source.reject(banknote);
				}
				catch(TooMuchCashException e) {
					// Should never happen
					throw e;
				}
			}
		}
		else {
			notifyInvalidBanknoteDetected();

			try {
				source.reject(banknote);
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

		return true;
	}

	@Override
	public synchronized void reject(Banknote cash) {
		if(!isActivated())
			throw new NoPowerException();

		throw new DeviceFailureException(
			"This device cannot reject coins by accepting them from its sinks.  It is now damaged.");
	}

	private void notifyValidBanknoteDetected(Banknote banknote) {
		for(BanknoteValidatorObserver observer : observers)
			observer.validBanknoteDetected(this, banknote.getCurrency(), banknote.getValue());
	}

	private void notifyInvalidBanknoteDetected() {
		for(BanknoteValidatorObserver observer : observers)
			observer.invalidBanknoteDetected(this);
	}
}
