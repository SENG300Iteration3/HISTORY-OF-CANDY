package com.diy.software.test.logic;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.*;

import com.diy.software.util.Tuple;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.diy.simulation.Customer;
import com.diy.software.controllers.*;
import com.diy.software.enums.PaymentType;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.MembershipControlListener;
import com.diy.software.listeners.PaymentControlListener;
import com.diy.software.listeners.StationControlListener;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.opeechee.CardReaderListener;
import com.jimmyselectronics.opeechee.InvalidPINException;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;

import ca.powerutility.PowerGrid;

public class TestMain {
	/*-------------------------Initialized in Before---------------------------*/
	Customer customer;
	StationControl controller;
	SystemControlStub systemControlStub;
	PaymentControlStub paymentControlStub;
	WalletControlStub walletControlStub;
	WalletControlStub cardReaderStub;
	FakeDataInitializer fakeData;
	ElectronicScaleStub electronicScaleStub;
	MembershipControlListenerStub membershipControlListenerStub;
	PaymentControl pc;
	WalletControl wc;
	MembershipControl mc;
	AttendantControl ac;
	BagsControl bc;
	ItemsControl ic;
	PinPadControl ppc;
	ArrayList<Tuple<String, Double>> checkoutList;
	double checkoutListTotal;
	double weightOfLastItem;
	double expectedWeight;
	boolean itemInScale;
	boolean flag;
	/*----------------------------------------------------*/
	
	/*-------------------------Needs to be written to---------------------------*/
	String reciept;
	boolean expectedWeightMatchesActual;
	/*----------------------------------------------------*/
	
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();

		fakeData = new FakeDataInitializer();
		controller = new StationControl(fakeData);
		cardReaderStub = new WalletControlStub();
		
		systemControlStub = new SystemControlStub(); 
		controller.register(systemControlStub);
		controller.station.cardReader.register(cardReaderStub);

		paymentControlStub = new PaymentControlStub();
		walletControlStub = new WalletControlStub();

		membershipControlListenerStub = new MembershipControlListenerStub();
		

		customer = controller.customer;
		electronicScaleStub = new ElectronicScaleStub();
		controller.station.baggingArea.register(electronicScaleStub);

		ppc = controller.getPinPadControl();
		//ArrayList<Barcode> barcodes = controller.getBarcodes();
		
		itemInScale = false;
		flag = false;

		pc = controller.getPaymentControl();
		pc.addListener(paymentControlStub);

		wc = controller.getWalletControl();
		wc.addListener(walletControlStub);

		ac = controller.getAttendantControl();
		bc = controller.getBagsControl();
		ic = controller.getItemsControl();

		mc = controller.getMembershipControl();
		mc.addListener(membershipControlListenerStub);

		ppc = controller.getPinPadControl();

		RecieptPrinterListenerStub recieptPrinterListenerStub = new RecieptPrinterListenerStub();
		controller.station.printer.register(recieptPrinterListenerStub);

		customer.shoppingCart.add(fakeData.getItems()[0]);

		controller.station.plugIn();
		controller.station.turnOn();

