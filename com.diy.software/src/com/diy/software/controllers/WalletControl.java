package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.MissingResourceException;

import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;


public class WalletControl implements ActionListener, CardReaderListener {
	private StationControl sc;
	private ArrayList<WalletControlListener> listeners;
	private String selectedCardKind;
	private CardData currentCardData;

	public WalletControl(StationControl sc) {
		this.sc = sc;
		this.sc.station.cardReader.register(this);
		this.listeners = new ArrayList<>();
	}

	public void addListener(WalletControlListener l) {
		listeners.add(l);
	}

	public void removeListener(WalletControlListener l) {
		listeners.remove(l);
	}

	private Card cardFromKind(String kind) throws MissingResourceException {
		Card cardOfInterest = null;
		for (Card c : sc.fakeData.getCards()) {
			if (c.kind == selectedCardKind) {
				cardOfInterest = c;
				break;
			}
		}
		if (cardOfInterest == null) {
			throw new MissingResourceException("Card not found in waller", "Card", "this");
		}
		return cardOfInterest;
	}

	public void selectCard(String kind) {
		if (selectedCardKind != null) {
			sc.customer.replaceCardInWallet();
		}
		selectedCardKind = kind;
		sc.customer.selectCard(kind);
		for (WalletControlListener l : listeners)
			l.cardHasBeenSelected(this);
	}

	public void insertCard(String pin) {
		try {
			currentCardData = sc.station.cardReader.insert(cardFromKind(selectedCardKind), pin);
			return;
		} catch (Exception e) {
			sc.triggerUnknownReasonForPaymentFailScreen(
					"Error of type: \"" + e.getClass().getName() + "\" caused when inserting card");
		}
		System.out.println("Unknown error caused when inserting card");
		sc.triggerUnknownReasonForPaymentFailScreen(null);
	}

	public void tapCard() {
		try {
			currentCardData = sc.station.cardReader.tap(cardFromKind(selectedCardKind));
		} catch (Exception e) {
			sc.triggerUnknownReasonForPaymentFailScreen(
					"Error of type: \"" + e.getClass().getName() + "\" caused when tapping card");
		}
		if (currentCardData == null) {
			System.out.println("Unknown error caused when tapping card");
			sc.triggerUnknownReasonForPaymentFailScreen(null);
		}
	}

	public void swipeCard() {
		try {
			currentCardData = sc.station.cardReader.swipe(cardFromKind(selectedCardKind));
		} catch (Exception e) {
			sc.triggerUnknownReasonForPaymentFailScreen(
					"Error of type: \"" + e.getClass().getName() + "\" caused when swiping card");
		}
		if (currentCardData == null) {
			System.out.println("Unknown error caused when swiping card");
			sc.triggerUnknownReasonForPaymentFailScreen(null);
		}
	}

	public void enablePayments() {
		for (WalletControlListener l : listeners)
			l.cardPaymentsEnabled(this);
	}

	public void disablePayments() {
		for (WalletControlListener l : listeners)
			l.cardPaymentsDisabled(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "cc2":
				selectCard("MAST");
				break;
			case "cc1":
				selectCard("VISA");
				break;
			case "cc0":
				selectCard("AMEX");
				break;
			case "m":
				selectCard("MEMBERSHIP");
				break;
			case "insert":
				disablePayments();
				sc.askForPin(c);
				break;
			case "tap":
				disablePayments();
				tapCard();
				break;
			case "swipe":
				disablePayments();
				swipeCard();
				break;
			case "remove":
				sc.station.cardReader.remove();
				break;
			default:
				break;
			}
		} catch (Exception ex) {

		}
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void cardInserted(CardReader reader) {
		for (WalletControlListener l : listeners)
			l.cardWithPinInserted(this);
	}

	@Override
	public void cardRemoved(CardReader reader) {
		for (WalletControlListener l : listeners)
			l.cardWithPinRemoved(this);
		sc.getPinPadControl().exitPinPad();
	}

	@Override
	public void cardTapped(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardSwiped(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * @author ericmao
	 * I was thinking about how to re-design how we read card data from CardReader. Since we have different  
	 * types of cards now, payment will vary (maybe) depending what kind of card we will insert. So 
	 * I'm thinking about using a switch statement or equivalent for each kind of card.
	 * I will probably bring this up the next meeting we do. RN I've just put this here b/c I don't
	 * know if this should go in SystemControl or WalletControl since I don't know what WalletControl does.
	 */
	public void cardDataRead(CardReader reader, CardData data) {
		// TODO Auto-generated method stub

	}
}
