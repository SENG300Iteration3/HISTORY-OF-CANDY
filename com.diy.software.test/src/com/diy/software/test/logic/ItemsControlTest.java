package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.util.Tuple;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedItem;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.svenden.ReusableBag;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

public class ItemsControlTest {
	ItemsControl itemsControl;
	StationControl systemControl;
	StubItemsControl stub;
	Item item;
	Barcode barcode;
	FakeDataInitializer fdi;
	Tuple<String, Double> itemTuple;

	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();

		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		systemControl = new StationControl();
		itemsControl = new ItemsControl(systemControl);
		stub = new StubItemsControl();
		itemsControl.addListener(stub);
		item = fdi.getItems()[0];
		itemTuple = new Tuple<String, Double>("Can of Beans", (double) 2);
		barcode = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four });

		systemControl.station.handheldScanner.register(itemsControl);
		systemControl.station.handheldScanner.plugIn();
		systemControl.station.handheldScanner.turnOff();
		systemControl.station.handheldScanner.turnOn();
		systemControl.station.handheldScanner.enable();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		systemControl.station.handheldScanner.disable();
		systemControl.station.handheldScanner.turnOff();
		systemControl.station.handheldScanner.unplug();
	}

	
	/**
	 * Test ensuring that the correct data is scraped from 
	 * checkoutList and stored in the ArrayList<Tuple<String, Double>>
	 * returned by getItemDescriptionList.
	 */
	@Test
	public void testGetItemDescriptionList() {
		StationControl sc = new StationControl(fdi);
		itemsControl = sc.getItemsControl();
		itemsControl.addItemToCheckoutList(fdi.getBarcodes()[0]);	// Adding the Barcode for "Can of Beans"
		itemsControl.addItemToCheckoutList(fdi.getPLUCode()[2]);	// Adding the PLUCode for "Tomatoes"
		System.out.println(sc.pluCodedItems.size());
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
		assertTrue(output2.y == 2.868);
		assertTrue(output3.y == 2.0);
		}
	
	/**
	 * Test ensuring that no data is created when no items 
	 * and no bags have been checked out.
	 */
	@Test
	public void testGetEmptyItemDescriptionList() {
		StationControl sc = new StationControl(fdi);
		BagsControl bc = new BagsControl(sc);
		itemsControl = new ItemsControl(sc);
		ArrayList<Tuple<String, Double>> list = itemsControl.getItemDescriptionPriceList();
		assertTrue(list.size() == 0);
		}
	
	/**
	 * Test ensuring that all the data store in ItemsControl and 
	 * StationControl is removed when an "item" is
	 * removed from the customers "checked out items"
	 */
	@Test
	public void testRemoveBarcodedItem() {
		StationControl sc = new StationControl(fdi);
		itemsControl = sc.getItemsControl();		
		BarcodedItem item = fdi.getBarcodedItems()[0];    		// item is a "Can of Beans"
		BarcodedProduct product = fdi.getBarcodedProducts()[0];
		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertTrue(itemsControl.getCheckoutTotal() == 0);
		assertTrue(sc.getExpectedWeight() == 0);
		while(!sc.station.mainScanner.scan(item));	
		sc.station.baggingArea.add(item);
		assertTrue(itemsControl.getCheckoutList().size() == 1);
		assertTrue(itemsControl.getCheckoutTotal() == product.getPrice());
		assertTrue(sc.getExpectedWeight() == item.getWeight());
		itemsControl.removeItem(1); // Remove the only item that has been scanned
		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertTrue(itemsControl.getCheckoutTotal() == 0);
		assertTrue(sc.getExpectedWeight() == 0);
	}


	/**
	 * Test ensuring that all the data store in ItemsControl and 
	 * StationControl is removed when an "item" is
	 * removed from the customers "checked out items"
	 */
	@Test
	public void testRemovePLUCodedItem() {
		StationControl sc = new StationControl(fdi);
		itemsControl = sc.getItemsControl();		
		PLUCodedItem item = fdi.getPLUItem()[2];    		// item is a "Can of Beans"
		PLUCodedProduct product = fdi.getPLUProducts()[2];
		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertTrue(itemsControl.getCheckoutTotal() == 0);
		assertTrue(sc.getExpectedWeight() == 0);
		itemsControl.setCurrentProductCode(fdi.getPLUCodes()[2]);
		sc.station.scanningArea.add(item);
		sc.station.baggingArea.add(item);
		assertTrue(itemsControl.getCheckoutList().size() == 1);
		assertTrue(itemsControl.getCheckoutTotal() == product.getPrice() * item.getWeight() / 1000);
		assertTrue(sc.getExpectedWeight() == item.getWeight());
		itemsControl.removeItem(1); // Remove the only item that has been scanned
		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertTrue(itemsControl.getCheckoutTotal() == 0);
		assertTrue(sc.getExpectedWeight() == 0);
	}
	
	
	
	/**
	 * Should return false when the checkoutList and
	 * bags list is empty.
	 */
	@Test
	public void testRemoveItemEmptyCheckoutList() {
		assertFalse(this.systemControl.getItemsControl().removeItem(0));
	}
	
	/**
	 * Should return false when the checkoutList and
	 * bags list is empty.
	 */
	@Test
	public void testRemoveItemNegativeIndex() {
		assertFalse(this.systemControl.getItemsControl().removeItem(-7943));
	}
	
	/**
	 * Should return false when the checkoutList and
	 * bags list is empty.
	 */
	@Test
	public void testRemoveItemLargeIndex() {
		assertFalse(this.systemControl.getItemsControl().removeItem(2000));
	}
	
	/**
	 * Test ensuring that all the data store in ItemsControl and 
	 * StationControl is removed when an "item" is
	 * removed from the customers "checked out items"
	 */
	@Test
	public void testRemoveBagFromCheckoutList() {
		StationControl sc = new StationControl(fdi);
		BagsControl bc = new BagsControl(sc);
		itemsControl = new ItemsControl(sc);
		assertTrue(itemsControl.getBagsList().size() == 0);
		assertTrue(itemsControl.getCheckoutTotal() == 0);
		assertTrue(sc.getExpectedWeight() == 0);
		ReusableBag bag = new ReusableBag();
		itemsControl.addReusableBags(bag);
		sc.updateExpectedCheckoutWeight(bag.getWeight());
		sc.station.baggingArea.add(bag);
		assertTrue(itemsControl.getBagsList().size() == 1);
		assertTrue(itemsControl.getCheckoutTotal() == 2);
		itemsControl.removeItem(1); // Remove the only item that has been scanned
		assertTrue(itemsControl.getBagsList().size() == 0);
		assertTrue(itemsControl.getCheckoutTotal() == 0);
	}
	
	
	@Test
	public void testRemoveListener() {
		stub.bagging = true;
		stub.selected = true;

		itemsControl.weightChanged(systemControl.station.baggingArea, 5);

		assertFalse(stub.bagging);
		assertFalse(stub.selected);

		itemsControl.removeListener(stub);

		stub.bagging = true;
		stub.selected = true;
		itemsControl.weightChanged(systemControl.station.baggingArea, 5);
		assertTrue(stub.bagging);
		assertTrue(stub.selected);
	}

	@Test
	public void testAddListener() {
		StubItemsControl stub2 = new StubItemsControl();

		stub2.bagging = true;
		stub2.selected = true;

		itemsControl.weightChanged(systemControl.station.baggingArea, 5);

		assertTrue(stub2.bagging);
		assertTrue(stub2.selected);

		itemsControl.addListener(stub2);

		stub.bagging = true;
		stub.selected = true;
		itemsControl.weightChanged(systemControl.station.baggingArea, 5);
		assertFalse(stub2.bagging);
		assertFalse(stub2.selected);
	}

	// FIXME: need to rewrite - Anh
	// @Test
	// public void testRequestNoBagging() {
	// AttendantListenerStub als = new AttendantListenerStub();
	// systemControl.getAttendantControl().addListener(als);
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
		systemControl.customer.shoppingCart.add(item);

		assertFalse(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		itemsControl.pickupNextItem();
		assertTrue(stub.selected);
		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		assertFalse(stub.available);
	}

	@Test
	public void testPickupNextItemMoreItems() {
		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.shoppingCart.add(item);

		assertFalse(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		itemsControl.pickupNextItem();
		assertTrue(stub.selected);
		assertFalse(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);

		systemControl.customer.shoppingCart.clear();
	}

	@Test
	public void testPutUnscannedItemBack() {
		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.selectNextItem();
		stub.selected = true;
		stub.available = false;

		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		itemsControl.putUnscannedItemBack();
		assertFalse(stub.selected);
		assertFalse(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
	}

	@Test
	public void testPutUnscannedItemBackNoItem() {

		assertTrue(stub.available);
		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		itemsControl.putUnscannedItemBack();
		assertFalse(stub.selected);
		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
	}

//	@Test
//	public void testScanCurrentItem() {
//		systemControl.customer.shoppingCart.add(item);
//		systemControl.customer.selectNextItem();
//		assertFalse(stub.bagging);
//
//		while (!stub.bagging) {
//			itemsControl.scanCurrentItem(true);
//		}
//		assertTrue(stub.bagging);
//	}

	@Test
	public void testScanCurrentItemScanFail() {
		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.selectNextItem();
		systemControl.station.handheldScanner.disable();
		stub.bagging = true;
		while (stub.bagging) {
			stub.bagging = false;
			itemsControl.scanCurrentItem(true);
		}
		assertFalse(stub.bagging);
	}

	@Test
	public void testPlaceItemOnScale() {
		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.selectNextItem();
		stub.bagging = true;
		stub.selected = true;
		itemsControl.placeItemOnBaggingArea();
		assertFalse(stub.bagging);
		assertFalse(stub.selected);
	}

//	@Test
//	public void testPlaceItemOnScaleInTime() {
//
//		systemControl.customer.shoppingCart.add(item);
//		systemControl.customer.selectNextItem();
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

	@Test
	public void testPlaceItemOnScaletoolight() {
		BarcodedItem lightItem = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one }), 0.01);
		systemControl.customer.shoppingCart.add(lightItem);
		systemControl.customer.selectNextItem();
		stub.bagging = true;
		stub.selected = true;
		boolean loop = true;

		while (loop) {
			itemsControl.placeItemOnBaggingArea();
			try {
				if (systemControl.station.baggingArea.getCurrentWeight() >= 1.123
						&& systemControl.station.baggingArea.getCurrentWeight() <= 1.123 + 0.1) {
					itemsControl.removeLastBaggedItem();
					stub.bagging = true;
					stub.selected = true;
				} else {
					loop = false;
				}
			} catch (OverloadException e) {
			}
		}
		assertTrue(stub.bagging);
		assertTrue(stub.selected);

	}

