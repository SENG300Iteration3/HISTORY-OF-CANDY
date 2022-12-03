package com.diy.software.test.logic;

import com.diy.software.util.Tuple;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;

import ca.powerutility.PowerGrid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.*;

public class TestSystemControl {
	
	private StationControl systemControl;
	private FakeDataInitializer fdi;
	private StubSystem stub;
	private WalletStub wStub;
	Barcode[] barcodes;
	BarcodedItem[] items;
	private ItemsControl ic;
	private Card membershipCard;
	
	@Before
	public void setup() {
		
		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		barcodes = fdi.getBarcodes();
		items = fdi.getItems();
		
		membershipCard = fdi.getCards()[3];
		
		PowerGrid.engageUninterruptiblePowerSource();
		
		systemControl = new StationControl();
		stub = new StubSystem();
		systemControl.register(stub);
		ic = systemControl.getItemsControl();
		
		wStub = new WalletStub();
		systemControl.getWalletControl().addListener(wStub);
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
	
	@Test
	public void testCardDataReadMembership() {
		//TODO
		//systemControl.cardDataRead(, null);
	}
	
	@Test
	public void testBarcodeScannedMembership() {
		//TODO
	}
	
	@Test
	public void startMembershipCardInput() {
		systemControl.startMembershipCardInput();
		assertTrue(wStub.membershipCardInputEnabled);
	}
	
	@Test
	public void testCancelMembershipCardInput() {
		wStub.membershipCardInputEnabled = true;
		systemControl.cancelMembershipCardInput();
		assertFalse(wStub.membershipCardInputEnabled);
	}
	
	@Test
	public void triggerMembershipCardInputFailScreen() {
		wStub.membershipCardInputEnabled = true;
		systemControl.triggerMembershipCardInputFailScreen("message");
		assertFalse(wStub.membershipCardInputEnabled);
	}
	
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
	
	public class WalletStub implements WalletControlListener{

		public boolean cardHasBeenSelected = false;
		public boolean paymentsEnabled = false;
		public boolean inserted = false;
		public boolean membershipCardSelected = false;
		public boolean membershipCardInputEnabled = false;

		@Override
		public void cardHasBeenSelected(WalletControl wc) {
			cardHasBeenSelected = true;
			
		}

		@Override
		public void cardPaymentsEnabled(WalletControl wc) {
			paymentsEnabled = true;
			
		}

		@Override
		public void cardPaymentsDisabled(WalletControl wc) {
			paymentsEnabled = false;
			
		}

		@Override
		public void cardHasBeenInserted(WalletControl wc) {
			
		}

		@Override
		public void cardWithPinInserted(WalletControl wc) {
			inserted = true;
			
		}

		@Override
		public void cardWithPinRemoved(WalletControl wc) {
			inserted = false;
			
		}

		@Override
		public void membershipCardHasBeenSelected(WalletControl wc) {
			membershipCardSelected = true;
			
		}

		@Override
		public void membershipCardInputEnabled(WalletControl wc) {
			membershipCardInputEnabled = true;
			
		}

		@Override
		public void membershipCardInputCanceled(WalletControl walletControl) {
			membershipCardInputEnabled = false;
			
		}
		
	}
}