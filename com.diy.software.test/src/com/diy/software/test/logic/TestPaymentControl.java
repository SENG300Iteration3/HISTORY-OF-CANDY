package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.enums.PaymentType;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PaymentControlListener;

import ca.powerutility.PowerGrid;

public class TestPaymentControl {

	StationControl sc;
	PaymentControl pc;
	FakeDataInitializer fdi;
	PaymentListenerStub pls;
	

	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		pls = new PaymentListenerStub();
		pc = new PaymentControl(sc);
		
	}
	
	
	@Test
	public void testActionPerformedCash() {
		ActionEvent e = new ActionEvent(this, 0, "cash");
		pc.addListener(pls);
		assertFalse(pls.pt == PaymentType.Cash);
		pc.actionPerformed(e);
		assertTrue(pls.pt == PaymentType.Cash);
	}
	
	@Test
	public void testAddListener() {
		assertFalse(pls.flag);
		
		pc.addListener(pls);
		
		assertFalse(pls.flag);
	}
	
	@Test
	public void testRemoveListener() {
		assertFalse(pls.flag);
		
		pc.removeListener(pls);
		
		assertFalse(pls.flag);
	}
	
	
	@Test
	public void testActionPerformedCredit() {
		ActionEvent e = new ActionEvent(this, 0, "credit");
		pc.addListener(pls);
		assertFalse(pls.pt == PaymentType.Credit);
		pc.actionPerformed(e);
		assertTrue(pls.pt == PaymentType.Credit);
	}
	
	
	@Test
	public void testActionPerformedDebit() {
		ActionEvent e = new ActionEvent(this, 0, "debit");
		pc.addListener(pls);	
		assertFalse(pls.pt == PaymentType.Debit);
		pc.actionPerformed(e);
		assertTrue(pls.pt == PaymentType.Debit);
	}
	
	
	@Test
	public void testActionPerformedDefault() {
		ActionEvent e = new ActionEvent(this, 0, "");
		pc.addListener(pls);	
		assertFalse(pls.pt == PaymentType.Debit);
		assertFalse(pls.pt == PaymentType.Credit);
		assertFalse(pls.pt == PaymentType.Cash);
		pc.actionPerformed(e);
		assertFalse(pls.pt == PaymentType.Debit);
		assertFalse(pls.pt == PaymentType.Credit);
		assertFalse(pls.pt == PaymentType.Cash);
	}
	
	@Test (expected = NullPointerException.class)
	public void testActionPerformedNullAction() {
		ActionEvent e = null;
		pc.actionPerformed(e);
	}
	
	
	@Test (expected = NullPointerException.class)
	public void testActionPerformedActionNameNull() {
		ActionEvent e = new ActionEvent(this, 0, null);
		pc.actionPerformed(e);
	}
	
	
	
	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}
	
	public class PaymentListenerStub implements PaymentControlListener  {
		PaymentType pt;
		PaymentControl pcc;
		boolean flag = false;
		
		@Override
		public void paymentMethodSelected(PaymentControl pc, PaymentType type) {
			pt = type;
			
		}
		
	}
	
	
	
	
	
}
