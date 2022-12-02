package com.diy.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.diy.hardware.DoItYourselfStation;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.opeechee.Card;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents the simulation of a customer, capable of interacting with the
 * hardware.
 */
public final class Customer {
	/**
	 * Simple representation of the customer's shopping cart. These are items that
	 * have yet to be scanned or entered.
	 */
	public final List<Item> shoppingCart = new ArrayList<>();
	/**
	 * Used to hold big or heavy items, once scanned or entered. It's really part of
	 * the shopping cart, but it was easier to simply make it a separate list.
	 */
	public final List<Item> bulkyItemStorage = new ArrayList<>();
	/**
	 * The customer's wallet.
	 */
	public final Wallet wallet = new Wallet();

	private Item currentItem = null;

	private Card selectedCard = null;
	private DoItYourselfStation station = null;

	/**
	 * The customer starts to use the indicated station.
	 * 
	 * @param station
	 *            The station to use.
	 */
	public void useStation(DoItYourselfStation station) {
		if(station == null)
			throw new NullPointerSimulationException("station");

		this.station = station;
	}

	/**
	 * The customer stops using the current station.
	 * 
	 * @throws NullPointerSimulationException
	 *             if there customer is not currently using a station.
	 */
	public void freeUpStation() {
		if(station == null)
			throw new NullPointerSimulationException("station");

		this.station = null;
	}

	/**
	 * Selects the next item from the shopping cart; this becomes the current item.
	 * 
	 * @throws NoSuchElementException
	 *             If there is no next item.
	 */
	public void selectNextItem() throws NoSuchElementException {
		if(shoppingCart.isEmpty())
			throw new NoSuchElementException();

		currentItem = shoppingCart.remove(shoppingCart.size() - 1);
	}

	/**
	 * Deselects the current item, returning it to the shopping cart; the current
	 * item becomes undefined.
	 * 
	 * @throws NullPointerSimulationException
	 *             If there is no current item.
	 */
	public void deselectCurrentItem() throws NullPointerSimulationException {
		if(currentItem == null)
			throw new NullPointerSimulationException();

		shoppingCart.add(currentItem);
		currentItem = null;
	}

	/**
	 * The customer leaves the current item in the cart. The current item is no
	 * longer defined.
	 * 
	 * @throws NullPointerSimulationException
	 *             If the current item is not defined.
	 */
	public void leaveBulkyItemInCart() {
		if(currentItem == null)
			throw new NullPointerSimulationException("currentItem");

		bulkyItemStorage.add(currentItem);
		currentItem = null;
	}

	/**
	 * Attempts to select a card of the indicated type from the customer's wallet,
	 * removing it from the wallet. This remains selected until inserted in the
	 * self-checkout station or until returned to the wallet.
	 * 
	 * @param kind
	 *            The kind of the card to look for.
	 * @throws NoSuchElementException
	 *             If a card of the indicated kind is not found.
	 */
	public void selectCard(String kind) {
		for(Card card : wallet.cards)
			if(card.kind.equals(kind)) {
				selectedCard = card;
				break;
			}

		if(selectedCard != null)
			wallet.cards.remove(selectedCard);
		else
			throw new NoSuchElementException();
	}

	/**
	 * Causes the customer to insert their selected card into the station's card
	 * reader, entering their PIN in the process.
	 * 
	 * @param pin
	 *            The personal identification number of the card (if relevant to the
	 *            kind of card being used).
	 * @throws IOException
	 *             If an I/O problem occurred.
	 */
	public void insertCard(String pin) throws IOException {
		if(station == null)
			throw new NullPointerSimulationException("station");

		if(selectedCard == null)
			throw new NullPointerSimulationException("selectedCard");

		station.cardReader.insert(selectedCard, pin);
	}

	/**
	 * Replaces the currently selected card to the wallet.
	 */
	public void replaceCardInWallet() {
		if(selectedCard == null)
			throw new NullPointerSimulationException("selectedCard");

		wallet.cards.add(selectedCard);
		selectedCard = null;
	}

	/**
	 * The customer attempts to scan the current item
	 * 
	 * @param useHandheld
	 *            If the handheld scanner is to be used.
	 * @throws NoSuchElementException
	 *             If an item has not been selected.
	 * @throws ClassCastException
	 *             If the currently selected item does not have a barcode.
	 */
	public void scanItem(boolean useHandheld) throws NoSuchElementException {
		if(station == null)
			throw new NullPointerSimulationException("station");

		if(currentItem == null)
			throw new NoSuchElementException();

		if(useHandheld)
			station.handheldScanner.scan((BarcodedItem)currentItem);
		else
			station.mainScanner.scan((BarcodedItem)currentItem);
	}

	/**
	 * The customer places the current item in the bagging area (regardless of
	 * whether it was hand-scanned).
	 * 
	 * @throws NoSuchElementException
	 *             If an item has not been selected.
	 */
	public void placeItemInBaggingArea() throws NoSuchElementException {
		if(station == null)
			throw new NullPointerSimulationException("station");

		if(currentItem == null)
			throw new NoSuchElementException();

		station.baggingArea.add(currentItem);
		currentItem = null;
	}
}
