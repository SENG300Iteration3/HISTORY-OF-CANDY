package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;

import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.SystemControl;
import com.diy.software.listeners.CashControlListener;
import com.diy.software.listeners.MembershipControlListener;
import com.unitedbankingservices.banknote.BanknoteValidatorObserver;

import ca.powerutility.NoPowerException;
import ca.powerutility.PowerGrid;

public class CashControlTest {
	SystemControl sc;
	CashControl cs;
	BanknoteValidatorObserverStub bns;
	
	@Before
	public void setUp() throws Exception {
		sc = new SystemControl();
		cs = new CashControl(sc);
		bns = new BanknoteValidatorObserverStub();
		
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
		
		@Override
		public void cashInsertionEnabled(CashControl cc) {
			insertionEnabled = true;
			
		}

		@Override
		public void cashInsertionDisabled(CashControl cc) {
			insertionDisabled = true;
			
		}

		@Override
		public void cashInserted(CashControl cc) {
			cashInserted = true;
			
		}
	}
}
