package com.diy.software.test.logic;

import com.diy.software.util.Tuple;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.MembershipControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.MembershipControlListener;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.Card.CardInsertData;
import com.jimmyselectronics.opeechee.Card.CardSwipeData;
import com.jimmyselectronics.opeechee.CardReader;

import ca.powerutility.PowerGrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.*;

public class TestSystemControl {
	
	private StationControl systemControl;
	private FakeDataInitializer fdi;
	private StubSystem stub;
	private WalletStub wStub;
	private MembershipStub mStub;
	Barcode[] barcodes;
	BarcodedItem[] items;
	private ItemsControl ic;
	private Card membershipCard;
	private Card giftcard;
	
	@Before
	public void setup() {
		
		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		fdi.addCardData();
		fdi.addFakeMembers();
		barcodes = fdi.getBarcodes();
		items = (BarcodedItem[])fdi.getItems();
		
		membershipCard = fdi.getCards()[3];
		
		giftcard = fdi.getCards()[4];
		
		PowerGrid.engageUninterruptiblePowerSource();
		
		systemControl = new StationControl(fdi);
		stub = new StubSystem();
		systemControl.register(stub);
		ic = systemControl.getItemsControl();
		
		wStub = new WalletStub();
		systemControl.getWalletControl().addListener(wStub);
		
		mStub = new MembershipStub();
		systemControl.getMembershipControl().addListener(mStub);
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
	public void testCardDataReadMembership() throws IOException {
		CardReader cardReader = systemControl.getStation().cardReader;
		CardSwipeData cardSwipeData;
		do{
			cardSwipeData = (CardSwipeData) cardReader.swipe(membershipCard);
		}
		while(cardSwipeData == null);
		
		systemControl.cardDataRead(cardReader, cardSwipeData);
		assertEquals("Welcome! Itadori", mStub.memberName);
	}
	
	@Test
	public void testBarcodeScannedMembership() {
		systemControl.startMembershipCardInput();
		BarcodeScanner scanner = systemControl.getStation().mainScanner;
		
		String number = membershipCard.number;
		Numeral[] code = new Numeral [number.length()];
		
		// Converting string number into Numeral array
		for (int i = 0; i < number.length(); i++) {
			Numeral digit = Numeral.valueOf(toByteDigit(number.charAt(i)));
			code[i] = digit;
		}
		Barcode barcode = new Barcode(code);
		
		systemControl.barcodeScanned(scanner, barcode);
		assertEquals("Welcome! Itadori", mStub.memberName);
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
	
	@Test
	public void giftcardTest() {
		ic.updateCheckoutTotal(40);
		CardSwipeData a = null;
		while(a == null) {
			try {
				a = (CardSwipeData) systemControl.getStation().cardReader.swipe(giftcard);
			} catch (Exception e) {}
		}
		assertTrue(ic.getCheckoutTotal() == 0);
		ic.updateCheckoutTotal(40);
		a = null;
		while(a == null) {
			try {
				a = (CardSwipeData) systemControl.station.cardReader.swipe(giftcard);
			} catch (Exception e) {}
		}
		assertTrue(ic.getCheckoutTotal() == 30);
		ic.updateCheckoutTotal(-30);
		a = null;
		while(a == null) {
			try {
				a = (CardSwipeData) systemControl.station.cardReader.swipe(giftcard);
			} catch (Exception e) {}
		}
		assertTrue(ic.getCheckoutTotal() == 0);
		ic.updateCheckoutTotal(10);
		a = null;
		while(a == null) {
			try {
				a = (CardSwipeData) systemControl.station.cardReader.swipe(giftcard);
			}catch (Exception e) {}
		}
		assertTrue(ic.getCheckoutTotal() == 10);
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
	
	private byte toByteDigit(char c) {
		byte b;
		switch (c) {
			case '0':	return b = 0;
			case '1':	return b = 1;
			case '2':	return b = 2;
			case '3':	return b = 3;
			case '4':	return b = 4;
			case '5':	return b = 5;
			case '6':	return b = 6;
			case '7':	return b = 7;
			case '8':	return b = 8;
			case '9':	return b = 9;
			default:	return b = -1;
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
	
	public class MembershipStub implements MembershipControlListener{

		public String memberName;
		public String memberNumber;
		public boolean scanSwipeSelected = false;
		public boolean membershipInput = true;

		@Override
		public void welcomeMember(MembershipControl mc, String memberName) {
			this.memberName = memberName;
		}

		@Override
		public void memberFieldHasBeenUpdated(MembershipControl mc, String memberNumber) {
			this.memberNumber = memberNumber;
			
		}

		@Override
		public void scanSwipeSelected(MembershipControl mc) {
			scanSwipeSelected = true;
			
		}

		@Override
		public void disableMembershipInput(MembershipControl mc) {
			membershipInput = false;
			
		}
		
	}
}