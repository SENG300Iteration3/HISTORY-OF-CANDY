package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PinPadControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PinPadControlListener;
import com.diy.software.listeners.WalletControlListener;

import ca.powerutility.PowerGrid;

public class TestPinPadControl {
	
	StationControl sc;
	FakeDataInitializer fdi;
	PinPadControl ppc;
	PinPadListenerStub ppls;
	WalletListenerStub wls;
	String testpin = "";
	WalletControl wc;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();

		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		ppls = new PinPadListenerStub();
		wls = new WalletListenerStub();
		ppc = new PinPadControl(sc);
		wc = sc.getWalletControl();
		
	}
	
	@Test
	public void addListener() {
		assertFalse(ppls.updated);
		
		ppc.addListener(ppls);
		
		assertFalse(ppls.updated);
	}
	
	@Test
	public void removeListener() {
		assertFalse(ppls.updated);
		
		ppc.removeListener(ppls);
		
		assertFalse(ppls.updated);
	}
	
	
	@Test
	public void testExitPinPad() {
		wc.addListener(wls);
		
		assertFalse(wls.cardPayments);
		ppc.exitPinPad();
		assertTrue(wls.cardPayments);
	}
	
	@Test 
	public void testActionPerformedPinInput() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "PIN_INPUT_BUTTON: 1234");
		assertFalse(ppls.updated);
		assertFalse(ppls.stubpin.equals("1234"));
		
		ppc.actionPerformed(e);
		
		assertTrue(ppls.updated);
		assertTrue(ppls.stubpin.equals("1234"));
	}
	
	@Test (expected = ArrayIndexOutOfBoundsException.class)
	public void testActionPerformedPinInputMissing() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "PIN_INPUT_BUTTON: ");
		ppc.actionPerformed(e);
	}
	
	@Test
	public void testActionPerformedPinInputNonNumber() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "PIN_INPUT_BUTTON: Hello");
		assertFalse(ppls.updated);
		assertFalse(ppls.stubpin.equals("Hello"));
		
		ppc.actionPerformed(e);
		
		assertTrue(ppls.updated);
		assertTrue(ppls.stubpin.equals("Hello"));
	}
	
	
	@Test
	public void testActionPerformedCancel() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "cancel");
		wc.addListener(wls);
		assertFalse(wls.cardPayments);
		
		ppc.actionPerformed(e);
		
		assertTrue(wls.cardPayments);
	}
	
	@Test
	public void testActionPerformedCorrectNoPin() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "correct");
	
		assertFalse(ppls.updated);
		assertTrue(ppls.stubpin.equals(""));
		
		ppc.actionPerformed(e);
		
		assertTrue(ppls.updated);
		assertTrue(ppls.stubpin.equals(""));
	}
	
	@Test 
	public void testActionPerformedCorrectWithPin() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "PIN_INPUT_BUTTON: 1");
		
		assertFalse(ppls.updated);
		assertTrue(ppls.stubpin.equals(""));
		
		ppc.actionPerformed(e);
		
		assertTrue(ppls.stubpin.equals("1"));
		assertTrue(ppls.updated);
		ppls.updated = false;
		
		e = new ActionEvent(this, 0, "correct");
		
		ppc.actionPerformed(e);
		
		assertTrue(ppls.updated);
		assertTrue(ppls.stubpin.equals(""));
		
	}
	
	@Test
	public void testActionPerformedSubmit() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this, 0, "submit");
		assertFalse(ppls.updated);
		ppc.actionPerformed(e);
		assertFalse(ppls.updated);
	}
	
	
	@Test
	public void testActionPerformedDefault() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this,0,"");
		
		assertFalse(ppls.updated);
		
		ppc.actionPerformed(e);
		
		assertFalse(ppls.updated);
	}
	
	
	@Test (expected = NullPointerException.class)
	public void testActionPerformedNullEvent() {
		ppc.addListener(ppls);
		ActionEvent e = null;
		
		ppc.actionPerformed(e);
	}
	
	
	@Test (expected = NullPointerException.class)
	public void testActionPerformedEventNameNull() {
		ppc.addListener(ppls);
		ActionEvent e = new ActionEvent(this,0,null);
		
		ppc.actionPerformed(e);
	}
	
	
	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}
	
	
	
	
	
	
	
	
	
	
	public class WalletListenerStub implements WalletControlListener {
		boolean cardPayments = false;
		
		
		@Override
		public void cardHasBeenSelected(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardPaymentsEnabled(WalletControl wc) {
			// TODO Auto-generated method stub
			cardPayments = true;
			
		}

		@Override
		public void cardPaymentsDisabled(WalletControl wc) {
			cardPayments = false;
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
		public void membershipCardHasBeenSelected(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputEnabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputCanceled(WalletControl walletControl) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
	
	
	public class PinPadListenerStub implements PinPadControlListener{
		String stubpin = "";
		boolean updated = false;
		
		@Override
		public void pinHasBeenUpdated(PinPadControl ppc, String pin) {
			stubpin = pin;
			updated = true;
			
		}
		
		
	}
	
	
}
