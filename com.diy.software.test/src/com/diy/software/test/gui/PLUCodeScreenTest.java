package com.diy.software.test.gui;

import static org.junit.Assert.*;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PLUCodeControl;
import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PLUCodeControlListener;
import com.diy.software.test.logic.StubSystem;

import ca.powerutility.PowerGrid;
import swing.screens.PLUCodeScreen;

public class PLUCodeScreenTest {
	FakeDataInitializer fdi;
	StationControl sc;
	PaymentControl pc;
	PLUCodeScreen screen;
	PLUCodeControlListenerStub stub;
	StubSystem sStub;
	JButton[] buttons;
	JButton cancelButton;
	JButton correctButton;
	JButton submitButton;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		screen = new PLUCodeScreen(sc);
		
		stub = new PLUCodeControlListenerStub();
		sc.getPLUCodeControl().addListener(stub);
		
		sStub = new StubSystem();
		sc.register(sStub);
		
		buttons = screen.getPluCodeButtons();
		submitButton = screen.getSubmitButton(); 
		cancelButton = screen.getCancelButton();
		correctButton = screen.getCorrectButton();
	}

	@After
	public void tearDown() throws Exception {
		PowerGrid.reconnectToMains();
	}
	
	@Test
	public void testCancelButton() {
		cancelButton.doClick();
		assertTrue(sStub.triggerPanelBack);
	}
	
	@Test
	public void testCorrectButton() {
		buttons[0].doClick();
		correctButton.doClick();
		assertEquals("1", stub.pluHasBeenUpdated);
	}
	
	@Test
	public void testCorrectButtonNothingInputted() {
		correctButton.doClick();
		assertEquals("", stub.pluHasBeenUpdated);
	}
	
	@Test
	public void testSubmitButtonOneArg() {
		buttons[0].doClick();
		submitButton.doClick();
		assertEquals("The code cannot contain less than four digits.", stub.pluErrorMessage);
	}
	
	@Test
	public void testSubmitButtonSixArgs() {
		buttons[0].doClick();
		buttons[0].doClick();
		buttons[0].doClick();
		buttons[2].doClick();
		buttons[0].doClick();
		buttons[0].doClick();
		submitButton.doClick();
		assertEquals("The code cannot contain more than five digits.", stub.pluErrorMessage);
	}
	
	@Test
	public void testSubmitButtonCorrectArgs() {
		buttons[0].doClick();
		buttons[8].doClick();
		buttons[0].doClick();
		buttons[0].doClick();
		buttons[0].doClick();
		submitButton.doClick();
		assertEquals("19111", stub.pluCodeEntered);
	}
	
	public class PLUCodeControlListenerStub implements PLUCodeControlListener{

		String pluHasBeenUpdated = "";
		String pluCodeEntered = "";
		String pluErrorMessage = "";

		@Override
		public void pluHasBeenUpdated(PLUCodeControl pcc, String pluCode) {
			pluHasBeenUpdated += pluCode;
			
		}

		@Override
		public void pluCodeEntered(PLUCodeControl pcc, String pluCode) {
			pluCodeEntered = pluCode;
			
		}

		@Override
		public void pluErrorMessageUpdated(PLUCodeControl pcc, String errorMessage) {
			pluErrorMessage = errorMessage;
			
		}
		
	}

}