//	@Test
//	public void testPlaceItemWrongItem() {
//		BarcodedItem heavyItem = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one }), 100.0);
//		systemControl.customer.shoppingCart.add(heavyItem);
//		systemControl.customer.selectNextItem();
//
//		systemControl.updateExpectedCheckoutWeight(100.0);
//		stub.removeItem = false;
//		while (!stub.removeItem) {
//			itemsControl.placeItemOnBaggingArea();
//			if (!stub.removeItem) {
//				systemControl.station.baggingArea.remove(heavyItem);
//				systemControl.customer.shoppingCart.add(heavyItem);
//				systemControl.customer.selectNextItem();
//			}
//		}
//
//		assertFalse(systemControl.expectedWeightMatchesActualWeight(100));
//
//		try {
//			assertTrue(systemControl.station.baggingArea.getCurrentWeight() >= itemsControl.getWrongBaggedItem().getWeight());
//			assertTrue(
//					systemControl.station.baggingArea.getCurrentWeight() - 1 <= itemsControl.getWrongBaggedItem().getWeight());
//		} catch (OverloadException e) {
//		}
//
//	}
//	@Test
//	public void testPlaceItemRemoveLastItem() {
//		systemControl.customer.shoppingCart.add(itemsControl.getWrongBaggedItem());
//		systemControl.customer.selectNextItem();
//<<<<<<< HEAD
//		
//		itemsControl.placeItemOnBaggingArea();
//		
//		
//=======
//
//		itemsControl.placeItemOnScale();
//
//>>>>>>> 2ca2c2d2fc7b8db7130ff9c97edf79962864bd32
//		try {
//			System.out.println(systemControl.station.baggingArea.getCurrentWeight());
//			assertTrue(systemControl.station.baggingArea.getCurrentWeight() >= itemsControl.getWrongBaggedItem().getWeight());
//		} catch (OverloadException e) {
//		}
//		stub.selected = true;
//		stub.bagging = true;
//
//		itemsControl.removeLastBaggedItem();
//		assertFalse(stub.selected);
//		assertFalse(stub.bagging);
//		try {
//			assertTrue(systemControl.station.baggingArea.getCurrentWeight() <= 1);
//		} catch (OverloadException e) {
//		}
//
//	}

	@Test
	public void testPlaceBulkyItemInCart() {
		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.selectNextItem();
		stub.bagging = true;
		stub.selected = true;
		assertTrue(systemControl.customer.bulkyItemStorage.isEmpty());
		itemsControl.placeBulkyItemInCart();
		assertFalse(stub.bagging);
		assertFalse(stub.selected);
		assertFalse(systemControl.customer.bulkyItemStorage.isEmpty());
		systemControl.customer.bulkyItemStorage.clear();
	}

	@Test
	public void testPlaceBulkyItemInCartNoItem() {

		assertFalse(stub.bagging);
		assertFalse(stub.selected);
		assertTrue(systemControl.customer.bulkyItemStorage.isEmpty());
		itemsControl.placeBulkyItemInCart();
		assertFalse(stub.bagging);
		assertFalse(stub.selected);
		assertTrue(systemControl.customer.bulkyItemStorage.isEmpty());
	}

	@Test
	public void testBarcodeScanned() {
		assertFalse(stub.bagging);
		itemsControl.barcodeScanned(systemControl.station.handheldScanner, null);
		assertTrue(stub.bagging);
	}

	@Test
	public void testWeightChanged() {
		stub.bagging = true;
		stub.selected = true;
		itemsControl.weightChanged(systemControl.station.baggingArea, 1);
		assertFalse(stub.bagging);
		assertFalse(stub.selected);
	}

	@Test
	public void testActionPerformedPickUp() {
		ActionEvent e = new ActionEvent(this, 0, "pick up");

		systemControl.customer.shoppingCart.add(item);

		assertFalse(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		itemsControl.actionPerformed(e);
		assertTrue(stub.selected);
		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		assertFalse(stub.available);

	}

//	@Test
//	public void testActionPerformedScan() {
//		ActionEvent e = new ActionEvent(this, 0, "handheld scan");
//
//		systemControl.customer.shoppingCart.add(item);
//
//		systemControl.customer.shoppingCart.add(item);
//		systemControl.customer.selectNextItem();
//		assertFalse(stub.bagging);
//
//		while (!stub.bagging) {
//			itemsControl.actionPerformed(e);
//		}
//		assertTrue(stub.bagging);
//
//	}

	@Test
	public void testActionPerformedPutBack() {
		ActionEvent e = new ActionEvent(this, 0, "put back");

		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.selectNextItem();
		stub.selected = true;
		stub.available = false;

		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		itemsControl.actionPerformed(e);
		assertFalse(stub.selected);
		assertFalse(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);

	}

	@Test
	public void testActionPerformedBag() {
		ActionEvent e = new ActionEvent(this, 0, "bag");

		systemControl.customer.shoppingCart.add(item);
		systemControl.customer.selectNextItem();
		stub.bagging = true;
		stub.selected = true;
		itemsControl.actionPerformed(e);
		assertFalse(stub.bagging);
		assertFalse(stub.selected);

	}

	@Test
	public void testActionPerformedRemove() {
		ActionEvent e = new ActionEvent(this, 0, "removeFromScale");
		AttendantListenerStub als = new AttendantListenerStub();
		systemControl.getAttendantControl().addListener(als);

		als.noBagging = false;
		itemsControl.actionPerformed(e);
		assertTrue(als.noBagging);

	}

	@Test
	public void testActionPerformedPay() {
		ActionEvent e = new ActionEvent(this, 0, "pay");

		systemControl.listeners.clear();
		StubSystem scStub = new StubSystem();
		systemControl.listeners.add(scStub);

		scStub.triggerPaymentWorkflow = false;
		itemsControl.actionPerformed(e);
		assertTrue(scStub.triggerPaymentWorkflow);
	}

	@Test
	public void testActionPerformedNoAction() {
		ActionEvent e = new ActionEvent(this, 0, "");

		assertFalse(stub.bagging);
		assertFalse(stub.selected);
		assertTrue(stub.available);
		itemsControl.actionPerformed(e);
		assertTrue(stub.available);
		assertFalse(stub.bagging);
		assertFalse(stub.selected);

	}

	@Test
	public void testActionPerformedScanNoItem() {
		ActionEvent e = new ActionEvent(this, 0, "scan");
		systemControl.customer.shoppingCart.clear();

		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		assertFalse(stub.bagging);
		itemsControl.actionPerformed(e);
		assertTrue(systemControl.customer.shoppingCart.isEmpty());
		assertTrue(stub.available);
		assertFalse(stub.selected);
		assertFalse(stub.bagging);
	}

	@Test
	public void testAddItemToCheckoutList() {
		Tuple<String, Double> output;

		assertTrue(itemsControl.getCheckoutList().size() == 0);

		assertFalse(stub.itemsHaveBeenUpdated);
		assertFalse(stub.productSubtotalUpdated);
		itemsControl.addItemToCheckoutList(itemTuple);
		assertTrue(stub.itemsHaveBeenUpdated);
		assertTrue(stub.productSubtotalUpdated);

		output = (Tuple<String, Double>) itemsControl.getCheckoutList().get(0);
		assertTrue(output.x.equals("Can of Beans"));
		assertTrue(output.y == (double) 2);
	}

	@Test
	public void testaddScannedItemToCheckoutList() {
		Tuple<String, Double> output;

		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertFalse(itemsControl.getCheckoutTotal() == (double) 2);

		assertFalse(stub.itemsHaveBeenUpdated);
		assertFalse(stub.productSubtotalUpdated);
		itemsControl.addScannedItemToCheckoutList(barcode);
		assertTrue(stub.itemsHaveBeenUpdated);
		assertTrue(stub.productSubtotalUpdated);

		output = (Tuple<String, Double>) itemsControl.getCheckoutList().get(0);
		assertTrue(output.x.equals("Can of Beans"));
		assertTrue(output.y == (double) 2);
		assertTrue(itemsControl.getCheckoutTotal() == (double) 2);

	}

	@Test
	public void testaddScannedItemToCheckoutListNull() {

		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertFalse(itemsControl.getCheckoutTotal() == (double) 2);

		assertFalse(stub.itemsHaveBeenUpdated);
		assertFalse(stub.productSubtotalUpdated);
		itemsControl.addScannedItemToCheckoutList(null);

		assertTrue(itemsControl.getCheckoutList().size() == 0);
		assertFalse(itemsControl.getCheckoutTotal() == (double) 2);

		assertFalse(stub.itemsHaveBeenUpdated);
		assertFalse(stub.productSubtotalUpdated);

	}

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
		itemsControl.overload(systemControl.station.baggingArea);
		assertTrue(itemsControl.userMessage.equals("Weight on scale has been overloaded, weight limit is: 5000.0"));
	}

	@Test
	public void testOutOfOverload() {
		itemsControl.outOfOverload(systemControl.station.baggingArea);
		assertTrue(itemsControl.userMessage.equals("Excessive weight removed, continue scanning"));
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testAddPLUCodedItemByBrowsing() {
		PriceLookUpCode pcode = new PriceLookUpCode("4444");
		PLUCodedProduct pcp = new PLUCodedProduct(pcode, "Durian", 9);
		PLUCodedItem pitem = new PLUCodedItem(pcode, 200);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pcode, pcp);
		ProductDatabases.INVENTORY.put(pcp, 10);

		itemsControl.setCurrentItem(pitem);
		itemsControl.setIsPLU(true);
		itemsControl.setCurrentProductCode(pcode);
		itemsControl.setExpectedPLU(pcode);

		// Flip Switch to Enter Catalog Browsing Code
		ActionEvent e = new ActionEvent(this, 0, "catalog");
		itemsControl.actionPerformed(e);

		e = new ActionEvent(this, 0, "Durian");
		itemsControl.actionPerformed(e);
		assertFalse(itemsControl.getInCatalog());
	}

	@Test
	public void testAddPLUCodedItemNotInProductDatabaseByBrowsing() {
		itemsControl.setIsPLU(true);

		// Flip Switch to Enter Catalog Browsing Code
		ActionEvent e = new ActionEvent(this, 0, "catalog");
		itemsControl.actionPerformed(e);

		e = new ActionEvent(this, 0, "Strawberry");
		itemsControl.actionPerformed(e);
		assertTrue(itemsControl.getInCatalog());
	}

	@Test
	public void testAddNonPLUCodedItemItemByBrowsing() {
		// Flip Switch to Enter Catalog Browsing Code
		ActionEvent e = new ActionEvent(this, 0, "catalog");
		itemsControl.actionPerformed(e);

		e = new ActionEvent(this, 0, "Can of Beans");
		itemsControl.actionPerformed(e);
		assertFalse(itemsControl.getInCatalog());
	}

	@Test
	public void testCancelCatalog() {
		// Flip Switch to Enter Catalog Browsing Code
		ActionEvent e = new ActionEvent(this, 0, "catalog");
		itemsControl.actionPerformed(e);
		assertTrue(itemsControl.getInCatalog());

		e = new ActionEvent(this, 0, "cancel catalog");
		itemsControl.actionPerformed(e);
		assertFalse(itemsControl.getInCatalog());
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

		@Override
		public void awaitingItemToBePlacedInScanningArea(StationControl sc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void itemRemoved(ItemsControl itemsControl) {
			// TODO Auto-generated method stub

		}

		@Override
		public void awaitingAttendantToApproveItemRemoval(ItemsControl ic) {
			// TODO Auto-generated method stub

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
		public void addTooMuchPaperState() {
			addPaper = true;

		}

		@Override
		public void addTooMuchInkState() {
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

		@Override
		public void banknotesInStorageLowState() {
			// TODO Auto-generated method stub

		}

		@Override
		public void printerNotLowInkState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void printerNotLowPaperState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinIsLowState(int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void banknotesNotLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinsNotLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerItemSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exitTextSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}
	}
}