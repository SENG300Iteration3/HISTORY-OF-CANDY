package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.MissingResourceException;

import com.diy.software.fakedata.GiftcardDatabase;
import com.diy.software.listeners.MembershipControlListener;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;

import swing.screens.OkayPromptScreen;

import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;


public class WalletControl implements ActionListener, CardReaderListener {
	private StationControl sc;
	private ArrayList<WalletControlListener> listeners;
	private String selectedCardKind;
	private Card selectedCard;
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
		
		//TODO: if there a better way to do this?
		for (Card card : sc.customer.wallet.cards) {
			if (card.kind == kind) {
				selectedCard = card;
				break;
			}
		}
		
		sc.customer.selectCard(kind);
		for (WalletControlListener l : listeners) {
			if (selectedCardKind == "MEMBERSHIP") {
				l.membershipCardHasBeenSelected(this);
			} else {
				l.cardHasBeenSelected(this);
			}
		}
			
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
	
	private void scanCard() {
		String number = selectedCard.number;
		Numeral[] code = new Numeral [number.length()];
		
		// Converting string number into Numeral array
		for (int i = 0; i < number.length(); i++) {
			Numeral digit = Numeral.valueOf(toByteDigit(number.charAt(i)));
			code[i] = digit;
		}
		Barcode barcode = new Barcode(code);
		BarcodedItem item = new BarcodedItem(barcode, 1);
		
		boolean scanSuccessful = sc.station.mainScanner.scan(item);
		if (!scanSuccessful) {
			sc.triggerMembershipCardInputFailScreen("Scan Failed. Please Try Again.");
		}
	}
	
	private byte toByteDigit(char c) {
		byte b;
		switch (c) {
			case '0':	return b = 0;
			case '1':	return b = 1;
			case '2':	return b = 2;
			case '3':	return b = 3;
			case '4':	return b = 4;
			case '5':	return b = 5;
			case '6':	return b = 6;
			case '7':	return b = 7;
			case '8':	return b = 8;
			case '9':	return b = 9;
			default:	return b = -1;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "giftcard":
				selectCard(GiftcardDatabase.CompanyGiftCard);
				break;
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
			case "scan":
				scanCard();
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
		
		//sc.getPinPadControl().exitPinPad();
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
	
	public void membershipCardInputEnabled() {
		for (WalletControlListener l : listeners) {
			l.membershipCardInputEnabled(this);
		}
	}

	public void membershipCardInputCanceled() {
		for (WalletControlListener l : listeners) {
			l.membershipCardInputCanceled(this);
		}
		
	}
}
