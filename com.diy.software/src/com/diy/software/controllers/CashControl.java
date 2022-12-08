package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import com.diy.software.listeners.CashControlListener;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.OutOfCashException;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.banknote.BanknoteDispensationSlot;
import com.unitedbankingservices.banknote.BanknoteDispensationSlotObserver;
import com.unitedbankingservices.banknote.BanknoteInsertionSlot;
import com.unitedbankingservices.banknote.BanknoteInsertionSlotObserver;
import com.unitedbankingservices.banknote.BanknoteStorageUnit;
import com.unitedbankingservices.banknote.BanknoteStorageUnitObserver;
import com.unitedbankingservices.banknote.BanknoteValidator;
import com.unitedbankingservices.banknote.BanknoteValidatorObserver;
import com.unitedbankingservices.coin.Coin;
import com.unitedbankingservices.coin.CoinStorageUnit;
import com.unitedbankingservices.coin.CoinStorageUnitObserver;
import com.unitedbankingservices.coin.CoinValidator;
import com.unitedbankingservices.coin.CoinValidatorObserver;

public class CashControl implements BanknoteValidatorObserver, CoinValidatorObserver, CoinStorageUnitObserver,
		BanknoteStorageUnitObserver, BanknoteInsertionSlotObserver, BanknoteDispensationSlotObserver, ActionListener {
	private StationControl sc;
	private ArrayList<CashControlListener> listeners;
	private boolean coinsFull;
	private boolean notesFull;

	public CashControl(StationControl sc) {
		this.sc = sc;
		sc.station.banknoteValidator.attach(this);
		sc.station.coinValidator.attach(this);
		sc.station.coinStorage.attach(this);
		sc.station.banknoteStorage.attach(this);
		sc.station.banknoteInput.attach(this);
		sc.station.banknoteOutput.attach(this);
		this.listeners = new ArrayList<>();

		coinsFull = false;
		notesFull = false;
	}
	
	public void resetState() {
		coinsFull = false;
		notesFull = false;
	}

	public void addListener(CashControlListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CashControlListener listener) {
		listeners.remove(listener);
	}

	private void coinsEnabled() {
		for (CashControlListener listener : listeners)
			listener.coinInsertionEnabled(this);
	}

	private void notesEnabled() {
		for (CashControlListener listener : listeners)
			listener.noteInsertionEnabled(this);
	}

	private void coinsDisabled() {
		for (CashControlListener listener : listeners)
			listener.coinInsertionDisabled(this);
	}

	private void notesDisabled() {
		for (CashControlListener listener : listeners)
			listener.noteInsertionDisabled(this);
	}

	public void cashInserted() {
		for (CashControlListener listener : listeners)
			listener.cashInserted(this);
	}

	private void checkCashRejected() {
		for (CashControlListener listener : listeners)
			listener.checkCashRejected(this);
	}

	private void changeReturned() {
		for (CashControlListener listener : listeners)
			listener.changeReturned(this);
	}

	public void paymentFailed(boolean a) {
		for (CashControlListener listener : listeners)
			listener.paymentFailed(this, a);
	}

	/*
	 * returns true if the coinInput can be enabled, false otherwise
	 */
	private void enableCoins() {
		if (!coinsFull && sc.getItemsControl().getCheckoutTotal() > 0) {
			sc.station.coinSlot.enable();
			coinsEnabled();
		}
	}

	private void disableCoins() {
		sc.station.coinSlot.disable();
		coinsDisabled();
	}

	/*
	 * return true if the noteInput can be enabled, false otherwise
	 */
	private void enableNotes() {
		if (!notesFull && sc.getItemsControl().getCheckoutTotal() > 0) {
			sc.station.banknoteInput.enable();
			notesEnabled();
		}
	}

	private void disableNotes() {
		sc.station.banknoteInput.disable();
		notesDisabled();
	}

	public void enablePayments() {
		enableNotes();
		enableCoins();
	}

	public void disablePayments() {
		disableNotes();
		disableCoins();
	}

	/**
	 * An event announcing that the indicated coin has been detected and determined
	 * to be valid.
	 * 
	 * @param validator
	 *                  The device on which the event occurred.
	 * @param value
	 *                  The value of the coin.
	 */
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		double current = sc.getItemsControl().getCheckoutTotal();
		if (current - value.doubleValue() >= 0) { // if no change needs to be returned
			sc.getItemsControl().updateCheckoutTotal(-1*value.doubleValue());
			cashInserted();
		} else {
			sc.getItemsControl().updateCheckoutTotal(-current);
			cashInserted();
			returnChange((value.doubleValue()) - current);
			disablePayments();
		}
	}

	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be valid.
	 * 
	 * @param validator
	 *                  The device on which the event occurred.
	 * @param currency
	 *                  The kind of currency of the inserted banknote.
	 * @param value
	 *                  The value of the inserted banknote.
	 */
	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, long value) {
		double current = sc.getItemsControl().getCheckoutTotal();
		if (current - value >= 0) { // if no change needs to be returned
			sc.getItemsControl().updateCheckoutTotal(-value);
			cashInserted();
		} else {
			sc.getItemsControl().updateCheckoutTotal(-current);
			cashInserted();
			returnChange(value - current);
			disablePayments();
		}
	}

	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be invalid.
	 * 
	 * @param validator
	 *                  The device on which the event occurred.
	 */
	@Override
	public void banknoteEjected(BanknoteInsertionSlot slot) {
	}

	/**
	 * An event announcing that a coin has been detected and determined to be
	 * invalid.
	 * 
	 * @param validator
	 *                  The device on which the event occurred.
	 */
	@Override
	public void invalidCoinDetected(CoinValidator validator) {
	}

	/**
	 * Announces that the indicated banknote storage unit is full of banknotes.
	 * 
	 * @param unit
	 *             The storage unit where the event occurred.
	 */
	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {
		notesFull = true;
		sc.station.banknoteInput.disable();
		notesDisabled();
	}

	/**
	 * Announces that the storage unit has been emptied of banknotes. Used to
	 * simulate direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *             The storage unit where the event occurred.
	 */
	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {
		notesFull = false;
	}

	/**
	 * Announces that the indicated coin storage unit is full of coins.
	 * 
	 * @param unit
	 *             The storage unit where the event occurred.
	 */
	@Override
	public void coinsFull(CoinStorageUnit unit) {
		coinsFull = true;
		sc.station.coinSlot.disable();
		coinsDisabled();
	}

	/**
	 * Announces that the storage unit has been emptied of coins. Used to simulate
	 * direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *             The storage unit where the event occurred.
	 */
	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {
		coinsFull = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			if (c.startsWith("d")) {
				Banknote banknote = new Banknote(Currency.getInstance("CAD"), Long.parseLong(c.split(" ")[1]));
				if (!sc.station.banknoteInput.isDisabled()) {
					sc.station.banknoteInput.receive(banknote);
				} else {
					System.out.println("Banknote storage unit is full");
				}
			} else if (c.startsWith("c")) {
				Coin coin = new Coin(Currency.getInstance("CAD"), new BigDecimal(c.split(" ")[1]));
				if (!sc.station.coinSlot.isDisabled()) {
					sc.station.coinSlot.receive(coin);
				} else {
					System.out.println("Coin storage unit is full");
				}
			}
			checkCashRejected();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
	}

	public void returnChange(double change) {
		ArrayList<BigDecimal> coins = new ArrayList<BigDecimal>(); // arraylist of the coin dispensers
		ArrayList<Integer> bills = new ArrayList<Integer>(); // arraylist of the bank note dispensers
		ArrayList<Integer> returnCoins = new ArrayList<Integer>(); // arraylist of how much a given coin dispenser needs to
																																// return
		ArrayList<Integer> returnBills = new ArrayList<Integer>(); // arraylist of how much a given banknote dispenser needs
																																// to return

		double returned = 0;

		for (BigDecimal x : sc.station.coinDenominations) { // gets all of the coin dispensers in the machine
			coins.add(x);
			returnCoins.add(0);
		}
		for (int x : sc.station.banknoteDenominations) { // gets all of the banknote dispensers in the machine
			bills.add(x);
			returnBills.add(0);
		}
		Collections.sort(coins, Collections.reverseOrder()); // sorts them from highest to lowest value
		Collections.sort(bills, Collections.reverseOrder()); // sorts them from highest to lowest value

		int q = 0;
		int MAX_q = 10;
		for (int i = 0; i < bills.size() && q < MAX_q; i++) { // gets how many notes should be returned
			if ((change - returned) < bills.get(bills.size() - 1)) { // if the smallest note is too big to be used as change
				break;
			}

			int value = bills.get(i);

			int capacity = sc.station.banknoteDispensers.get(value).size();

			int n = Math.min(Math.min((MAX_q - q), capacity), (int) ((change - returned) / ((double) value)));

			returned += value * n;
			returnBills.set(i, n);
			q += n;
		}

		q = 0;
		MAX_q = 25;
		for (int i = 0; i < coins.size() && q < MAX_q; i++) { // gets how many coins should be returned
			if ((change - returned) < coins.get(coins.size() - 1).doubleValue()) { // if the smallest coin is too big to be used as
																																				// change
				break;
			}

			BigDecimal value = coins.get(i);

			int capacity = sc.station.coinDispensers.get(value).size();

			int n = Math.min(Math.min((MAX_q - q), capacity), (int) ((change - returned) / value.doubleValue()));

			returned += value.doubleValue() * n;
			returnCoins.set(i, n);
			q += n;
		}

		double totalReturned = 0;

		for (int i = 0; i < bills.size(); i++) { // returns all of the bills that you have accounted for
			int times = returnBills.get(i);
			int value = bills.get(i);
			while (times > 0) {
				try {
					sc.station.banknoteDispensers.get(value).emit();
					totalReturned += value;
				} catch (OutOfCashException | DisabledException | TooMuchCashException e) {
					break;
				}
				times--;
			}
		}

		sc.station.banknoteOutput.dispense(); // dispenses all banknotes

		for (int i = 0; i < coins.size(); i++) { // returns all of the coins that you have accounted for
			int times = returnCoins.get(i);
			BigDecimal value = coins.get(i);
			while (times > 0) {
				try {
					sc.station.coinDispensers.get(value).emit();
					totalReturned += value.doubleValue();
				} catch (OutOfCashException | DisabledException | TooMuchCashException e) {
					break;
				}
				times--;
			}
		}
    
    if (totalReturned - change * 100 < -0.01) {
			// TODO: notify attendant that customer was shortchanged by
			// change-totalReturned
		}

		changeReturned();
  }

	/*
	 * Checks if banknote storage is below a threshold and notifies system
	 * 
	 * @param unit
	 * 			The storage unit being checked
	 */
	public boolean banknotesInStorageLow(BanknoteStorageUnit unit) {
		boolean isLow = false;
		//count is less than 1/20 of the capacity
		if (unit.getBanknoteCount() <= (unit.getCapacity()/20)) {
			isLow = true;
		}
		return isLow;
	}

}
