package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.BagsControlListener;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.necchi.BarcodedItem;

import ca.powerutility.PowerGrid;

public class TestBagsControl {
	BagsControl bc;
	StationControl sc;
	BagsListenerStub bls;
	FakeDataInitializer fdi;
	ItemsControl ic;
	Item item1;
	Item item2;
	Item item3;
	Item item4;
	Item[] items;
	
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		items = fdi.getItems();
		item1 = items[0];
		item2 = items[1];
		item3 = items[2];
		item4 = items[3];
		
		sc = new StationControl(fdi);
		ic = sc.getItemsControl();
		bc = new BagsControl(sc);
		bls = new BagsListenerStub();
	}
	
	@Test
	public void testAddListener() {
		bls.attendantVerifyBags = false;
		bc.ownBagsPlacedInBaggingArea();
		assertFalse(bls.attendantVerifyBags);
		
		bc.addListener(bls);
		
		bls.attendantVerifyBags = false;
		bc.ownBagsPlacedInBaggingArea();
		assertTrue(bls.attendantVerifyBags);
	}
	
	@Test
	public void testRemoveListener() {
		bc.addListener(bls);
		
		bls.attendantVerifyBags = false;
		bc.ownBagsPlacedInBaggingArea();
		assertTrue(bls.attendantVerifyBags);
		
		bc.removeListener(bls);
		
		bls.attendantVerifyBags = false;
		bc.ownBagsPlacedInBaggingArea();
		assertFalse(bls.attendantVerifyBags);
	}
	
	
	@Test
	public void testPlaceBagsInBaggingArea() {
		sc.unblockStation();
		bc.addListener(bls);
		double lastItemWeight = sc.getWeightOfLastItemAddedToBaggingArea();
		double lastExpectedWeight = sc.getExpectedWeight();
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.customerAwaitingBags);
		
		bc.placeBagsInBaggingArea();
		
		assertTrue(lastExpectedWeight != sc.getExpectedWeight());
		assertTrue(lastItemWeight != sc.getWeightOfLastItemAddedToBaggingArea());
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertTrue(bls.customerAwaitingBags);
	}
	
	@Test
	public void testOwnBagsPlacedIngBaggingArea() {
		sc.unblockStation();
		bc.addListener(bls);
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		
		bc.ownBagsPlacedInBaggingArea();
		
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertTrue(bls.attendantVerifyBags);
	}
	
	@Test
	public void testPlacePurchasedBagsInBaggingArea() {
		bc.addListener(bls);
		double lastItemWeight = sc.getWeightOfLastItemAddedToBaggingArea();
		double lastExpectedWeight = sc.getExpectedWeight();
		double lastCheckoutListTotal = ic.getCheckoutTotal();
		
		bc.placeBagsInBaggingArea();
		
		assertFalse(lastItemWeight == sc.getWeightOfLastItemAddedToBaggingArea());
		assertFalse(lastExpectedWeight == sc.getExpectedWeight());
		assertFalse(lastCheckoutListTotal == ic.getCheckoutTotal());
	}
	
	@Test
	public void testActionPerformedAddBags() {
		sc.unblockStation();
		bc.addListener(bls);
		ActionEvent e =  new ActionEvent(this,0,"add bags");
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.customerAwaitingBags);
		
		bc.actionPerformed(e);
		
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertTrue(bls.customerAwaitingBags);
	}
	
	@Test
	public void testActionPerformedDoneAddBags() {
		sc.unblockStation();
		bc.addListener(bls);
		ActionEvent e =  new ActionEvent(this,0,"done adding bags");
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		
		bc.actionPerformed(e);
		
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertTrue(bls.attendantVerifyBags);
	}
	
	@Test
	public void testActionPerformedPurchaseBags() {
		bc.addListener(bls);
		ActionEvent e =  new ActionEvent(this,0,"purchase bags");
		double lastItemWeight = sc.getWeightOfLastItemAddedToBaggingArea();
		double lastExpectedWeight = sc.getExpectedWeight();
		double lastCheckoutListTotal = ic.getCheckoutTotal();
		
		bc.actionPerformed(e);
		
		assertFalse(lastItemWeight == sc.getWeightOfLastItemAddedToBaggingArea());
		assertFalse(lastExpectedWeight == sc.getExpectedWeight());
		assertFalse(lastCheckoutListTotal == ic.getCheckoutTotal());
	}
	
	
	@Test (expected = NullPointerException.class)
	public void testActionPerformedNullEvent() {
		sc.unblockStation();
		bc.addListener(bls);
		ActionEvent e =  null;
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		assertFalse(bls.customerAwaitingBags);
		
		bc.actionPerformed(e);
	}
	
	
	@Test 
	public void testActionPerformedDefault() {
		sc.unblockStation();
		bc.addListener(bls);
		ActionEvent e =  new ActionEvent(this,0,"");
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		assertFalse(bls.customerAwaitingBags);
		
		bc.actionPerformed(e);
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		assertFalse(bls.customerAwaitingBags);
	}
	
	
	@Test
	public void testActionPerformedEventNameNull() {
		sc.unblockStation();
		bc.addListener(bls);
		ActionEvent e =  new ActionEvent(this,0,null);
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		assertFalse(bls.customerAwaitingBags);
		
		bc.actionPerformed(e);
		
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(bls.attendantVerifyBags);
		assertFalse(bls.customerAwaitingBags);
	}
	
	
	@After 
	public void teardown() {
		PowerGrid.reconnectToMains();
	}
	
	
	public class BagsListenerStub implements BagsControlListener {
		boolean customerAwaitingBags = false;
		boolean attendantVerifyBags = false;
		
		
		@Override
		public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
			customerAwaitingBags = true;
			
		}

		@Override
		public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
			attendantVerifyBags = true;
			
		}

		@Override
		public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
			// No method ever calls this... 
		
		}
		
		
	}
		
	
	
	
	
}
