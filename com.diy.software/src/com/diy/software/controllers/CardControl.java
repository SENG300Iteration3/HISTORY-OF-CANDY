package com.diy.software.controllers;

import com.diy.hardware.external.CardIssuer;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.fakedata.GiftcardDatabase;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;

public class CardControl implements CardReaderListener{
	public FakeDataInitializer fakeData;
	
	private StationControl sc;
	
	public CardControl(StationControl sc) {
		this.sc = sc;
		sc.station.cardReader.register(this);
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void cardInserted(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardRemoved(CardReader reader) {}

	@Override
	public void cardTapped(CardReader reader) {}

	@Override
	public void cardSwiped(CardReader reader) {}

	@Override
	// <<<<<<< HEAD
	/*
	 * Reads the data from the card and pays for the transaction using the card. On
	 * success, the amount owed will be updated to 0.0 and the hold placed on the
	 * card will be resolved. paymentSuccess is set to true. On failure by the bank
	 * being unable to authorize a hold, the amount due will not change.
	 * paymentSuccess is set to false. On failure by the bank being unable to post
	 * the transaction, the card's credit limit will not change. paymentSuccess is
	 * set to false.
	 */
	public void cardDataRead(CardReader reader, CardData data) {
		Double amountOwed = sc.getItemsControl().getCheckoutTotal();
		String cardNumber = data.getNumber();
		CardIssuer bank = fakeData.getCardIssuer();
		
		if(data.getType().equals(GiftcardDatabase.CompanyGiftCard)) {
			Double amountOnCard = GiftcardDatabase.giftcardMap.get(cardNumber);
			Double dif = amountOnCard - amountOwed;
			if(dif >= 0) {
				GiftcardDatabase.giftcardMap.put(cardNumber, dif);
				sc.getItemsControl().updateCheckoutTotal(-amountOwed);
				sc.paymentHasBeenMade(data);
			}else {
				GiftcardDatabase.giftcardMap.put(cardNumber, 0.0);
				sc.getItemsControl().updateCheckoutTotal(-amountOnCard);
				//TODO: tell customer that their card wasn't enough maybe?
			}
			return;
		}

		long holdNum = bank.authorizeHold(cardNumber, amountOwed);
		if (holdNum <= -1) {
			sc.paymentHasBeenCanceled(data);
		} else if (bank.postTransaction(cardNumber, holdNum, amountOwed)) {
			bank.releaseHold(cardNumber, holdNum);
			sc.getItemsControl().updateCheckoutTotal(-sc.getItemsControl().getCheckoutTotal());
			sc.paymentHasBeenMade(data);
			sc.getItemsControl().updateCheckoutTotal(0);
			return;
		}
		sc.paymentHasBeenCanceled(data);
	}

}