		update();

	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}
	
	/**
	 * Updates values that are changed by SystemControl
	 */
	public void update() {
		checkoutList = ic.getCheckoutList();
		checkoutListTotal = ic.getCheckoutTotal();
		weightOfLastItem = controller.getWeightOfLastItemAddedToBaggingArea();
		expectedWeight = controller.getExpectedWeight();
	}

	@Test
	public void testUpdatingCheckoutManuallyWithBag() {
		Item item = fakeData.getItems()[0];
		BarcodedItem bitem = (BarcodedItem)item;
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(bitem.getBarcode());
		customer.selectNextItem();
		customer.scanItem(true);

		assertTrue(ic.getCheckoutTotal() == product.getPrice());

<<<<<<< HEAD
		// bc.placePurchasedBagsInBaggingArea();
=======
		bc.placeBagsInBaggingArea();
>>>>>>> d9a659bcdb920e21592c85b742eb33244ae4ba54
		controller.bagItem(bitem);

		assertTrue(ic.getCheckoutTotal() == product.getPrice() + bc.getArbitraryBagPrice());
	}

	@Test
	public void testUpdatingCheckoutExpectedWeightWithBag() {
		Item item = fakeData.getItems()[0];
		BarcodedItem bitem = (BarcodedItem)item;
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(bitem.getBarcode());
		customer.selectNextItem();
		customer.scanItem(true);

		assertTrue(controller.getExpectedWeight() == product.getExpectedWeight());

<<<<<<< HEAD
		// bc.placePurchasedBagsInBaggingArea();
=======
		bc.placeBagsInBaggingArea();
>>>>>>> d9a659bcdb920e21592c85b742eb33244ae4ba54
		controller.bagItem(bitem);

		controller.updateExpectedCheckoutWeight(bc.getArbitraryBagWeight(), false);

		assertTrue(controller.getExpectedWeight() == product.getExpectedWeight() + bc.getArbitraryBagWeight()*2);
	}

	// This test should throw something but it doesn't
	@Test(expected = InvalidPINException.class)
	public void testAskForPinWithoutCard() {
		controller.askForPin("000");
		controller.cardRemoved(controller.station.cardReader);
		controller.cardTapped(controller.station.cardReader);
	}

	@Test
	public void testAskForPinWithAValidCard() {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("VISA");
		controller.startPaymentWorkflow();
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().tapCard();
		
		assertTrue(flag);
	}

	@Test(expected = InvalidPINException.class)
	public void testAskForPinWithWrongPin() {
		controller.askForPin("000");
		controller.cardRemoved(controller.station.cardReader);
		controller.cardTapped(controller.station.cardReader);
	}


	@Test
	public void guiTest() {
		controller.goBackOnUI();
		controller.goToInitialScreenOnUI();
	}
	
	/*---These tests interact with the hardware to test the system controller---*/
	@Test
	public void scanWhileBlocked() {
		BarcodeScanner scanner = controller.station.handheldScanner;
		controller.blockStation();
		assertFalse(scanner.scan((BarcodedItem)fakeData.getItems()[1]));
		controller.unblockStation();

		assertTrue(scanner.scan((BarcodedItem)fakeData.getItems()[2]));
	}
	
	/**
	 * Tests the expected weight after scanning 3 items.
	 * Also tests if the database item weights are what they
	 * say they are.
	 */
	@Test
	public void testExpectedCheckoutWeight() {
		double expected = 0;
		ArrayList<Item> tempList = new ArrayList<>();
		for(Item item: customer.shoppingCart) {
			tempList.add(item);
		}
		for(Item item : tempList) {
			BarcodedItem barcodedItem = (BarcodedItem)item;
			expected += barcodedItem.getWeight();

			customer.selectNextItem();
			customer.scanItem(true);
		}
	
		assertTrue("sum is "+controller.getExpectedWeight() + " expected is "+expected, controller.getExpectedWeight() == expected);
		assertTrue(expected == controller.getExpectedWeight());
	}

	@Test
	public void testExpectedWeightWithOneScannedItem() {
		double sum1 = 0;
		double sum2 = 0;
		for(Item item : customer.shoppingCart) {
			sum1 +=item.getWeight();
			BarcodedItem temp = (BarcodedItem)item;
			sum2 += temp.getWeight();
		}

		assertTrue(sum1 == sum2);

		customer.selectNextItem();
		customer.scanItem(true);

		assertTrue(sum1 == controller.getExpectedWeight());
	}

	@Test
	public void removeItemFromScale() throws OverloadException {
		int sizeBefore = customer.shoppingCart.size();
		Item item = customer.shoppingCart.get(0);
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();

		assertTrue(customer.shoppingCart.size() == sizeBefore-1);
		double weightBefore = controller.station.baggingArea.getCurrentWeight();
		assertTrue(controller.station.baggingArea.getCurrentWeight() != 0);
		controller.removeItem(item);
		assertTrue(controller.station.baggingArea.getCurrentWeight() != weightBefore);
	}

	@Test
	public void testCheckMembership() {
		controller.startMembershipWorkflow();
		mc.checkMembership(1234);
		assertTrue(membershipControlListenerStub.message.equals("Welcome! Itadori"));
		
		mc.checkMembership(1239);
		
		
		assertTrue(membershipControlListenerStub.message.equals("Member not found try again!"));
		
		
	}
	public class MembershipControlListenerStub implements MembershipControlListener{

		public String message;

		@Override
		public void welcomeMember(MembershipControl mc, String memberName) {
			message = memberName;
		}

		@Override
		public void memberFieldHasBeenUpdated(MembershipControl mc, String memberNumber) {
			message = memberNumber;
			
		}

		@Override
		public void scanSwipeSelected(MembershipControl mc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disableMembershipInput(MembershipControl mc) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Test
	public void testExpectedWeightWithNoScannedItems() {
		assertTrue(controller.getExpectedWeight() == 0);
	}

	@Test(expected = NoSuchElementException.class)
	public void testScanningNullItem() {
		customer.shoppingCart.clear();
		customer.shoppingCart.add(null);

		customer.selectNextItem();
		customer.scanItem(true);
	}

	/**
	 * Tests the addition of items in the checkout list when they are scanned.
	 * Also tests the function of Tuple, and Product Database
	 */
	@Test
	public void testCheckoutList() {
		customer.selectNextItem();
		
		// Get checkout list is not populated?
		update();
		ArrayList<Tuple<String, Double>> list = checkoutList;
		
		assertEquals("Size is not 0", 0, list.size());

		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		update();
		list = checkoutList;
		
		ArrayList<Tuple<String, Double>> manualList = new ArrayList<>();
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(((BarcodedItem)fakeData.getItems()[0]).getBarcode());
		Tuple<String, Double> item = new Tuple<String, Double>(product.getDescription(), fakeData.getItems()[0].getWeight());
		manualList.add(item);
		
		assertEquals("Size is not 1", 1, list.size());
		assertEquals("List does not contain product!", list.get(0).x, manualList.get(0).x);
	}
	
	@Test
	public void testGetBillTotal() {
		BarcodedItem item = (BarcodedItem)customer.shoppingCart.get(0);
		BarcodedProduct temp = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(item.getBarcode());
		double expected = temp.getPrice();

		customer.selectNextItem();
		update();

		assertTrue("Total is wrong!", 0 == checkoutListTotal);

		customer.scanItem(true);
		update();

		double total = checkoutListTotal;
		assertTrue("Total is wrong!", total == expected);
	}
	
	/*
	 *  Expected behavior:
	 *  
	 *  Item scanned -> station locked until item placed in bagging area -> item placed in bagging area ->
	 *  Item scanned -> station locked -> wrong item placed in bagging area -> currentWeight != expectedWeight ->
	 *  station locked until wrong item removed from bagging area and the scanned item added to baggging area
	 */
	@Test 
	public void testWeightDiscrepency() {
		customer.selectNextItem();
		
		double sumOfScannedWeights = 0;
		
		customer.scanItem(true);
		sumOfScannedWeights += fakeData.getItems()[0].getWeight();
		
		assertFalse("Station is not locked after scanning an item!", controller.station.handheldScanner.isDisabled());
		double expectedWeight = controller.getExpectedWeight();
		assertTrue(controller.expectedWeightMatchesActualWeight(sumOfScannedWeights));
		assertTrue("expected weight does not equal sum of scanned weights!", sumOfScannedWeights == expectedWeight);
		
		controller.bagItem(fakeData.getItems()[0]);
		assertTrue("Station is locked even after adding correct item to bagging area!", controller.station.handheldScanner.isDisabled());
	}
	
	
	@Test
	public void testUnregister() {
		assertTrue(controller.listeners.contains(systemControlStub));
		controller.unregister(systemControlStub);
		assertFalse(controller.listeners.contains(systemControlStub));
	}
	
	@Test
	public void testElectronicScale() {
		customer.selectNextItem();
		customer.scanItem(true);
		
		controller.bagItem(fakeData.getItems()[0]);
		assertTrue(itemInScale);
	}

	@Test(expected = IOException.class)
	public void testInvalidPinCreditCardAMEX() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		customer.selectCard(customer.wallet.cards.get(0).kind);
		customer.insertCard("0000");
	}
	@Test(expected = IOException.class)
	public void testInvalidPinCreditCardVisa() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		customer.selectCard(customer.wallet.cards.get(1).kind);
		customer.insertCard("0000");
	}
	@Test(expected = IOException.class)
	public void testInvalidPinCreditCardMC() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		customer.selectCard(customer.wallet.cards.get(2).kind);
		customer.insertCard("0000");
	}
	@Test(expected = IOException.class)
	public void testNullPin() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		customer.selectCard(customer.wallet.cards.get(1).kind);
		customer.insertCard("0000");
	}

	@Test
	public void testPayingWithAMEXCorrectlyInsert() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("AMEX");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().insertCard("1234");
		
		
		assertTrue(flag);
	}

	
	@Test
	public void testPayingWithVISACorrectlyInsert() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("VISA");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().insertCard("0987");
		
		
		assertTrue(flag);
	}

	/**
	 * In this case MC has only tap implemented so this test should fail for now
	 * @throws IOException
	 */
	@Test
	public void testPayingWithMCCorrectlyInsert() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("MAST");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().insertCard("1111");
		
		assertTrue(flag);
	}

	@Test
	public void testPayingWithAMEXCorrectlySwipe() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("AMEX");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().swipeCard();
		
		assertTrue(flag);
	}

	/**
	 * In this case VISA does not have swipe implemented so this test should fail for now
	 * @throws IOException
	 */
	@Test
	public void testPayingWithVISACorrectlySwipe() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("VISA");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().swipeCard();
		
		assertTrue(flag);
	}

	/**
	 * In this case MC does not have swipe implemented so this test should fail for now
	 * @throws IOException
	 */
	@Test
	public void testPayingWithMCCorrectlySwipe() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("MAST");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().swipeCard();
		
		assertTrue(flag);
	}

	/**
	 * In this case AMEX does not have tap implemented so this test should fail for now
	 * @throws IOException
	 */
	@Test
	public void testPayingWithAMEXCorrectlyTap() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("AMEX");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().tapCard();
		
		assertTrue(flag);
	}

	/**
	 * In this case VISA does not have tap implemented so this test should fail for now
	 * @throws IOException
	 */
	@Test
	public void testPayingWithVISACorrectlyTap() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("VISA");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().tapCard();
		
		assertTrue(flag);
	}

	@Test
	public void testPayingWithMCCorrectlyTap() throws IOException {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();
		
		
		assertTrue(ic.getCheckoutTotal() != 0);
		
		controller.startPaymentWorkflow();
		wc.selectCard("MAST");
		pc.startPaymentProcess(PaymentType.Credit);
		controller.getWalletControl().tapCard();
		
		assertTrue(flag);
	}

	@Test
	public void testRemovingItems() {
		Item item = customer.shoppingCart.get(0);
		BarcodedItem bitem = (BarcodedItem)item;
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(bitem.getBarcode());
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();

		System.out.println(product.getExpectedWeight());

		System.out.println(customer.shoppingCart.size());
		assertTrue(customer.shoppingCart.size() == 0);
		assertTrue(ic.getCheckoutList().size() == 1);
		System.out.println(controller.getWeightOfLastItemAddedToBaggingArea() + " " + bitem.getWeight());
		assertTrue(controller.getWeightOfLastItemAddedToBaggingArea() == bitem.getWeight());
		assertTrue(controller.getExpectedWeight() == bitem.getWeight());
		assertTrue(ic.getCheckoutTotal() == product.getPrice());

		// Removing doesn't remove the item from the checkout I think I wrote this wrong??
		controller.removeItem(item);
		
		assertTrue(customer.shoppingCart.size() == 0);
		assertTrue(ic.getCheckoutList().size() == 1);
		assertTrue(controller.getExpectedWeight() == 0);
		assertTrue(ic.getCheckoutTotal() == 0);
	}

	@Test
	public void testBankHoldLessThanNegative1() {
		CardSwipeDataStub myStub = new CardSwipeDataStub();
		CardIssuer bank = fakeData.getCardIssuer();
		bank.authorizeHold("0000000000000000", 10);
		controller.cardDataRead(controller.station.cardReader, myStub);
		assertTrue(flag);

	}

	@Test
	public void testRecieptPrinting() {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();

		controller.station.printer.turnOn();
		controller.printReceipt("I AM A RECIEPT");
		
		assertTrue(flag);

	}
	// The exceptions are caught in the implementation so not really a way to test it
	@Test
	public void testRecieptPrinterEmptyString() {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();

		controller.station.printer.turnOn();
		controller.printReceipt("");

		controller.outOfInk(null);
		controller.outOfPaper(null);
	}
	@Test
	public void testRecieptPrinterOverloadString() {
		customer.selectNextItem();
		customer.scanItem(true);
		customer.placeItemInBaggingArea();

		controller.station.printer.turnOn();
		controller.printReceipt("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

	}

	@Test
	public void testOverload() {
		controller.overload(controller.station.baggingArea);
		assertTrue(controller.station.handheldScanner.isDisabled());

		controller.outOfOverload(controller.station.baggingArea);
		assertFalse(controller.station.handheldScanner.isDisabled());
	}

	@Test
	public void testPopulateReciept() {
		ArrayList<Tuple<BarcodedProduct, Integer>> list = new ArrayList<>();
		Item item = fakeData.getItems()[0];
		BarcodedItem bitem = (BarcodedItem)item;
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(bitem.getBarcode());
		list.add(new Tuple<BarcodedProduct,Integer>(product, 10));

		String string = controller.populateReceipt(list);

		// Size of the reciept with only one item
		assertTrue(string.split(" ").length == 30);
	}

	public class RecieptPrinterListenerStub implements ReceiptPrinterListener {

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			flag = true;
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			flag = true;
			
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			flag = true;
			
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			flag = true;
			
		}

		@Override
		public void outOfPaper(IReceiptPrinter printer) {
			System.out.println("out of paper");
			flag = true;
		}

		@Override
		public void outOfInk(IReceiptPrinter printer) {
			flag = true;
			
		}

		@Override
		public void lowInk(IReceiptPrinter printer) {
			flag = true;
			
		}

		@Override
		public void lowPaper(IReceiptPrinter printer) {
			flag = true;
			
		}

		@Override
		public void paperAdded(IReceiptPrinter printer) {
			flag = true;
			
		}

		@Override
		public void inkAdded(IReceiptPrinter printer) {
			flag = true;
			
		}
		
	}

	public class CardSwipeDataStub implements CardData {

		@Override
		public String getNumber() {
			return "0000000000000000";
		}

		@Override
		public String getCardholder() {
			return "Test Card";
		}

		@Override
		public String getCVV() {
			return "000";
		}

		@Override
		public String getType() {
			return "AMEX";
		}
		
	}
	
	public class PaymentControlStub implements PaymentControlListener {

		@Override
		public void paymentMethodSelected(PaymentControl pc, PaymentType type) {
			System.out.printf("Selected %s\n", type);
		}
		
	}
	
	public class WalletControlStub implements WalletControlListener, CardReaderListener {

		@Override
		public void cardHasBeenSelected(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardPaymentsEnabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardPaymentsDisabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
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
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardInserted(CardReader reader) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardRemoved(CardReader reader) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardTapped(CardReader reader) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardSwiped(CardReader reader) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardDataRead(CardReader reader, CardData data) {
			flag = true;
			System.out.println("ERERER");
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

	public class SystemControlStub implements StationControlListener {

		@Override
		public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {
			System.out.println("Payment canceled!");
			flag = true;
			
		}

		@Override
		public void paymentsHaveBeenEnabled(StationControl systemControl) {
			
			
		}

		@Override
		public void initiatePinInput(StationControl systemControl, String kind) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPanelBack(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerInitialScreen(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPaymentWorkflow(StationControl systemControl) {
			System.out.println("PAYMENT WORKFLOW!");
			
		}

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerMembershipWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startMembershipCardInput(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputFinished(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputCanceled(StationControl systemControl, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPurchaseBagsWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noBagsInStock(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerPLUCodeWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerBrowsingCatalog(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}
<<<<<<< HEAD
=======

		@Override
		public void triggerReceiptScreen(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}
>>>>>>> d9a659bcdb920e21592c85b742eb33244ae4ba54
		
	}
	
	public class ElectronicScaleStub implements ElectronicScaleListener {

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void weightChanged(ElectronicScale scale, double weightInGrams) {
			itemInScale = true;
			
		}

		@Override
		public void overload(ElectronicScale scale) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void outOfOverload(ElectronicScale scale) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
