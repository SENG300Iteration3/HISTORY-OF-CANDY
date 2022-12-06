package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.CashControlListener;
import com.diy.software.listeners.WalletControlListener;
import com.diy.software.test.gui.PresentCardScreenTest.WalletControlListenerStub;
import com.diy.software.test.logic.StubSystem;

import ca.powerutility.PowerGrid;
import swing.screens.PresentCardScreen;
import swing.screens.PresentGiftCardOrCashScreen;

public class PresentGiftCardOrCashScreenTest {
	
	FakeDataInitializer fdi;
	StationControl sc;
	PresentGiftCardOrCashScreen screen;
	StubSystem sStub;
	CashControlListenerStub cStub;
	WalletControlListenerStub wStub;

	@Before
	public void setUp() throws Exception {
PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		
		sStub = new StubSystem();
		sc.register(sStub);
		
		cStub = new CashControlListenerStub();
		sc.getCashControl().addListener(cStub);
		
		wStub = new WalletControlListenerStub();
		sc.getWalletControl().addListener(wStub);
	}

	@After
	public void tearDown() throws Exception {
		fdi = null;
		sc = null;
		screen = null;
		sStub = null;
		cStub = null; 
	}
	
	@Test
	public void testScreenIsGiftCard() {
		screen = new PresentGiftCardOrCashScreen(sc, true);
		assertTrue(wStub.cardPaymentEnabled);
		assertFalse(cStub.coinInsertionEnabled);
		assertFalse(cStub.noteInsertionEnabled);
	}
	
	@Test
	public void testScreenIsCashZeroCheckoutTotal() {
		screen = new PresentGiftCardOrCashScreen(sc, false);
		assertFalse(wStub.cardPaymentEnabled);
		assertFalse(cStub.coinInsertionEnabled);
		assertFalse(cStub.noteInsertionEnabled);
	}
	
	@Test
	public void testScreenIsCashNonZeroCheckoutTotal() {
		sc.customer.selectNextItem();
		sc.customer.scanItem(false); //FIXME: dealing with scanning failures
		sc.customer.placeItemInBaggingArea();
		screen = new PresentGiftCardOrCashScreen(sc, false);
		assertFalse(wStub.cardPaymentEnabled);
		assertTrue(cStub.coinInsertionEnabled);
		assertTrue(cStub.noteInsertionEnabled);
	}

	@Test
	public void testBackButton() {
		screen = new PresentGiftCardOrCashScreen(sc, false);
		screen.getBackButton().doClick();
		assertFalse(cStub.coinInsertionEnabled);
		assertFalse(cStub.noteInsertionEnabled);
		assertTrue(sStub.triggerPanelBack);
	}

	public class CashControlListenerStub implements CashControlListener {
		
		public boolean coinInsertionEnabled = false;
		public boolean noteInsertionEnabled = false;

		@Override
		public void coinInsertionEnabled(CashControl cc) {
			coinInsertionEnabled = true;
			
		}

		@Override
		public void noteInsertionEnabled(CashControl cc) {
			noteInsertionEnabled = true;
			
		}

		@Override
		public void coinInsertionDisabled(CashControl cc) {
			coinInsertionEnabled = false;
			
		}

		@Override
		public void noteInsertionDisabled(CashControl cc) {
			noteInsertionEnabled = false;
			
		}

		@Override
		public void cashInserted(CashControl cc) {
			// TODO Auto-generated method stub
			
		}

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
	
	public class WalletControlListenerStub implements WalletControlListener {

		public boolean cardPaymentEnabled = false;
		
		@Override
		public void cardHasBeenSelected(WalletControl wc) {
			// TODO Auto-generated method stub
			
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
		public void cardPaymentsEnabled(WalletControl wc) {
			cardPaymentEnabled = true;
			
		}

		@Override
		public void cardPaymentsDisabled(WalletControl wc) {
			cardPaymentEnabled = false;
			
		}

		@Override
		public void cardHasBeenInserted(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardWithPinInserted(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardWithPinRemoved(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputCanceled(WalletControl walletControl) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
