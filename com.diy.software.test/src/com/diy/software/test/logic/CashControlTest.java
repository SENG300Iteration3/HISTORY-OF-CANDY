package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.CashControlListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.banknote.BanknoteValidatorObserver;

import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.SimulationException;

public class CashControlTest {
	StationControl sc;
	CashControl cs;
	BanknoteValidatorObserverStub bns;
	Currency currency;
	
	@Before
	public void setUp() throws Exception {
		sc = new StationControl();
		cs = new CashControl(sc);
		bns = new BanknoteValidatorObserverStub();
		
		this.currency = Currency.getInstance("CAD");
				
		cs.addListener(bns);
		PowerGrid.engageUninterruptiblePowerSource();
	}

	@Test
	public void testAddListener() {
		cs.addListener(bns);
		cs.enablePayments();
		assertTrue(bns.insertionEnabled);
	}

	@Test
	public void testRemoveListener() {
		cs.removeListener(bns);
		cs.enablePayments();
		assertFalse(bns.insertionEnabled);
	}

	@Test
	public void testEnablePayments() {
		cs.enablePayments();
		assertTrue(bns.insertionEnabled);
		
	}

	@Test
	public void testDisablePayments() {
		cs.disablePayments();
		assertTrue(bns.insertionDisabled);
	}

	@Test
	public void testCoinAdded() {
		
		bns.cashInserted = false;
		cs.coinAdded(null);
		assertTrue(bns.cashInserted);
	}

	@Test
	public void testBanknoteAdded() {
		
		bns.cashInserted = false;
		cs.banknoteAdded(null);
		assertTrue(bns.cashInserted);
	}

	@Test
	public void testValidCoinDetected() {
		fail("Not yet implemented");
	}

	@Test
	public void testValidBanknoteDetected() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvalidBanknoteDetected() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvalidCoinDetected() {
		fail("Not yet implemented");
	}

	@Test
	public void testBanknotesFull() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testBanknotesInStorageLow() throws SimulationException, TooMuchCashException {
		for (int i = 0; i < 1; i++) {
			sc.station.banknoteStorage.load(new Banknote(currency, 20));
		}
		assertTrue(cs.banknotesInStorageLow(sc.station.banknoteStorage));
	}
	
	@Test
	public void testBanknotesInStorageNotLow() throws SimulationException, TooMuchCashException {
		for (int i = 0; i < 1000; i++) {
			sc.station.banknoteStorage.load(new Banknote(currency, 20));
		}
		assertFalse(cs.banknotesInStorageLow(sc.station.banknoteStorage));
	}
	
	@Test
	public void testBanknotesInStorageAtThreshold() throws SimulationException, TooMuchCashException {
		for (int i = 0; i < 50; i++) {
			sc.station.banknoteStorage.load(new Banknote(currency, 20));
		}
		assertTrue(cs.banknotesInStorageLow(sc.station.banknoteStorage));
	}
	
	/*
	@Test
	public void testBanknotesLoaded() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testBanknotesUnloaded() {
		fail("Not yet implemented");
	}
*/
	@Test
	public void testCoinsFull() {
		fail("Not yet implemented");
	}
/*
	@Test
	public void testCoinsLoaded() {
		fail("Not yet implemented");
	}
	*?
/*
	@Test
	public void testCoinsUnloaded() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsPaymentEnough() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateChange() {
		// not used 
		fail("Not yet implemented");
	}*/

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
		
		public boolean insertionEnabled = false;
		public boolean insertionDisabled = false;
		public boolean cashInserted = false;
		
		/*@Override
		public void cashInsertionEnabled(CashControl cc) {
			insertionEnabled = true;
			
		}

		@Override
		public void cashInsertionDisabled(CashControl cc) {
			insertionDisabled = true;
			
		}*/

		@Override
		public void cashInserted(CashControl cc) {
			cashInserted = true;
			
		}

		@Override
		public void coinInsertionEnabled(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noteInsertionEnabled(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinInsertionDisabled(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noteInsertionDisabled(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

//		@Override
//		public void cashRejected(CashControl cc) {
//			// TODO Auto-generated method stub
//			
//		}

		@Override
		public void changeReturned(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void paymentFailed(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkCashRejected(CashControl cc) {
			// TODO Auto-generated method stub
			
		}
	}
}
