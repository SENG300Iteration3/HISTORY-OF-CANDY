package com.diy.hardware.external;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents external companies that issue cards and authorize payments made
 * with them.
 * <p>
 * This class uses a transactional model that is simpler than the real thing: to
 * debit a purchase, a hold is first placed on the amount and then the
 * transaction is posted. It does not quite work like that in the real world.
 */
public class CardIssuer {
	private String name;
	private long maximumHoldCount;

	/**
	 * Create a card provider.
	 * 
	 * @param name
	 *            The company's name.
	 * @param maximumHoldCount
	 *            The maximum number of holds permitted on cards issued by this
	 *            issuer.
	 * @throws SimulationException
	 *             If name is null.
	 */
	public CardIssuer(String name, long maximumHoldCount) {
		if(name == null)
			throw new NullPointerSimulationException("name");

		if(maximumHoldCount <= 0)
			throw new InvalidArgumentSimulationException("Cards must support at least one hold: " + maximumHoldCount);

		this.name = name;
		this.maximumHoldCount = maximumHoldCount;
	}

	/**
	 * Blocks the card corresponding to the indicated number, thereby preventing new
	 * transactions.
	 * 
	 * @param cardNumber
	 *            The number of the card to block.
	 * @return true, if the card has been successfully blocked; otherwise, false.
	 */
	public boolean block(String cardNumber) {
		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			cr.isBlocked = true;
		}

		return true;
	}

	/**
	 * Eliminates the block on the card corresponding to the indicated number,
	 * thereby permitting new transactions.
	 * 
	 * @param cardNumber
	 *            The number of the card to unblock.
	 * @return true, if the card has been successfully unblocked; otherwise, false.
	 */
	public boolean unblock(String cardNumber) {
		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			cr.isBlocked = false;
		}

		return true;
	}

	private class CardRecord {
		boolean isBlocked = false;
		String number;
		String cardholder;
		Calendar expiry;
		String ccv;
		BigDecimal available;
		Map<Long, BigDecimal> holds = new HashMap<>();

		synchronized BigDecimal holdsTotal() {
			BigDecimal total = BigDecimal.ZERO;

			for(BigDecimal hold : holds.values())
				total = total.add(hold);

			return total;
		}
	}

	private HashMap<String, CardRecord> database = new HashMap<>();

	private boolean isValidCardNumber(String number) {
		if(number == null)
			return false;

		try {
			Long.parseLong(number);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidCardholderName(String name) {
		if(name == null)
			return false;

		if(name.equals(""))
			return false;

		return true;
	}

	private boolean isValidCCV(String ccv) {
		if(ccv == null)
			return false;

		if(ccv.length() != 3)
			return false;

		try {
			Integer.parseInt(ccv);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidDate(Calendar date) {
		if(date == null)
			return false;

		if(date.before(Calendar.getInstance()))
			return false;

		return true;
	}

	/**
	 * Adds information about a card to the company's database. They do this when
	 * they create the card.
	 * 
	 * @param number
	 *            The card number.
	 * @param cardholder
	 *            The name of the cardholder.
	 * @param expiry
	 *            The expiry date of the card. Must be in the future.
	 * @param ccv
	 *            The security code on the back of the card.
	 * @param amount
	 *            For a credit card, this represents the credit limit. For a debit
	 *            card, this is how much money is available. (Yes, it is a
	 *            simplistic simulation.)
	 */
	public void addCardData(String number, String cardholder, Calendar expiry, String ccv, double amount) {
		if(!isValidCardNumber(number))
			throw new InvalidArgumentSimulationException("The card number is not valid.");

		if(!isValidCardholderName(cardholder))
			throw new InvalidArgumentSimulationException("The cardholder name is not valid.");

		if(amount <= 0)
			throw new InvalidArgumentSimulationException("amount must be positive.");

		if(!isValidCCV(ccv))
			throw new InvalidArgumentSimulationException("The CCV is not valid.");

		if(!isValidDate(expiry))
			throw new InvalidArgumentSimulationException("The expiry date is not valid");

		if(database.containsKey(number))
			throw new InvalidArgumentSimulationException("The number " + number + " is already in use.");

		CardRecord cr = new CardRecord();
		cr.number = number;
		cr.cardholder = cardholder;
		cr.expiry = expiry;
		cr.ccv = ccv;
		cr.available = new BigDecimal(amount);

		database.put(number, cr);
	}

	private static final Random random = new Random();

	/**
	 * Authorizes a hold on the indicated amount for the card with the indicated
	 * number. If successful, the hold is kept indefinitely until either released
	 * explicitly or the transaction is completed.
	 * 
	 * @param cardNumber
	 *            The number of the card on which to place a hold.
	 * @param amount
	 *            The amount to hold.
	 * @return -1 if the hold failed; otherwise, a non-negative integer representing
	 *             a code to reference the hold.
	 */
	public long authorizeHold(String cardNumber, double amount) {
		if(amount <= 0.0)
			return -1L;
		
		BigDecimal amountBD = new BigDecimal(amount);
		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return -1;

		synchronized(cr) {
			if(cr.holds.size() == maximumHoldCount)
				return -1;

			if(cr.isBlocked)
				return -1;

			if(cr.available.subtract(cr.holdsTotal()).compareTo(amountBD) >= 0) {
				Long holdNumber;

				while(true) {
					holdNumber = random.nextLong(maximumHoldCount);
					if(!cr.holds.containsKey(holdNumber))
						break;
				}

				cr.holds.put(holdNumber, amountBD);

				return holdNumber;
			}
		}

		return -1;
	}

	/**
	 * Eliminates the hold corresponding to the indicated hold number, on the card
	 * corresponding to the indicated card number.
	 * 
	 * @param cardNumber
	 *            The card on which to release the hold.
	 * @param holdNumber
	 *            The identifier of the hold to be released.
	 * @return true, if the hold has been successfully released; otherwise, false.
	 */
	public boolean releaseHold(String cardNumber, long holdNumber) {
		if(holdNumber < 0)
			return false;

		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			if(cr.isBlocked)
				return false;

			cr.holds.remove(holdNumber);
		}

		return true;
	}

	/**
	 * Records a transaction against the card corresponding to the indicated card
	 * number. A hold must have been successfully placed on the card prior to this
	 * transaction, corresponding to the indicated hold number.
	 * <p>
	 * The actual amount should be less than or equal to the amount in the
	 * corresponding hold. Otherwise, this transaction will fail.
	 * 
	 * @param cardNumber
	 *            The number of the card on which the transaction is to be posted.
	 * @param holdNumber
	 *            The number of the initial hold placed on the card.
	 * @param actualAmount
	 *            The amount to be charged against the card.
	 * @return true, if the posting was successful; otherwise, false.
	 */
	public boolean postTransaction(String cardNumber, long holdNumber, double actualAmount) {
		BigDecimal actualAmountBD = new BigDecimal(actualAmount);
		
		if(holdNumber < 0)
			return false;

		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			if(cr.isBlocked)
				return false;

			BigDecimal heldAmount = cr.holds.get(holdNumber);

			if(heldAmount == null)
				return false;

			if(heldAmount.subtract(actualAmountBD).compareTo(BigDecimal.ZERO) >= 0) {
				cr.available = cr.available.subtract(actualAmountBD);
				cr.holds.remove(holdNumber);
				return true;
			}
			else {
				cr.holds.remove(holdNumber);
				return false;
			}
		}
	}
}
