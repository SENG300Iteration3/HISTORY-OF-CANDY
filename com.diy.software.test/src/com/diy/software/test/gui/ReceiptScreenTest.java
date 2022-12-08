package com.diy.software.test.gui;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.test.logic.StubSystem;

import ca.powerutility.PowerGrid;
import swing.screens.ReceiptScreen;

public class ReceiptScreenTest {
	
	StationControl sc;
	ReceiptScreen screen;
	StubSystem sStub;
	FakeDataInitializer fdi;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addCardData();
		fdi.addFakeAttendantLogin();
		fdi.addFakeMembers();
		fdi.addPLUCodedProduct();
		fdi.addProductAndBarcodeData();
		
		sc = new StationControl(fdi);
		screen = new ReceiptScreen(sc);
		
		sStub = new StubSystem();
		sc.register(sStub);
		
	}

	@After
	public void tearDown() throws Exception {
		sc = null;
		screen = null;
		sStub = null;
	}

	@Test
	public void testOkayButton() {
		screen.getOkayButton().doClick();
		assertTrue(sStub.initialScreen);
	}
	
	@Test
	public void testPrintRecieptItemsNoItems() {
		sc.getReceiptControl().printItems();
		assertEquals("", screen.receiptTextArea.getText());
	}
	
	@Test
	public void testPrintRecieptBalance() {
		sc.getReceiptControl().printTotalCost();
		assertEquals("Total: $0.0\n", screen.receiptTextArea.getText());
	}
	
	@Test
	public void testPrintMembershipNoMembers() {
		sc.getReceiptControl().printMembership();
		assertEquals("", screen.receiptTextArea.getText());
	}
	
	@Test
	public void testPrintDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");  
	    Date date = new Date();  
	    String expected = formatter.format(date) + "\n";
		sc.getReceiptControl().printDateTime();
		assertEquals(expected, screen.receiptTextArea.getText());
	}
	
	@Test
	public void testPrintThankyouMessageNotMembers() {
		sc.getReceiptControl().printThankyouMsg();
		assertEquals("Thank you for shopping with us!\n", screen.receiptTextArea.getText());
	}

}
