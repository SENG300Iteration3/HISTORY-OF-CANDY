package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.enums.PaymentType;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.WalletControlListener;
import com.diy.software.test.logic.StubSystem;

import ca.powerutility.PowerGrid;
import swing.screens.PresentCardScreen;

public class PresentCardScreenTest {
	
	FakeDataInitializer fdi;
	StationControl sc;
	PresentCardScreen screen;
	StubSystem sStub;
	WalletControlListenerStub wStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		screen = new PresentCardScreen(sc, "card");
		
		sStub = new StubSystem();
		sc.register(sStub);
		
		wStub = new WalletControlListenerStub();
		sc.getWalletControl().addListener(wStub);
	}

	@After
	public void tearDown() throws Exception {
		fdi = null;
		sc = null;
		screen = null;
		sStub = null;
		wStub = null; 
	}

	@Test
	public void testBackButton() {
		screen.getBackButton().doClick();
		assertFalse(wStub.cardPaymentEnabled);
		assertTrue(sStub.triggerPanelBack);
	}

	public class WalletControlListenerStub implements WalletControlListener {

		public boolean cardPaymentEnabled = true;
		
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
			// TODO Auto-generated method stub
			
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
