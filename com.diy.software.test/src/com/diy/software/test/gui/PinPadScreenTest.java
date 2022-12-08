package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PinPadControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PinPadControlListener;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

import ca.powerutility.PowerGrid;
import swing.screens.PinPadScreen;

public class PinPadScreenTest {

	PinPadScreen screen;
	StationControl sc;
	FakeDataInitializer fdi;
	PinPadControlListenerStub pStub;
	StationControlListenerStub sStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();

		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		screen = new PinPadScreen(sc);

		pStub = new PinPadControlListenerStub();
		sc.getPinPadControl().addListener(pStub);

		sStub = new StationControlListenerStub();
		sc.register(sStub);
	}

	@After
	public void tearDown() throws Exception {
		screen = null;
		sc = null;
		fdi = null;
	}

	@Test
	public void testPinpadButton() {
		screen.getPinpadButtons()[0].doClick();
		assertEquals("1", pStub.pin);
	}

	@Test
	public void testCancelButton() {
		screen.getCancelButton().doClick();
		assertTrue(sStub.panelTriggeredBack);
	}

	@Test
	public void testCorrectButton() {
		screen.getPinpadButtons()[0].doClick();
		screen.getCorrectButton().doClick();
		assertEquals("", pStub.pin);
	}

	@Test
	public void testCorrectButtonNothingInputted() {
		screen.getCorrectButton().doClick();
		assertEquals("", pStub.pin);
	}

	//TODO: Submit button tests

	public class PinPadControlListenerStub implements PinPadControlListener{

		String pin;

		@Override
		public void pinHasBeenUpdated(PinPadControl ppc, String pin) {
			this.pin = pin;

		}

	}

public class StationControlListenerStub implements StationControlListener{

		public boolean panelTriggeredBack = false;

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