package com.jimmyselectronics.opeechee;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.opeechee.Card.CardData;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents the card reader, capable of tap, chip insert, and swipe. Either
 * the reader or the card may fail, or the data read in can be corrupted, with
 * varying probabilities.
 * 
 * @author Jimmy's Electronics LLP
 */
public class CardReader extends AbstractDevice<CardReaderListener> {
	private boolean cardIsInserted = false;
	private final static ThreadLocalRandom random = ThreadLocalRandom.current();
	private final static double PROBABILITY_OF_TAP_FAILURE = 0.01;
	private final static double PROBABILITY_OF_INSERT_FAILURE = 0.01;
	private final static double PROBABILITY_OF_SWIPE_FAILURE = 0.1;

	/**
	 * Tap the card. Requires power.
	 * 
	 * @param card
	 *            The card to tap.
	 * @return The card's (possibly corrupted) data, or null if the card is not tap
	 *             enabled.
	 * @throws IOException
	 *             If the tap failed (lack of failure does not mean that the data is
	 *             not corrupted).
	 */
	public synchronized CardData tap(Card card) throws IOException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(card.isTapEnabled) {
			notifyCardTapped();

			if(random.nextDouble(0.0, 1.0) > PROBABILITY_OF_TAP_FAILURE) {
				CardData data = card.tap();

				notifyCardDataRead(data);

				return data;
			}
			else
				throw new ChipFailureException();
		}

		// else ignore

		return null;
	}

	/**
	 * Swipe the card. Requires power.
	 * 
	 * @param card
	 *            The card to swipe.
	 * @return The card data.
	 * @throws IOException
	 *             If the swipe failed.
	 */
	public synchronized CardData swipe(Card card) throws IOException {
		if(!isPoweredUp())
			throw new NoPowerException();

		notifyCardSwiped();

		if(random.nextDouble(0.0, 1.0) > PROBABILITY_OF_SWIPE_FAILURE) {
			CardData data = card.swipe();

			notifyCardDataRead(data);

			return data;
		}

		throw new MagneticStripeFailureException();
	}

	/**
	 * Insert the card. Requires power.
	 * 
	 * @param card
	 *            The card to insert.
	 * @param pin
	 *            The customer's PIN.
	 * @return The card data.
	 * @throws SimulationException
	 *             If there is already a card in the slot.
	 * @throws IOException
	 *             The insertion failed.
	 */
	public synchronized CardData insert(Card card, String pin) throws IOException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(cardIsInserted)
			throw new IllegalStateException("There is already a card in the slot");

		cardIsInserted = true;

		notifyCardInserted();

		if(card.hasChip && random.nextDouble(0.0, 1.0) > PROBABILITY_OF_INSERT_FAILURE) {
			CardData data = card.insert(pin);

			notifyCardDataRead(data);

			return data;
		}

		throw new ChipFailureException();
	}

	/**
	 * Remove the card from the slot. Requires power.
	 * 
	 * @throws NullPointerSimulationException
	 *             if no card is present.
	 */
	public void remove() {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(!cardIsInserted)
			throw new NullPointerSimulationException();

		cardIsInserted = false;
		notifyCardRemoved();
	}

	private void notifyCardTapped() {
		for(CardReaderListener l : listeners())
			l.cardTapped(this);
	}

	private void notifyCardInserted() {
		for(CardReaderListener l : listeners())
			l.cardInserted(this);
	}

	private void notifyCardSwiped() {
		for(CardReaderListener l : listeners())
			l.cardSwiped(this);
	}

	private void notifyCardDataRead(CardData data) {
		for(CardReaderListener l : listeners())
			l.cardDataRead(this, data);
	}

	private void notifyCardRemoved() {
		for(CardReaderListener l : listeners())
			l.cardRemoved(this);
	}
}
