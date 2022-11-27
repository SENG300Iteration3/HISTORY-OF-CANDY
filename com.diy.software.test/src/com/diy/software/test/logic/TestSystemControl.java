package com.diy.software.test.logic;

import com.diy.software.util.Tuple;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.SystemControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;

import ca.powerutility.PowerGrid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.*;

public class TestSystemControl {
	
	private SystemControl systemControl;
	private FakeDataInitializer fdi;
	private StubSystem stub;
	Barcode[] barcodes;
	BarcodedItem[] items;
	private ItemsControl ic;
	
	@Before
	public void setup() {
		
		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		barcodes = fdi.getBarcodes();
		items = fdi.getItems();
		
		PowerGrid.engageUninterruptiblePowerSource();
		
		systemControl = new SystemControl();
		stub = new StubSystem();
		systemControl.register(stub);
		ic = systemControl.getItemsControl();
	}
	
	
	@Test
	public void testgetCheckoutListTotal() {
		scanAndBagItems();
		
		assertTrue(ic.getCheckoutTotal() == 30.0);
	}
	
	@Test
	public void addNewBarcodeTest() {
		assertTrue(ic.getCheckoutList().size() == 0);
		while(!systemControl.station.handheldScanner.scan(items[0])) {}
		assertTrue(ic.getCheckoutList().size() == 1);
	}
	
	@Test
	public void addNullBarcodeTest() {
		assertTrue(ic.getCheckoutList().size() == 0);
		ic.addScannedItemToCheckoutList(null);
		assertTrue(ic.getCheckoutList().size() == 0);
	}
	
	@Test
	public void testGetItemPriceList() {
		ic.addScannedItemToCheckoutList(barcodes[0]);
		ic.addScannedItemToCheckoutList(barcodes[1]);
		ic.addScannedItemToCheckoutList(barcodes[2]);
		ic.addScannedItemToCheckoutList(barcodes[3]);
		
		String[] expectedNames = {"Can of Beans", "Bag of Doritos", "Rib Eye Steak", "Cauliflower"};
		double[] expectedPrices = { 200, 550, 1725, 650};
		
		
		
		ArrayList<Tuple<String, Double>> testingValues = ic.getCheckoutList();
		for (int i = 0; i < testingValues.size(); i++) {
			testingValues.get(i).x.equals(expectedNames[i]);
			testingValues.get(i).y.equals(expectedPrices[i]);
		}
	}
	
	@Test
	public void testWeightsNotMatching() {
		scanAndBagItems();
		BarcodedItem item = new BarcodedItem(new Barcode(new Numeral[] { Numeral.nine, Numeral.two, Numeral.three, Numeral.four }), 600);
		/*
		 *  Adding an item that hasn't been scanned
		 *  now the expected weight won't match the actual weight
		 *  and the system will be locked
		 */
		assertFalse(systemControl.station.cardReader.isDisabled());
		assertFalse(systemControl.station.handheldScanner.isDisabled());
		systemControl.station.baggingArea.add(item);
		assertTrue(systemControl.station.cardReader.isDisabled());
		assertTrue(systemControl.station.handheldScanner.isDisabled());
	}
	
	
	@Test
	public void testblockStation() {
		assertFalse(stub.locked);
		systemControl.blockStation();
		assertTrue(stub.locked);
		
		assertTrue(stub.locked);
		systemControl.unblockStation();
		assertFalse(stub.locked);
	}
	
	@Test
	public void testUpdateExpectedCheckoutWeight() {
		assertTrue(systemControl.getExpectedWeight() == 0);
		systemControl.updateExpectedCheckoutWeight(100);
		assertTrue(systemControl.getExpectedWeight() == 100);
	}
	
	@Test
	public void testUpdateExpectedCheckoutWeightWithBag() {
		assertTrue(systemControl.getExpectedWeight() == 0);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(0));
		systemControl.updateExpectedCheckoutWeight(100, false);
		assertTrue(systemControl.getExpectedWeight() == 100);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(100));
	}
	
	@Test
	public void testUpdateExpectedCheckoutWeightWithBagWeight() {
		assertTrue(systemControl.getExpectedWeight() == 0);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(0));
		systemControl.updateExpectedCheckoutWeight(100, true);
		assertTrue(systemControl.getExpectedWeight() == 100);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(0));
	}
	
	@Test
	public void testUpdateExpectedCheckoutWeightWithBagWeightAndItem() {
		assertTrue(systemControl.getExpectedWeight() == 0);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(0));
		systemControl.updateExpectedCheckoutWeight(100, false);
		assertTrue(systemControl.getExpectedWeight() == 100);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(100));
		systemControl.updateExpectedCheckoutWeight(100, true);
		assertTrue(systemControl.getExpectedWeight() == 200);
		assertTrue(systemControl.expectedWeightMatchesActualWeight(100));
	}
	
	/*@Test (expected = NoPowerException.class)
	public void testblockStationNopower() {
		DoItYourselfStationAR diy = systemControl.station;
		//diy.turnOff();
		//systemControl.blockStation();
		//diy.turnOn();
		
		//systemControl.unblockStation();
	}*/
	
	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		systemControl.station.turnOff();
	}
	
	public void scanAndBagItems() {
		for (BarcodedItem item : items) {
			while(!systemControl.station.handheldScanner.scan(item)) {}
			systemControl.station.baggingArea.add(item);
			
		}
	}
	
}