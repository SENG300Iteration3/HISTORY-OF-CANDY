package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.util.Tuple;
import com.diy.hardware.PLUCodedItem;
import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.svenden.ReusableBag;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;

public class ItemsControlTest {
	ItemsControl itemsControl;
	StationControl sc;
	StubItemsControl stub;
	BarcodedItem item;
	PLUCodedItem pluItem;
	Barcode barcode;
	FakeDataInitializer fdi;
	Tuple<String, Double> itemTuple;

	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();

		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		fdi.addPLUCodedProduct();
		sc = new StationControl();
		itemsControl = new ItemsControl(sc);
		stub = new StubItemsControl();
		itemsControl.addListener(stub);
		itemTuple = new Tuple<String, Double>("Can of Beans", (double) 2);
		barcode = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four });
		item = (BarcodedItem) fdi.getItems()[0];
		pluItem = (PLUCodedItem) fdi.getItems()[2];
		sc.station.handheldScanner.register(itemsControl);
		sc.station.handheldScanner.plugIn();
		sc.station.handheldScanner.turnOff();
		sc.station.handheldScanner.turnOn();
		sc.station.handheldScanner.enable();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		sc.station.handheldScanner.disable();
		sc.station.handheldScanner.turnOff();
		sc.station.handheldScanner.unplug();
	}
	
	/**
	 * Test ensuring that the correct data is scraped from 
	 * checkoutList and stored in the ArrayList<Tuple<String, Double>>
	 * returned by getItemDescriptionList.
	 */
	@Test
	public void testGetItemDescriptionList() {
		StationControl sc = new StationControl(fdi);
		BagsControl bc = new BagsControl(sc);
		itemsControl = new ItemsControl(sc);
		itemsControl.addItemToCheckoutList(fdi.getBarcodes()[0]);	// Adding the Barcode for "Can of Beans"
		itemsControl.addItemToCheckoutList(fdi.getPLUCode()[2]);	// Adding the PLUCode for "Tomatoes"
		itemsControl.addReusableBags(new ReusableBag());
		ArrayList<Tuple<String, Double>> list = itemsControl.getItemDescriptionPriceList();
		Tuple<String, Double> output1 = list.get(0);
		Tuple<String, Double> output2 = list.get(1);
		Tuple<String, Double> output3 = list.get(2);
		assertTrue(list.size() == 3);
		assertTrue(output1.x == "Can of Beans"); 
		assertTrue(output2.x == "Tomatoes");
		assertTrue(output3.x == "Reusable Bag");
		assertTrue(output1.y == 2.0); 
		assertTrue(output2.y == 1.2);
		assertTrue(output3.y == 2.0);
		}

	@Test
	public void testRemoveListener() {
		stub.bagging = true;
		stub.selected = true;

		itemsControl.weightChanged(sc.station.baggingArea, 5);

		assertFalse(stub.bagging);
		assertFalse(stub.selected);

		itemsControl.removeListener(stub);

		stub.bagging = true;
		stub.selected = true;
		itemsControl.weightChanged(sc.station.baggingArea, 5);
		assertTrue(stub.bagging);
		assertTrue(stub.selected);
	}

	@Test
	public void testAddListener() {
		StubItemsControl stub2 = new StubItemsControl();

		stub2.bagging = true;
		stub2.selected = true;

		itemsControl.weightChanged(sc.station.baggingArea, 5);

		assertTrue(stub2.bagging);
		assertTrue(stub2.selected);

		itemsControl.addListener(stub2);

		stub.bagging = true;
		stub.selected = true;
		itemsControl.weightChanged(sc.station.baggingArea, 5);
		assertFalse(stub2.bagging);
		assertFalse(stub2.selected);
	}

	// FIXME: need to rewrite - Anh
	// @Test
	// public void testRequestNoBagging() {
	// AttendantListenerStub als = new AttendantListenerStub();
	// sc.getAttendantControl().addListener(als);
	//
	// als.noBagging = false;
	// itemsControl.requestNoBagging();
	// assertTrue(als.noBagging);
	// }

	@Test
	public void testPickupNextItemNull() {
		assertTrue(stub.available);
		itemsControl.pickupNextItem();
		assertFalse(stub.available);
	}

	@Test
	public void testPickupNextItemOneItem() {
		sc.customer.shoppingCart.add(item);

		assertFalse(sc.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		itemsControl.pickupNextItem();
		assertTrue(stub.selected);
		assertTrue(sc.customer.shoppingCart.isEmpty());
		assertFalse(stub.available);
	}

	@Test
	public void testPickupNextItemMoreItems() {
		sc.customer.shoppingCart.add(item);
		sc.customer.shoppingCart.add(item);

		assertFalse(sc.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		itemsControl.pickupNextItem();
		assertTrue(stub.selected);
		assertFalse(sc.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);

		sc.customer.shoppingCart.clear();
	}

	@Test
	public void testPutUnscannedItemBack() {
		sc.customer.shoppingCart.add(item);
		sc.customer.selectNextItem();
		stub.selected = true;
		stub.available = false;

		assertTrue(sc.customer.shoppingCart.isEmpty());
		itemsControl.putUnscannedItemBack();
		assertFalse(stub.selected);
		assertFalse(sc.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
	}

	@Test
	public void testPutUnscannedItemBackNoItem() {

		assertTrue(stub.available);
		assertTrue(sc.customer.shoppingCart.isEmpty());
		itemsControl.putUnscannedItemBack();
		assertFalse(stub.selected);
		assertTrue(sc.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
	}

//	@Test
//	public void testScanCurrentItem() {
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		assertFalse(stub.bagging);
//
//		while (!stub.bagging) {
//			itemsControl.scanCurrentItem(true);
//		}
//		assertTrue(stub.bagging);
//	}
//
//	@Test
//	public void testScanCurrentItemScanFail() {
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		sc.station.handheldScanner.disable();
//		stub.bagging = true;
//		while (stub.bagging) {
//			stub.bagging = false;
//			itemsControl.scanCurrentItem(true);
//		}
//		assertFalse(stub.bagging);
//	}
//
//	@Test
//	public void testPlaceItemOnScale() {
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		stub.bagging = true;
//		stub.selected = true;
//		itemsControl.placeItemOnBaggingArea();
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//	}
//
//	@Test
//	public void testPlaceItemOnScaleInTime() {
//
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		assertFalse(stub.bagging);
//
//		while (!stub.bagging) {
//			itemsControl.scanCurrentItem(true);
//		}
//		assertTrue(stub.bagging);
//		stub.bagging = true;
//		stub.selected = true;
//		itemsControl.placeItemOnBaggingArea();
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//	}

//	@Test
//	public void testPlaceItemOnScaletoolight() {
//		BarcodedItem lightItem = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one }), 0.01);
//		sc.customer.shoppingCart.add(lightItem);
//		sc.customer.selectNextItem();
//		stub.bagging = true;
//		stub.selected = true;
//		boolean loop = true;
//
//		while (loop) {
//			itemsControl.placeItemOnBaggingArea();
//			try {
//				if (sc.station.baggingArea.getCurrentWeight() >= 1.123
//						&& sc.station.baggingArea.getCurrentWeight() <= 1.123 + 0.1) {
//					itemsControl.removeLastBaggedItem();
//					stub.bagging = true;
//					stub.selected = true;
//				} else {
//					loop = false;
//				}
//			} catch (OverloadException e) {
//			}
//		}
//		assertTrue(stub.bagging);
//		assertTrue(stub.selected);
//
//	}

//	@Test
//	public void testPlaceItemWrongItem() {
//		BarcodedItem heavyItem = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one }), 100.0);
//		sc.customer.shoppingCart.add(heavyItem);
//		sc.customer.selectNextItem();
//
//		sc.updateExpectedCheckoutWeight(100.0);
//		stub.removeItem = false;
//		while (!stub.removeItem) {
//			itemsControl.placeItemOnBaggingArea();
//			if (!stub.removeItem) {
//				sc.station.baggingArea.remove(heavyItem);
//				sc.customer.shoppingCart.add(heavyItem);
//				sc.customer.selectNextItem();
//			}
//		}
//
//		assertFalse(sc.expectedWeightMatchesActualWeight(100));
//
//		try {
//			assertTrue(sc.station.baggingArea.getCurrentWeight() >= itemsControl.getWrongBaggedItem().getWeight());
//			assertTrue(
//					sc.station.baggingArea.getCurrentWeight() - 1 <= itemsControl.getWrongBaggedItem().getWeight());
//		} catch (OverloadException e) {
//		}
//
//	}

//	@Test
//	public void testPlaceItemRemoveLastItem() {
//		sc.customer.shoppingCart.add(itemsControl.getWrongBaggedItem());
//		sc.customer.selectNextItem();
//		itemsControl.placeItemOnBaggingArea();
//		try {
//			System.out.println(sc.station.baggingArea.getCurrentWeight());
//			assertTrue(sc.station.baggingArea.getCurrentWeight() >= itemsControl.getWrongBaggedItem().getWeight());
//		} catch (OverloadException e) {
//		}
//		stub.selected = true;
//		stub.bagging = true;
//
//		itemsControl.removeLastBaggedItem();
//		assertFalse(stub.selected);
//		assertFalse(stub.bagging);
//		try {
//			assertTrue(sc.station.baggingArea.getCurrentWeight() <= 1);
//		} catch (OverloadException e) {
//		}
//
//	}
//
//	@Test
//	public void testPlaceBulkyItemInCart() {
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		stub.bagging = true;
//		stub.selected = true;
//		assertTrue(sc.customer.bulkyItemStorage.isEmpty());
//		itemsControl.placeBulkyItemInCart();
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//		assertFalse(sc.customer.bulkyItemStorage.isEmpty());
//		sc.customer.bulkyItemStorage.clear();
//	}
//
//	@Test
//	public void testPlaceBulkyItemInCartNoItem() {
//
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//		assertTrue(sc.customer.bulkyItemStorage.isEmpty());
//		itemsControl.placeBulkyItemInCart();
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//		assertTrue(sc.customer.bulkyItemStorage.isEmpty());
//	}

	@Test
	public void testBarcodeScanned() {
		assertFalse(stub.bagging);
		itemsControl.barcodeScanned(sc.station.handheldScanner, null);
		assertTrue(stub.bagging);
	}

//	@Test
//	public void testWeightChanged() {
//		stub.bagging = true;
//		stub.selected = true;
//		itemsControl.weightChanged(sc.station.baggingArea, 1);
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//	}
//
//	@Test
//	public void testActionPerformedPickUp() {
//		ActionEvent e = new ActionEvent(this, 0, "pick up");
//
//		sc.customer.shoppingCart.add(item);
//
//		assertFalse(sc.customer.shoppingCart.isEmpty());
//		assertTrue(stub.available);
//		assertFalse(stub.selected);
//		itemsControl.actionPerformed(e);
//		assertTrue(stub.selected);
//		assertTrue(sc.customer.shoppingCart.isEmpty());
//		assertFalse(stub.available);
//
//	}
//
//	@Test
//	public void testActionPerformedScan() {
//		ActionEvent e = new ActionEvent(this, 0, "handheld scan");
//
//		sc.customer.shoppingCart.add(item);
//
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		assertFalse(stub.bagging);
//
//		while (!stub.bagging) {
//			itemsControl.actionPerformed(e);
//		}
//		assertTrue(stub.bagging);
//
//	}
//
//	@Test
//	public void testActionPerformedPutBack() {
//		ActionEvent e = new ActionEvent(this, 0, "put back");
//
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		stub.selected = true;
//		stub.available = false;
//
//		assertTrue(sc.customer.shoppingCart.isEmpty());
//		itemsControl.actionPerformed(e);
//		assertFalse(stub.selected);
//		assertFalse(sc.customer.shoppingCart.isEmpty());
//		assertTrue(stub.available);
//
//	}
//
//	@Test
//	public void testActionPerformedBag() {
//		ActionEvent e = new ActionEvent(this, 0, "bag");
//
//		sc.customer.shoppingCart.add(item);
//		sc.customer.selectNextItem();
//		stub.bagging = true;
//		stub.selected = true;
//		itemsControl.actionPerformed(e);
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//
//	}
//
//	@Test
//	public void testActionPerformedRemove() {
//		ActionEvent e = new ActionEvent(this, 0, "removeFromScale");
//		AttendantListenerStub als = new AttendantListenerStub();
//		sc.getAttendantControl().addListener(als);
//
//		als.noBagging = false;
//		itemsControl.actionPerformed(e);
//		assertTrue(als.noBagging);
//
//	}
//
//	@Test
//	public void testActionPerformedPay() {
//		ActionEvent e = new ActionEvent(this, 0, "pay");
//
//		sc.listeners.clear();
//		StubSystem scStub = new StubSystem();
//		sc.listeners.add(scStub);
//
//		scStub.triggerPaymentWorkflow = false;
//		itemsControl.actionPerformed(e);
//		assertTrue(scStub.triggerPaymentWorkflow);
//	}
//
//	@Test
//	public void testActionPerformedNoAction() {
//		ActionEvent e = new ActionEvent(this, 0, "");
//
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//		assertTrue(stub.available);
//		itemsControl.actionPerformed(e);
//		assertTrue(stub.available);
//		assertFalse(stub.bagging);
//		assertFalse(stub.selected);
//
//	}
//
//	@Test
//	public void testActionPerformedScanNoItem() {
//		ActionEvent e = new ActionEvent(this, 0, "scan");
//		sc.customer.shoppingCart.clear();
//
//		assertTrue(sc.customer.shoppingCart.isEmpty());
//		assertTrue(stub.available);
//		assertFalse(stub.selected);
//		assertFalse(stub.bagging);
//		itemsControl.actionPerformed(e);
//		assertTrue(sc.customer.shoppingCart.isEmpty());
//		assertTrue(stub.available);
//		assertFalse(stub.selected);
//		assertFalse(stub.bagging);
//	}

// TODO this is broken now
//	@Test
//	public void testAddItemToCheckoutList() {
//		Tuple<String, Double> output;
//
//		assertTrue(itemsControl.getCheckoutList().size() == 0);
//
//		assertFalse(stub.itemsHaveBeenUpdated);
//		assertFalse(stub.productSubtotalUpdated);
//		itemsControl.addItemToCheckoutList(itemTuple);
//		assertTrue(stub.itemsHaveBeenUpdated);
//		assertTrue(stub.productSubtotalUpdated);
//
//		output = itemsControl.getCheckoutList().get(0);
//		assertTrue(output.x.equals("Can of Beans"));
//		assertTrue(output.y == (double) 2);
//	}

	
// TODO FIX ME
//	@Test
//	public void testaddScannedItemToCheckoutList() {
//		Tuple<String, Double> output;
//
//		assertTrue(itemsControl.getCheckoutList().size() == 0);
//		assertFalse(itemsControl.getCheckoutTotal() == (double) 2);
//
//		assertFalse(stub.itemsHaveBeenUpdated);
//		assertFalse(stub.productSubtotalUpdated);
//		itemsControl.addScannedItemToCheckoutList(barcode);
//		assertTrue(stub.itemsHaveBeenUpdated);
//		assertTrue(stub.productSubtotalUpdated);
//
//		output = itemsControl.getCheckoutList().get(0);
//		assertTrue(output.x.equals("Can of Beans"));
//		assertTrue(output.y == (double) 2);
//		assertTrue(itemsControl.getCheckoutTotal() == (double) 2);
//
//	}

//	@Test
//	public void testaddScannedItemToCheckoutListNull() {
//
//		assertTrue(itemsControl.getCheckoutList().size() == 0);
//		assertFalse(itemsControl.getCheckoutTotal() == (double) 2);
//
//		assertFalse(stub.itemsHaveBeenUpdated);
//		assertFalse(stub.productSubtotalUpdated);
//		itemsControl.addScannedItemToCheckoutList(null);
//
//		assertTrue(itemsControl.getCheckoutList().size() == 0);
//		assertFalse(itemsControl.getCheckoutTotal() == (double) 2);
//
//		assertFalse(stub.itemsHaveBeenUpdated);
//		assertFalse(stub.productSubtotalUpdated);
//
//	}

	@Test
	public void testUpdateCheckoutTotal() {

		assertTrue(itemsControl.getCheckoutTotal() == (double) 0);

		assertFalse(stub.itemsHaveBeenUpdated);
		assertFalse(stub.productSubtotalUpdated);
		itemsControl.updateCheckoutTotal(25);
		assertTrue(stub.itemsHaveBeenUpdated);
		assertTrue(stub.productSubtotalUpdated);

		assertTrue(itemsControl.getCheckoutTotal() == (double) 25);

	}

	@Test
	public void testOverload() {
		itemsControl.overload(sc.station.baggingArea);
		assertTrue(itemsControl.userMessage.equals("Weight on scale has been overloaded, weight limit is: 5000.0"));
	}

	@Test
	public void testOutOfOverload() {
		itemsControl.outOfOverload(sc.station.baggingArea);
		assertTrue(itemsControl.userMessage.equals("Excessive weight removed, continue scanning"));
	}

	public class StubItemsControl implements ItemsControlListener {
		boolean available = true;
		boolean selected = false;
		boolean bagging = false;
		public boolean itemsHaveBeenUpdated = false;
		public boolean productSubtotalUpdated = false;
		public boolean removeItem = false;

		@Override
		public void awaitingItemToBeSelected(ItemsControl ic) {
			selected = false;
			bagging = false;

		}

		@Override
		public void itemWasSelected(ItemsControl ic) {
			selected = true;

		}

		@Override
		public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
			bagging = true;

		}

		@Override
		public void noMoreItemsAvailableInCart(ItemsControl ic) {
			available = false;

		}

		@Override
		public void itemsAreAvailableInCart(ItemsControl ic) {
			available = true;

		}

		@Override
		public void itemsHaveBeenUpdated(ItemsControl ic) {
			itemsHaveBeenUpdated = true;
		}

		@Override
		public void productSubtotalUpdated(ItemsControl ic) {
			productSubtotalUpdated = true;
		}

		@Override
		public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {
			removeItem = true;

		}

	}

	public class AttendantListenerStub implements AttendantControlListener {
		boolean attendantBags = false;
		boolean lowState = false;
		boolean addPaper = false;
		boolean addInk = false;
		public boolean noBagging = false;

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			attendantBags = true;
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			// TODO Auto-generated method stub
		}

		public boolean getAttendantBags() {
			return attendantBags;
		}

		@Override
		public void addPaperState() {
			addPaper = true;

		}

		@Override
		public void addInkState() {
			addInk = true;

		}

		@Override
		public void printerNotLowState() {
			lowState = true;

		}

		@Override
		public void signalWeightDescrepancy(String updateMessage) {
			// TODO Auto-generated method stub

		}

		@Override
		public void initialState() {
			// TODO Auto-generated method stub

		}

		@Override
		public void noBagRequest() {
			noBagging = true;
		}

		@Override
		public void lowInk(AttendantControl ac, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void lowPaper(AttendantControl ac, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void outOfInk(AttendantControl ac, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void outOfPaper(AttendantControl ac, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinIsLowState(CoinStorageUnit unit, int amount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void loggedIn(boolean isLoggedIn) {
			// TODO Auto-generated method stub

		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemBagged() {
			// TODO Auto-generated method stub
			
		}
	}
}
