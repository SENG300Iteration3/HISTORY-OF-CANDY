package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.enums.PaymentType;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PaymentControlListener;
import com.diy.software.test.logic.StubSystem;

import ca.powerutility.PowerGrid;
import swing.screens.PaymentScreen;

public class PaymentScreenTest {
	
	FakeDataInitializer fdi;
	StationControl sc;
	PaymentControl pc;
	PaymentScreen screen;
	PaymentControlListenerStub stub;
	StubSystem sStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		screen = new PaymentScreen(sc);
		
		stub = new PaymentControlListenerStub();
		sc.getPaymentControl().addListener(stub);
		
		sStub = new StubSystem();
		sc.register(sStub);
	}

	@After
	public void tearDown() throws Exception {
		fdi = null;
		sc = null;
		screen = null;
	}

	@Test
	public void testGiftCardButton() {
		screen.getGiftCardButton().doClick();
		assertEquals(PaymentType.GiftCard, stub.type);
	}
	
	@Test
	public void testCashButton() {
		screen.getCashButton().doClick();
		assertEquals(PaymentType.Cash, stub.type);
	}
	
	@Test
	public void testCreditButton() {
		screen.getCreditButton().doClick();
		assertEquals(PaymentType.Credit, stub.type);
	}
	
	@Test
	public void testDebitButton() {
		screen.getDebitButton().doClick();
		assertEquals(PaymentType.Debit, stub.type);
	}
	
	@Test
	public void testCancelButton() {
		screen.getCancelButton().doClick();
		assertTrue(sStub.triggerPanelBack);
	}
	
	public class PaymentControlListenerStub implements PaymentControlListener {

		public PaymentType type;
		
		@Override
		public void paymentMethodSelected(PaymentControl pc, PaymentType type) {
			this.type = type;
			
		}
	}

}
