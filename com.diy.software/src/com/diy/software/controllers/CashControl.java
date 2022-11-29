package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private ArrayList<Integer> notesToReturn;

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
    
		notesToReturn = new ArrayList<Integer>();
	}

	public void addListener(CashControlListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CashControlListener listener) {
		listeners.remove(listener);
	}

	private void coinsEnabled() {
		for(CashControlListener listener : listeners)
			listener.coinInsertionEnabled(this);
	}
  
	private void notesEnabled() {
		for(CashControlListener listener : listeners)
			listener.noteInsertionEnabled(this);
	}
  
	private void coinsDisabled() {
		for(CashControlListener listener : listeners)
			listener.coinInsertionDisabled(this);
	}
  
	private void notesDisabled() {
		for(CashControlListener listener : listeners)
			listener.noteInsertionDisabled(this);
	}
  
	private void cashInserted() {
		for(CashControlListener listener : listeners)
			listener.cashInserted(this);
	}

	private void cashRejected() {
		for (CashControlListener listener : listeners) 
			listener.cashRejected(this);
	}
  
	private void changeReturned() {
		for (CashControlListener listener : listeners) 
			listener.changeReturned(this);
	}
  
	/*
	 * returns true if the coinInput can be enabled, false otherwise
	 */
	private void enableCoins() {
		if(!coinsFull && sc.getItemsControl().getCheckoutTotal() > 0) {
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
		if(!notesFull && sc.getItemsControl().getCheckoutTotal() > 0) {
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
	public void validCoinDetected(CoinValidator validator, long value) {
		double current = sc.getItemsControl().getCheckoutTotal();
		if(current - (double)value/100.0 >= 0) { //if no change needs to be returned
			sc.getItemsControl().updateCheckoutTotal(-(double)value/100.0);
			cashInserted();
		}else {
			returnChange((value/100.0)-current);
			sc.getItemsControl().updateCheckoutTotal(-current);
			cashInserted();
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
		if(current - value >= 0) { //if no change needs to be returned
			sc.getItemsControl().updateCheckoutTotal(-value);
			cashInserted();
		}else {
			returnChange(value-current);
			sc.getItemsControl().updateCheckoutTotal(-current);
			cashInserted();
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
  	public void invalidBanknoteDetected(BanknoteValidator validator) {
		cashRejected();
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
		cashRejected();
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
				if (sc.station.banknoteInput.isActivated()) {
					sc.station.banknoteInput.receive(banknote);
				} else {
					System.out.println("Banknote storage unit is full");
				}
			}else if (c.startsWith("c")) {
				Coin coin = new Coin(Currency.getInstance("CAD"), Long.parseLong(c.split(" ")[1]));
				if (sc.station.coinSlot.isActivated()) {
					sc.station.coinSlot.receive(coin);
				} else {
					System.out.println("Coin storage unit is full");
				}
			}
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
	}
  
	public void returnChange(double change) {
		ArrayList<Long> coins = new ArrayList<Long>(); //arraylist of the coin dispensers
		ArrayList<Integer> bills = new ArrayList<Integer>(); //arraylist of the bank note dispensers
		ArrayList<Integer> returnCoins = new ArrayList<Integer>(); //arraylist of how much a given coin dispenser needs to return
		ArrayList<Integer> returnBills = new ArrayList<Integer>(); //arraylist of how much a given banknote dispenser needs to return
		
		double returned = 0;
		
		for(long x : sc.station.coinDenominations) { //gets all of the coin dispensers in the machine
			coins.add(x);
			returnCoins.add(0);
		}
		for(int x : sc.station.banknoteDenominations) { //gets all of the banknote dispensers in the machine
			bills.add(x);
			returnBills.add(0);
		}
		Collections.sort(coins, Collections.reverseOrder()); //sorts them from highest to lowest value
		Collections.sort(bills, Collections.reverseOrder()); //sorts them from highest to lowest value
		
		int q = 0;
		int MAX_q = 10;
		for(int i = 0; i < bills.size() && q < MAX_q; i++) { //gets how many notes should be returned
			if((change-returned) < bills.get(bills.size()-1)) { //if the smallest note is too big to be used as change
				break;
			}
			
			int value = bills.get(i);
			
			int capacity = sc.station.banknoteDispensers.get(value).size();
			
			int n = Math.min(Math.min((MAX_q-q), capacity), (int)((change-returned)/((double)value)));
			
			returned += value*n;
			returnCoins.set(i, n);
			q += n;
		}
		
		q = 0;
		MAX_q = 25;
		for(int i = 0; i < coins.size() && q < MAX_q; i++) { //gets how many coins should be returned
			if((change-returned) < coins.get(coins.size()-1)) { //if the smallest coin is too big to be used as change
				break;
			}
			
			long value = coins.get(i);
			
			int capacity = sc.station.coinDispensers.get(value).size();
			
			int n = Math.min(Math.min((MAX_q-q), capacity), (int)((change-returned)/(((double)value)/100.0)));
			
			returned += value*n;
			returnCoins.set(i, n);
			q += n;
		}
		
		for(int i = 0; i < bills.size(); i++) { //returns all of the bills that you have accounted for
			int times = returnBills.get(i);
			int value = bills.get(i);
			while(times > 0) {
					try {
						sc.station.banknoteDispensers.get(value).emit();
					} catch (OutOfCashException | DisabledException | TooMuchCashException e) {
						break;
					}
				times--;
			}
		}
		
		sc.station.banknoteOutput.dispense(); //dispenses all banknotes
		
		for(int i = 0; i < coins.size(); i++) { //returns all of the coins that you have accounted for
			int times = returnCoins.get(i);
			long value = coins.get(i);
			while(times > 0) {
				try {
					sc.station.coinDispensers.get(value).emit();
				} catch (OutOfCashException | DisabledException | TooMuchCashException e) {
					break;
				}
				times--;
			}
		}
		
		changeReturned();
	}

  // STUFF FOR CHANGE (leftover money kind)

  /**
   * Returns true if the payment is enough. Otherwise, will return false.
   * 
   * 
   * @param payment
   *                      How much the customer pays.
   * @param checkoutTotal
   *                      How much the customer has to pay.
   */
//  public boolean isPaymentEnough(Double payment, Double checkoutTotal) {
//
//    // payment > checkoutTotal
//    if (payment.compareTo(checkoutTotal) > 0)
//      return true;
//
//    // payment < checkoutTotal
//    if (payment.compareTo(checkoutTotal) < 0)
//      return false;
//
//    // payment == checkoutTotal
//    if (payment.compareTo(checkoutTotal) == 0)
//      return true;
//
//    return false; // when something is wrong.
//  }

  /**
   * Returns a list of banknote denominations and coin denominations that make up
   * the change,
   * where change.x = banknote denominations and change.y = coin denominations.
   * If you want total amount of change, add the sum(change.x) + sum(change.y)
   * together.
   * 
   * BEWARE that since Double is being used, there may be precision issues. But
   * I'm not too sure
   * how big of a problem may be so far now I'll ignore it. Just beware using '='
   * with Doubles.
   * 
   * @param amountRecieved
   *                       The amount to calculate the change for.
   */

//  public Tuple<List<Integer>, List<Double>> calculateChange(Double amountRecieved) {
//    List<Double> usedCoins = new ArrayList<Double>();
//    List<Integer> usedBanknotes = new ArrayList<Integer>();
//
//    usedBanknotes = calculateChangeBanknotes(amountRecieved);
//
//    Double amountAfterBanknotes = 0.0;
//    for (Integer i : usedBanknotes)
//      amountAfterBanknotes += i;
//    usedCoins = calculateChangeCoins(amountRecieved - amountAfterBanknotes);
//
//    Tuple<List<Integer>, List<Double>> change = new Tuple<List<Integer>, List<Double>>(usedBanknotes, usedCoins);
//    return change;
//  }

  /**
   * HELPER FUNCTION
   * Returns a list of coin denominations that make up the change. It's
   * amount after exchanging the smallest amount possible given the coin values.
   * If you want the amount of change, get the sum of the output.
   * 
   * BEWARE that since Double is being used, there may be precision issues.
   * 
   * Assumes that coinDenominations (what counts as a coin) is defined
   * 
   * @param amountRecieved
   *                       The amount to calculate the change for.
   */
//  private List<Double> calculateChangeCoins(Double amountRecieved) {
//    List<Long> coins = sc.station.coinDenominations;
//    List<Double> usedCoins = new ArrayList<Double>();
//    Collections.sort(coins, Collections.reverseOrder());
//    Double currentAmount = amountRecieved;
//
//    for (Long coinValue : coins) {
//      while (currentAmount - coinValue > 0) {
//        currentAmount = currentAmount - coinValue;
//        usedCoins.add(coinValue.doubleValue());
//
//      }
//    }
//    return usedCoins;
//  }

  /**
   * HELPER FUNCTION
   * Returns a list of banknote denominations make up the change. It's
   * amount after exchanging the smallest amount possible given the banknote
   * values.
   * If you want the amount of change, get the sum of the output.
   * 
   * BEWARE that since Double is being used, there may be precision issues.
   * 
   * Assumes that banknoteDenominations (what counts as a banknote) is defined
   * 
   * @param amountRecieved
   *                       The amount to calculate the change for.
   */
//  private List<Integer> calculateChangeBanknotes(Double amountRecieved) {
//    int[] banknotes = sc.station.banknoteDenominations;
//    List<Integer> usedBanknotes = new ArrayList<Integer>();
//    Arrays.sort(banknotes);
//    // sort returns the array in ascending order. We want it sorted in descending
//    // order.
//    reverseArray(banknotes);
//
//    Double currentAmount = amountRecieved;
//
//    for (int banknoteValue : banknotes) {
//      while (currentAmount - banknoteValue > 0) {
//        currentAmount = currentAmount - banknoteValue;
//        usedBanknotes.add(banknoteValue);
//
//      }
//    }
//
//    return usedBanknotes;
//  }

//  private void reverseArray(int[] arr) {
//    for (int i = 0; i < arr.length / 2; i++) {
//      int temp = arr[i];
//      arr[i] = arr[arr.length - i - 1];
//      arr[arr.length - i - 1] = temp;
//    }
//  }
  
  // Future Iterations ^^

}
