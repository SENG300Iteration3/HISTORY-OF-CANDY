package com.unitedbankingservices.coin;

import com.unitedbankingservices.AbstractDevice;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.Sink;
import com.unitedbankingservices.TooMuchCashException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a simple coin slot device that has one output channel. The slot is
 * stupid: it has no functionality other than being enabled/disabled, and cannot
 * determine the value and currency of the coin.
 */
public final class CoinSlot extends AbstractDevice<CoinSlotObserver> implements Sink<Coin> {
	/**
	 * Represents this device's output sink.
	 */
	public Sink<Coin> sink;

	/**
	 * Creates a coin slot.
	 */
	public CoinSlot() {}

	/**
	 * Tells the coin slot that the indicated coin is being inserted. If the slot is
	 * enabled, announces "coinInserted" event. Requires power.
	 * 
	 * @param coin
	 *            The coin to be added. Cannot be null.
	 * @throws DisabledException
	 *             If the coin slot is currently disabled.
	 * @throws TooMuchCashException
	 *             If the sink has no space.
	 * @throws SimulationException
	 *             If coin is null.
	 */
	public synchronized void receive(Coin coin) throws DisabledException, TooMuchCashException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		notifyCoinInserted();

		if(sink.hasSpace()) {
			try {
				sink.receive(coin);
			}
			catch(TooMuchCashException e) {
				// Should never happen
				throw e;
			}
		}
		else
			throw new TooMuchCashException("Unable to route coin: Output channel is full");
	}

	@Override
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return sink.hasSpace();
	}

	private void notifyCoinInserted() {
		for(CoinSlotObserver observer : observers)
			observer.coinInserted(this);
	}
}
