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
import com.jimmyselectronics.opeechee.Card.CardData;

import ca.powerutility.PowerGrid;
import swing.screens.NotEnoughBagsScreen;

public class NotEnoughBagsScreenTest {
	
	FakeDataInitializer fdi;
	NotEnoughBagsScreen screen;
	StationControl sc;
	StationControlListenerStub sStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		fdi =  new FakeDataInitializer();
		sStub = new StationControlListenerStub();
		sc = new StationControl(fdi);
		sc.register(sStub);
		screen = new NotEnoughBagsScreen(sc, 0);
	}

	@After
	public void tearDown() throws Exception {
		screen = null;
		sc = null;
		sStub = null;
	}

	@Test
	public void testAddBagsButton() {
		screen.getAddBagsButton().doClick();
		assertEquals(sc.getBagInStock(), sc.getBagDispenserControl().getNumBag());
	}
	
	@Test
	public void testAskAttendantButton() {
		screen.getAskAttendantButton().doClick();
		assertTrue(sStub.noBagsInStock);
	}
	
	public class StationControlListenerStub implements StationControlListener{
		
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
			// TODO Auto-generated method stub
			
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
			// TODO Auto-generated method stub
			
		}
		
	}

}
