package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.BagDispenserControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.BagDispenserControlListener;
import com.diy.software.listeners.StationControlListener;
import com.diy.software.test.gui.MembershipScreenTest.StationControlListenerStub;
import com.diy.software.test.logic.MembershipControlTest;
import com.jimmyselectronics.opeechee.Card.CardData;

import ca.powerutility.PowerGrid;
import swing.screens.PurchaseBagScreen;

public class PurchaseBagScreenTest {
	FakeDataInitializer fdi;
	PurchaseBagScreen screen;
	StationControl sc;
	BagDispenserControlListenerStub bStub;
	StationControlListenerStub sStub;
	BagDispenserControl bdc;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		screen = new PurchaseBagScreen(sc);
		bdc = sc.getBagDispenserControl();
		
		bStub = new BagDispenserControlListenerStub();
		sStub = new StationControlListenerStub();
		sc.getBagDispenserControl().addListener(bStub);
		sc.getBagDispenserControl().addListener(screen);
		sc.register(sStub);
	}

	@After
	public void tearDown() throws Exception {
		fdi = null;
		screen = null;
		sc = null;
	}

	@Test
	public void testNumberPadButton() {
		screen.getNumberPadButtons()[0].doClick();
		assertEquals("1", bStub.number);
	}
	
	@Test
	public void testCancelButton() {
		screen.getCancelButton().doClick();
		assertTrue(sStub.panelTriggeredBack);
	}
	
	@Test
	public void testCorrectButton() {
		screen.getNumberPadButtons()[0].doClick();
		screen.getCorrectButton().doClick();
		assertEquals("", bStub.number);
	}
	
	@Test
	public void testCorrectButtonNothingInputted() {
		screen.getCorrectButton().doClick();
		assertNull(bStub.number);
	}
	
	@Test
	public void testSubmitButton() {
		int initBagsInStock = sc.getBagInStock();
		screen.getNumberPadButtons()[0].doClick();
		screen.getSubmitButton().doClick();
		assertEquals(initBagsInStock - 1, sc.getBagInStock());
	}
	
	@Test
	public void testSubmitButtonNotEnoughBags() {
		screen.getNumberPadButtons()[0].doClick();
		screen.getNumberPadButtons()[9].doClick();
		screen.getNumberPadButtons()[0].doClick();
		screen.getSubmitButton().doClick();
		assertTrue(sStub.notEnoughBagsInStock);
	}
	
	@Test
	public void testSubmitButtonNoBags() {
		screen.getNumberPadButtons()[0].doClick();
		screen.getNumberPadButtons()[9].doClick();
		screen.getNumberPadButtons()[9].doClick();
		screen.getSubmitButton().doClick();
		
		assertEquals(0, sc.getBagInStock());
		
		screen.getNumberPadButtons()[0].doClick();
		screen.getSubmitButton().doClick();
	
		assertTrue(sStub.noBagsInStock);
	}
	
	public class BagDispenserControlListenerStub implements BagDispenserControlListener{

		String number;
		
		@Override
		public void numberFieldHasBeenUpdated(BagDispenserControl bdp, String number) {
			this.number = number;
		}	
	}
	
	public class StationControlListenerStub implements StationControlListener{

		public boolean panelTriggeredBack = false;
		public boolean notEnoughBagsInStock = false;
		public boolean noBagsInStock = false;
		
		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void paymentsHaveBeenEnabled(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startMembershipCardInput(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputFinished(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputCanceled(StationControl systemControl, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void initiatePinInput(StationControl systemControl, String kind) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPanelBack(StationControl systemControl) {
			panelTriggeredBack = true;
			
		}

		@Override
		public void triggerInitialScreen(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPaymentWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerMembershipWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPurchaseBagsWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noBagsInStock(StationControl systemControl) {
			noBagsInStock = true;
			
		}

		@Override
		public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
			notEnoughBagsInStock = true;
			
		}

		@Override
		public void triggerPLUCodeWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerBrowsingCatalog(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
