package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;

import ca.powerutility.PowerGrid;

public class WalletControlTest {
	WalletControl wc;
	StationControl sc;
	ReaderStub readStub;
	Card card1;
	Card card2;
	Card card3;
	WalletStub wStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		FakeDataInitializer fdi = new FakeDataInitializer();
		fdi.addCardData();
		sc = new StationControl(fdi);
		wc = new WalletControl(sc); 
		readStub = new ReaderStub();
		
		sc.station.cardReader.deregisterAll();
		sc.station.cardReader.register(readStub);
		Card[] cards = sc.fakeData.getCards();
		card1 = cards[0];
		card2 = cards[1];
		card3 = cards[2];
		sc.customer.wallet.cards.clear();
		sc.customer.wallet.cards.add(card1);
		wStub = new WalletStub();
		sc.station.cardReader.register(wc);
		sc.station.cardReader.turnOff();
		sc.station.cardReader.plugIn();
		sc.station.cardReader.turnOn();
		sc.station.cardReader.enable();
		
	}
	
	@Test
	public void testAddListener() {
		wStub.inserted = false;
		wc.cardInserted(sc.station.cardReader);
		assertFalse(wStub.inserted);
		
		wc.addListener(wStub);
		
		wStub.inserted = false;
		wc.cardInserted(sc.station.cardReader);
		assertTrue(wStub.inserted);
	}
	
	
	@Test
	public void testRemoveListener() {
		wc.addListener(wStub);
		wStub.inserted = false;
		wc.cardInserted(sc.station.cardReader);
		assertTrue(wStub.inserted);
		
		wc.removeListener(wStub);
		
		wStub.inserted = false;
		wc.cardInserted(sc.station.cardReader);
		assertFalse(wStub.inserted);
	}
	

	@Test
	public void testSelectCard() {
		
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertTrue(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card1));
		
	}
	
	@Test
	public void testSelectCardWithListener() {
		wc.addListener(wStub);
		
		wStub.cardHasBeenSelected = false;
		
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertTrue(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card1));
		assertTrue(wStub.cardHasBeenSelected);
		
	}
	
	@Test
	public void testSelectCardAlreadySelected() {
		sc.customer.wallet.cards.add(card2);
		
		assertTrue(sc.customer.wallet.cards.contains(card1));
		assertTrue(sc.customer.wallet.cards.contains(card2));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		assertTrue(sc.customer.wallet.cards.contains(card2));
		wc.selectCard("VISA");
		assertTrue(sc.customer.wallet.cards.contains(card1));
		assertFalse(sc.customer.wallet.cards.contains(card2));
		
	}
	
	@Test
	public void testInsertCardWrongPin() {
		
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		
		assertFalse(readStub.cardInserted);
		wc.insertCard("1");
		assertTrue(readStub.cardInserted);
		sc.station.cardReader.remove();
	}
	
	@Test
	public void testInsertCardNoCard() {
		readStub.cd = null;
		assertFalse(readStub.cardInserted);
		wc.insertCard("1");
		assertFalse(readStub.cardInserted);
		assertNull(readStub.cd);
	}
	
	@Test
	public void testInsertCard() {
		
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		
		readStub.cd = null;
		assertFalse(readStub.cardInserted);
		
		int i = 0;
		while ((readStub.cd == null) && (i < 50)) {
			wc.insertCard("1234");
			
			if (readStub.cd == null && readStub.cardInserted) {
				sc.station.cardReader.remove();
			}
			
			i++;
		}
		assertTrue(i != 50);
		assertTrue(readStub.cardInserted);
		assertNotNull(readStub.cd);
		
		sc.station.cardReader.remove();
		
	}
	
	@Test
	public void testTapCard() {
		sc.customer.wallet.cards.clear();
		sc.customer.wallet.cards.add(card2);
		wc.selectCard("VISA");
		assertFalse(sc.customer.wallet.cards.contains(card2));
		
		readStub.cd = null;
		assertFalse(readStub.cardTapped);
		int i = 0;
		while ((readStub.cd == null) && (i < 50)) {
			wc.tapCard();
			i++;
		}
		assertTrue(i != 50);
		assertTrue(readStub.cardTapped);
		assertNotNull(readStub.cd);
		
	}
	
	@Test
	public void testTapCardNoTap() {
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		
		readStub.cd = null;
		
		assertFalse(readStub.cardTapped);
		wc.tapCard();
		assertFalse(readStub.cardTapped);
		assertNull(readStub.cd);
	}
	
	@Test
	public void testTapCardNoCard() {
		readStub.cd = null;
		assertFalse(readStub.cardTapped);
		wc.tapCard();
		assertFalse(readStub.cardTapped);
		assertNull(readStub.cd);
	}

	@Test
	public void testSwipeCard() {
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		readStub.cd = null;
		
		assertFalse(readStub.cardSwiped);
		int i = 0;
		while ((readStub.cd == null) && (i < 50)) {
			wc.swipeCard();
			
			i++;
		}
		assertTrue(i != 50);
		assertTrue(readStub.cardSwiped);
		assertNotNull(readStub.cd);
	}
	
	@Test
	public void testSwipeCardNoCard() {
		readStub.cd = null;
		assertFalse(readStub.cardSwiped);
		wc.swipeCard();
		assertFalse(readStub.cardSwiped);
		assertNull(readStub.cd);
	}
	
	@Test
	public void testEnablePayments() {
		wc.addListener(wStub);
		wStub.paymentsEnabled = false;
		wc.enablePayments();
		assertTrue(wStub.paymentsEnabled);
	}
	
	@Test
	public void testDisablePayments() {
		wc.addListener(wStub);
		wStub.paymentsEnabled = true;
		wc.disablePayments();
		assertFalse(wStub.paymentsEnabled);
	}
	
	@Test
	public void testActionPerformedMAST() {
		ActionEvent e = new ActionEvent(this, 0, "cc2");
		sc.customer.wallet.cards.clear();
		sc.customer.wallet.cards.add(card3);
		
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertTrue(sc.customer.wallet.cards.contains(card3));
		wc.actionPerformed(e);
		assertTrue(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card3));
	}
	
	@Test
	public void testActionPerformedVISA() {
		ActionEvent e = new ActionEvent(this, 0, "cc1");
		sc.customer.wallet.cards.clear();
		sc.customer.wallet.cards.add(card2);
		
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertTrue(sc.customer.wallet.cards.contains(card2));
		wc.actionPerformed(e);
		assertTrue(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card2));
	}
	
	@Test
	public void testActionPerformedAMEX() {
		ActionEvent e = new ActionEvent(this, 0, "cc0");
		sc.customer.wallet.cards.clear();
		sc.customer.wallet.cards.add(card1);
		
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.actionPerformed(e);
		assertTrue(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card1));
	}
	
	
	@Test
	public void testActionPerformedInsert() {
		ActionEvent e = new ActionEvent(this, 0, "insert");
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		
		readStub.cd = null;
		assertFalse(readStub.cardInserted);
		
		StubSystem scStub = new StubSystem();
		
		sc.listeners.add(scStub);
		
		scStub.paymentType = null;
		int i = 0;
		while ((scStub.paymentType == null) && (i < 50)) {
			wc.actionPerformed(e);
			
			if (readStub.cd == null && readStub.cardInserted) {
				sc.station.cardReader.remove();
			}
			
			i++;
		}
		assertTrue(i != 50);
		assertNotNull(scStub.paymentType);
		assertTrue(scStub.paymentType.equals("insert"));
	}
	
	@Test
	public void testActionPerformedTap() {
		ActionEvent e = new ActionEvent(this, 0, "tap");
		sc.customer.wallet.cards.clear();
		sc.customer.wallet.cards.add(card2);
		wc.selectCard("VISA");
		assertFalse(sc.customer.wallet.cards.contains(card2));
		
		readStub.cd = null;
		assertFalse(readStub.cardTapped);
		int i = 0;
		while ((readStub.cd == null) && (i < 50)) {
			wc.actionPerformed(e);
			i++;
		}
		assertTrue(i != 50);
		assertTrue(readStub.cardTapped);
		assertNotNull(readStub.cd);
	}
	
	@Test
	public void testActionPerformedSwipe() {
		ActionEvent e = new ActionEvent(this, 0, "swipe");
		assertTrue(sc.customer.wallet.cards.contains(card1));
		wc.selectCard("AMEX");
		assertFalse(sc.customer.wallet.cards.contains(card1));
		
		readStub.cd = null;
		assertFalse(readStub.cardSwiped);
		int i = 0;
		while ((readStub.cd == null) && (i < 50)) {
			wc.actionPerformed(e);
			i++;
		}
		assertTrue(i != 50);
		assertTrue(readStub.cardSwiped);
		assertNotNull(readStub.cd);
	}
	
	@Test
	public void testActionPerformedRemove() {
		ActionEvent e = new ActionEvent(this, 0, "remove");
		
		readStub.cardInserted = false;
		try {
			sc.station.cardReader.insert(card1, "");
		} catch (IOException e1) {
		}
		assertTrue(readStub.cardInserted);
		wc.actionPerformed(e);
		assertFalse(readStub.cardInserted);
	}
	
	@Test
	public void testActionPerformedNothingHappened() {
		ActionEvent e = new ActionEvent(this, 0, "");
		readStub.cd = null;
		assertFalse(readStub.cardSwiped);
		assertFalse(readStub.cardInserted);
		assertFalse(readStub.cardTapped);
		wc.actionPerformed(e);
		assertFalse(readStub.cardSwiped);
		assertFalse(readStub.cardInserted);
		assertFalse(readStub.cardTapped);
		assertNull(readStub.cd);
	}
	
	@Test
	public void testActionPerformedNoCard() {
		ActionEvent e = new ActionEvent(this, 0, "cc2");
		
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card3));
		wc.actionPerformed(e);
		assertFalse(sc.customer.wallet.cards.isEmpty());
		assertFalse(sc.customer.wallet.cards.contains(card3));
	}
	
	@Test
	public void testCardInserted() {
		wc.addListener(wStub);
		
		wStub.inserted = false;
		wc.cardInserted(sc.station.cardReader);
		assertTrue(wStub.inserted);
	}
	
	@Test
	public void testCardRemoved() {
		wc.addListener(wStub);
		
		wStub.inserted = true;
		wc.cardRemoved(sc.station.cardReader);
		assertFalse(wStub.inserted);
	}
	
	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		sc.station.cardReader.disable();
		sc.station.cardReader.turnOff();
		sc.station.cardReader.unplug();
	}
	
	public class ReaderStub implements CardReaderListener {
		boolean cardSelected = false;
		boolean enabled = true;
		boolean turnOn = true;
		boolean cardInserted = false;
		boolean cardTapped = false;
		boolean cardSwiped = false;
		CardData cd;


		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			enabled = true;
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			enabled = false;
			
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			turnOn = true;
			
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			turnOn = false;
			
		}

		@Override
		public void cardInserted(CardReader reader) {
			cardInserted = true;
			
		}

		@Override
		public void cardRemoved(CardReader reader) {
			cardInserted = false;
			
		}

		@Override
		public void cardTapped(CardReader reader) {
			cardTapped = true;
			
		}

		@Override
		public void cardSwiped(CardReader reader) {
			cardSwiped = true;
			
		}

		@Override
		public void cardDataRead(CardReader reader, CardData data) {
			cd = data;
			
		}
	}
	
	public class WalletStub implements WalletControlListener{

		public boolean cardHasBeenSelected = false;
		public boolean paymentsEnabled = false;
		public boolean inserted = false;

		@Override
		public void cardHasBeenSelected(WalletControl wc) {
			cardHasBeenSelected = true;
			
		}

		@Override
		public void cardPaymentsEnabled(WalletControl wc) {
			paymentsEnabled = true;
			
		}

		@Override
		public void cardPaymentsDisabled(WalletControl wc) {
			paymentsEnabled = false;
			
		}

		@Override
		public void cardHasBeenInserted(WalletControl wc) {
			
		}

		@Override
		public void cardWithPinInserted(WalletControl wc) {
			inserted = true;
			
		}

		@Override
		public void cardWithPinRemoved(WalletControl wc) {
			inserted = false;
			
		}

		@Override
		public void membershipCardHasBeenSelected(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputEnabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputCanceled(WalletControl walletControl) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
