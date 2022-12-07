package com.jimmyselectronics.opeechee;

import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.opeechee.Card.CardData;

/**
 * Listens for events emanating from a coin dispenser.
 * 
 * @author Jimmy's Electronics LLP
 */
public interface CardReaderListener extends AbstractDeviceListener {
	/**
	 * Announces that a card has been inserted in the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	void cardInserted(CardReader reader);

	/**
	 * Announces that a card has been removed from the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	void cardRemoved(CardReader reader);

	/**
	 * Announces that a (tap-enabled) card has been tapped on the indicated card
	 * reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	void cardTapped(CardReader reader);

	/**
	 * Announces that a card has swiped on the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	void cardSwiped(CardReader reader);

	/**
	 * Announces that the data has been read from a card.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 * @param data
	 *            The data that was read. Note that this data may be corrupted.
	 */
	void cardDataRead(CardReader reader, CardData data);
}
