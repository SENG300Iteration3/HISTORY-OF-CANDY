package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.diy.hardware.DoItYourselfStation;
import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.CashControlListener;
import com.unitedbankingservices.DisabledException;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.banknote.BanknoteDispenserMR;
import com.unitedbankingservices.banknote.BanknoteValidatorObserver;
import com.unitedbankingservices.coin.Coin;
import com.unitedbankingservices.coin.CoinDispenserAR;

import ca.powerutility.PowerGrid;

public class CashControlTest {
	StationControl sc;
	CashControl cs;
	ItemsControl ic;
	
	BanknoteValidatorObserverStub bns;
	
	public static void configureDoItYourselfStation() {
	    DoItYourselfStation.configureBanknoteDenominations(new int[] { 100, 50, 20, 10, 5, 1 });
	    DoItYourselfStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.valueOf(2.00), BigDecimal.valueOf(1.00), 
	    		BigDecimal.valueOf(0.25), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.01) });
	}
	
	public void fillNotes(DoItYourselfStation station) {
		for(int i = 0; station.banknoteStorage.hasSpace(); i++) {
			try {
				station.banknoteStorage.receive(new Banknote(Currency.getInstance("CAD"), 100));
			} catch (DisabledException | TooMuchCashException e) {}
		}
	}
	
	public void fillCoins(DoItYourselfStation station) {
		for(int i = 0; station.coinStorage.hasSpace(); i++) {
			try {
				station.coinStorage.receive(new Coin(Currency.getInstance("CAD"), BigDecimal.valueOf(2.0)));
			} catch (DisabledException | TooMuchCashException e) {}
		}
	}
	
	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		configureDoItYourselfStation();
		sc = new StationControl(new FakeDataInitializer());
		cs = sc.getCashControl();
		ic = sc.getItemsControl();
		bns = new BanknoteValidatorObserverStub();
		
		cs.addListener(bns);
		cs.paymentFailed();
	}

	@Test
	public void testAddListener() {
		ic.updateCheckoutTotal(1.0);
		cs.addListener(bns);
		cs.enablePayments();
		assertTrue(bns.coininsertionEnabled);
		assertTrue(bns.noteinsertionEnabled);
	}

	@Test
	public void testRemoveListener() {
		ic.updateCheckoutTotal(0.0);
		cs.removeListener(bns);
		cs.enablePayments();
		assertFalse(bns.coininsertionEnabled);
		assertFalse(bns.noteinsertionEnabled);
	}

	@Test
	public void testEnablePayments() {
		ic.updateCheckoutTotal(1.0);
		cs.enablePayments();
		assertTrue(bns.coininsertionEnabled);
		assertTrue(bns.noteinsertionEnabled);
	}

	@Test
	public void testDisablePayments() {
		ic.updateCheckoutTotal(1.0);
		cs.disablePayments();
		assertFalse(bns.coininsertionEnabled);
		assertFalse(bns.noteinsertionEnabled);
	}

	@Test
	public void testCoinAdded() {
		ic.updateCheckoutTotal(1.0);
		cs.enablePayments();
		bns.cashInserted = false;
		try {
			sc.station.coinSlot.receive(new Coin(Currency.getInstance("CAD"), BigDecimal.valueOf(2.0)));
		} catch (DisabledException | TooMuchCashException e) {}
		assertTrue(bns.cashInserted);
	}

	@Test
	public void testBanknoteAdded() {
		ic.updateCheckoutTotal(1.0);
		cs.enablePayments();
		bns.cashInserted = false;
		try {
			sc.station.banknoteInput.receive(new Banknote(Currency.getInstance("CAD"), 100));
		} catch (DisabledException | TooMuchCashException e) {}
		assertTrue(bns.cashInserted);
	}

	@Test
	public void testValidCoinDetected() {
		ic.updateCheckoutTotal(1000.0);
		cs.enablePayments();
		bns.cashInserted = false;
		while(ic.getCheckoutTotal() == 1000.0) {
			try {
				sc.station.coinSlot.receive(new Coin(Currency.getInstance("CAD"), BigDecimal.valueOf(1.0)));
			} catch (DisabledException | TooMuchCashException e) {}
		}
		assertTrue(bns.cashInserted);
	}

	@Test
	public void testValidBanknoteDetected() {
		ic.updateCheckoutTotal(1000.0);
		cs.enablePayments();
		bns.cashInserted = false;
		while(ic.getCheckoutTotal() == 1000.0) {
			try {
				sc.station.banknoteInput.receive(new Banknote(Currency.getInstance("CAD"), 100));
			} catch (DisabledException | TooMuchCashException e) {}
		}
		assertTrue(bns.cashInserted);
	}

	@Test
	public void testInvalidBanknoteDetected() {
		ic.updateCheckoutTotal(100.0);
		bns.cashRejected = false;
		while(!bns.cashRejected) {
			cs.enablePayments();
			ActionEvent e = new ActionEvent(this, 0, "d 100");
			cs.actionPerformed(e);
			ic.updateCheckoutTotal(100.0);
		}
		assertTrue(bns.cashRejected);
	}

	@Test
	public void testInvalidCoinDetected() {
		ic.updateCheckoutTotal(1.0);
		bns.cashRejected = false;
		while(!bns.cashRejected) {
			cs.enablePayments();
			ActionEvent e = new ActionEvent(this, 0, "c 100");
			cs.actionPerformed(e);
			ic.updateCheckoutTotal(1.0);
		}
		assertTrue(bns.cashRejected);
	}

	@Test
	public void testBanknotesFull() {
		fillNotes(sc.station);
		ic.updateCheckoutTotal(1000.0);
		cs.enablePayments();
		ActionEvent e = new ActionEvent(this, 0, "d 100");
		cs.actionPerformed(e);
		assertFalse(bns.noteinsertionEnabled);
	}
	
	@Test
	public void emptyBanknotes() {
		fillNotes(sc.station);
		ic.updateCheckoutTotal(1000.0);
		sc.station.banknoteStorage.unload();
		cs.enablePayments();
		assertTrue(bns.noteinsertionEnabled);
	}
	
	@Test
	public void emptyCoins() {
		fillCoins(sc.station);
		ic.updateCheckoutTotal(1000.0);
		sc.station.coinStorage.unload();
		cs.enablePayments();
		assertTrue(bns.coininsertionEnabled);
	}
	
	@Test
	public void testCoinsFull() {
		fillCoins(sc.station);
		ic.updateCheckoutTotal(1000.0);
		cs.enablePayments();
		ActionEvent e = new ActionEvent(this, 0, "c 100");
		cs.actionPerformed(e);
		assertFalse(bns.coininsertionEnabled);
	}
	
	@Test
	public void badCoin() {
		ic.updateCheckoutTotal(1000.0);
		cs.enablePayments();
		ActionEvent e = new ActionEvent(this, 0, "c -1");
		cs.actionPerformed(e);
		assertTrue(ic.getCheckoutTotal() == 1000.0);
	}
	
	@Test
	public void returnChange() {
		ic.updateCheckoutTotal(22.22);
		cs.enablePayments();
		ActionEvent e = new ActionEvent(this, 0, "d 100");
		while(ic.getCheckoutTotal() == 22.22) {
			cs.actionPerformed(e);
		}
		assertTrue(Math.abs(bns.lastReturnedCash-77.78) < 0.01);
	}
	
	@Test
	public void returnChange2() {
		ic.updateCheckoutTotal(1.00);
		cs.enablePayments();
		ActionEvent e = new ActionEvent(this, 0, "c 2.0");
		while(ic.getCheckoutTotal() == 1.00) {
			cs.actionPerformed(e);
		}
		assertTrue(Math.abs(bns.lastReturnedCash-1.00) < 0.01);
	}
	
	@Test
	public void returnChange3() {
		ic.updateCheckoutTotal(4.96);
		cs.enablePayments();
		
		ActionEvent e = new ActionEvent(this, 0, "d 10");
		for(CoinDispenserAR i : sc.station.coinDispensers.values()) {
			i.disable();
		}
		for(BanknoteDispenserMR i : sc.station.banknoteDispensers.values()) {
			i.disable();
		}
		while(ic.getCheckoutTotal() == 4.96) {
			cs.actionPerformed(e);
		}
		assertTrue(Math.abs(bns.lastReturnedCash-0) < 0.01);
	}

	@Test
	public void testActionPerformed() {
		try {
			ActionEvent e = new ActionEvent(this, 0, "d 123");
			cs.actionPerformed(e);
		}catch(Exception e) {
			fail("exception thrown");
		}
	}
	
	@Test
	public void testActionPerformedc() {
		try {
			ActionEvent e = new ActionEvent(this, 0, "c 123");
			cs.actionPerformed(e);
		}catch(Exception e) {
			fail("exception thrown");
		}
	}
	
	@Test
	public void testActionPerformedNull() {
		try {
			ActionEvent e = new ActionEvent(this, 0, "x 123");
			cs.actionPerformed(e);
		}catch(Exception e) {
			
		}
	}
	
	
	public class BanknoteValidatorObserverStub implements CashControlListener, BanknoteValidatorObserver{
		
		public boolean coininsertionEnabled = false;
		public boolean noteinsertionEnabled = false;
		public boolean cashInserted = false;
		public boolean changeReturned = false;
		public boolean paymentFailed = false;
		public boolean cashRejected = false;
		public double lastReturnedCash = 0.0;

		@Override
		public void cashInserted(CashControl cc) {
			cashInserted = true;
			
		}

		@Override
		public void coinInsertionEnabled(CashControl cc) {
			coininsertionEnabled = true;
			
		}

		@Override
		public void noteInsertionEnabled(CashControl cc) {
			noteinsertionEnabled = true;
			
		}

		@Override
		public void coinInsertionDisabled(CashControl cc) {
			coininsertionEnabled = false;
			
		}

		@Override
		public void noteInsertionDisabled(CashControl cc) {
			noteinsertionEnabled = false;
			
		}

		@Override
		public void changeReturned(CashControl cc) {
			changeReturned = true;
			List<Coin> c = sc.station.coinTray.collectCoins();
			List<Banknote> b = null; 
			if(sc.station.banknoteOutput.hasDanglingBanknotes()) {
				b = sc.station.banknoteOutput.removeDanglingBanknotes();
			}
			double returnedCash = 0;
			for(Coin i : c) {
				returnedCash += i.getValue().doubleValue();
			}
			if(b != null) {
				for(Banknote i : b) {
					returnedCash += i.getValue();
				}
			}
			lastReturnedCash = returnedCash;
		}

		@Override
		public void paymentFailed(CashControl cc) {
			cashRejected = true;
			
		}

		@Override
		public void checkCashRejected(CashControl cc) {
			List<Coin> c = sc.station.coinTray.collectCoins();
			Banknote b = null; 
			if(sc.station.banknoteInput.hasDanglingBanknotes()) {
				b = sc.station.banknoteInput.removeDanglingBanknote();
			}
			double returnedCash = 0;
			for(Coin i : c) {
				returnedCash += i.getValue().doubleValue();
				cashRejected = true;
			}
			if(b != null) {
				returnedCash += b.getValue();
				cashRejected = true;
			}
			if(returnedCash != 0.0) {
				lastReturnedCash = returnedCash;
			}
		}
	}
}