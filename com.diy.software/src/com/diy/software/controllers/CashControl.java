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
			sc.getItemsControl().updateCheckoutTotal(-current);
			cashInserted();
			returnChange((value/100.0)-current);
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
		if(current - value >= 0) { //if no change needs to be returned
			sc.getItemsControl().updateCheckoutTotal(-value);
			cashInserted();
		}else {
			sc.getItemsControl().updateCheckoutTotal(-current);
			cashInserted();
			returnChange(value-current);
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
    lastInsertedCoin = value;
    try{
      sc.station.coinStorage.receive(new Coin(Currency.getInstance("CAD"), value));
    } catch (TooMuchCashException e) {
      e.printStackTrace();
    } catch (DisabledException e) {
      e.printStackTrace();
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
    lastInsertedBanknote = value;
    try {
      sc.station.banknoteStorage.receive(new Banknote(Currency.getInstance("CAD"), value));
    } catch (DisabledException e) {
      e.printStackTrace();
    } catch (TooMuchCashException e) {
      e.printStackTrace();
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
   * Announces that the indicated coin dispenser is full of coins.
   * 
   * @param dispenser
   *                  The dispenser where the event occurred.
   */

  /**
   * Announces that the indicated banknote storage unit is full of banknotes.
   * 
   * @param unit
   *             The storage unit where the event occurred.
   */
  @Override
  public void banknotesFull(BanknoteStorageUnit unit) {
  }

  /**
   * Announces that the indicated storage unit has been loaded with banknotes.
   * Used to simulate direct, physical loading of the unit.
   * 
   * @param unit
   *             The storage unit where the event occurred.
   */
  @Override
  public void banknotesLoaded(BanknoteStorageUnit unit) {
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
  }

  /**
   * Announces that the indicated coin storage unit is full of coins.
   * 
   * @param unit
   *             The storage unit where the event occurred.
   */
  @Override
  public void coinsFull(CoinStorageUnit unit) {
    System.out.println("Coin storage Unit is too full of coins >//<");
  }

  /**
   * Announces that the indicated storage unit has been loaded with coins. Used to
   * simulate direct, physical loading of the unit.
   * 
   * @param unit
   *             The storage unit where the event occurred.
   */
  @Override
  public void coinsLoaded(CoinStorageUnit unit) {
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
    System.out.println("Coin storage Unit emptied of coins >//<");
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
