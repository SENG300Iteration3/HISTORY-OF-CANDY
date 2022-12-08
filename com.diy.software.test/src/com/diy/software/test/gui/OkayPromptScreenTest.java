package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

import ca.powerutility.PowerGrid;
import swing.screens.OkayPromptScreen;

public class OkayPromptScreenTest {
	
	OkayPromptScreen screen;
	StationControl sc;
	FakeDataInitializer fdi;
	StationControlListenerStub sStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		
		sStub = new StationControlListenerStub();
		sc.register(sStub);;
	}

	@After
	public void tearDown() throws Exception {
		screen = null;
		sc = null;
		fdi = null;
	}

	@Test
	public void testOkayButtonDoNotNavigateToInitialScreen() {
		screen = new OkayPromptScreen(sc, "message", false, true);
		screen.getOkayButton().doClick();
		assertTrue(sStub.panelBack);
		assertFalse(sStub.initialScreen);
	}
	
	@Test
	public void testOkayButtonNavigateToInitialScreen() {
		screen = new OkayPromptScreen(sc, "message", true, true);
		screen.getOkayButton().doClick();
		assertFalse(sStub.panelBack);
		assertTrue(sStub.initialScreen);
	}
	
	public class StationControlListenerStub implements StationControlListener{
		
		public boolean initialScreen = false;
		public boolean panelBack = false;

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
			panelBack = true;
			
		}

		@Override
		public void triggerInitialScreen(StationControl systemControl) {
			initialScreen = true;
			
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPLUCodeWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerBrowsingCatalog(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerReceiptScreen(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
