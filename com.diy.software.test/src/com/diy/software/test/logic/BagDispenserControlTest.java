package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.BagDispenserControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.BagDispenserControlListener;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.svenden.ReusableBag;

import ca.powerutility.PowerGrid;

public class BagDispenserControlTest {
	StationControl sc;
	FakeDataInitializer fakeData;
	ItemsControl ic;
	BagDispenserControl bdc;
	ReusableBag bag;
	int capacity;
	int bagsInStock;		//initial number of bags in stock
	boolean notEnough;
	boolean outOfStock;
	boolean workflowStarted;
	String input;
	
	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fakeData = new FakeDataInitializer();
		sc = new StationControl(fakeData);
		
		bdc = sc.getBagDispenserControl();
		ic = sc.getItemsControl();
		
		sc.register(stationListener);
		bdc.addListener(bagListener);
		bag = new ReusableBag();
		bagsInStock = sc.getBagInStock();
		notEnough = false;
		outOfStock = false;
		workflowStarted = false;
		input = "";
		capacity = sc.station.reusableBagDispenser.getCapacity();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test start of workflow
	 */
	@Test
	public void testStartWorkFlow() {
		ActionEvent e = new ActionEvent(this, 0, "purchase bags");
		bdc.actionPerformed(e);
		assertTrue(workflowStarted);
	}

	/**
	 * Test purchase 0 bag
	 */
	@Test
	public void testPurchaseNoBag() {
		double expectedWeight = sc.getExpectedWeight();
		double expectedTotal = ic.getCheckoutTotal();
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: 0");
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 
		
		// all data should stay the same as before
		assertTrue(sc.getBagInStock() == capacity);
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase 1 bag
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseOneBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight();
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice();
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: 1");
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 

		assertTrue(sc.getBagInStock() == (capacity-1));
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase all bags in stock
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseAllBags() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*bagsInStock;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*bagsInStock;
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + bagsInStock);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  

		assertTrue(sc.getBagInStock() == 0);
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase number of bags more than the number of bags in stock
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseOverCapacityBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight();
		double expectedTotal = ic.getCheckoutTotal();
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + (bagsInStock + 1));
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  

		// no bag is dispensed
		assertTrue(notEnough);
		assertTrue(sc.getBagInStock() == bagsInStock);
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	 
	/**
	 * Test purchase multiple bags
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseMultipleBags() throws OverloadException {
		int numBag = 3;
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*numBag;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*numBag;
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + numBag);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  

		assertTrue(sc.getBagInStock() == (bagsInStock - numBag));
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase bags multiple times until there is no bag left
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseBagsMultipleTimes() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*bagsInStock;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*bagsInStock;
		
		// 1st time: purchase 3 bags
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + 3);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  
		
		// 2nd time: purchase capacity - 3 bags
		// there is no bag left after this purchase
		e = new ActionEvent(this, 0, "NUMBER_BAGS: " + (bagsInStock - 3));
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  

		assertTrue(sc.getBagInStock() == 0);
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase bags multiple times
	 * The last purchase asks for more bags than the number of bags in stock
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseBagsMultipleTimesOverCapacity() throws OverloadException {
		int numBag = 3;
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*numBag;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*numBag;
		
		// 1st time: purchase 3 bags
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + numBag);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 
		
		// 2nd time: purchase capacity - 3 + 1 bags
		// more than the number of bags left in the dispenser
		e = new ActionEvent(this, 0, "NUMBER_BAGS: " + (bagsInStock - numBag + 1));
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  

		assertTrue(notEnough);
		assertTrue(sc.getBagInStock() == bagsInStock-numBag);	//only dispensed the 1st purchase bags
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase bags when there is no bag in stock
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseBagsWhenEmpty() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*bagsInStock;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*bagsInStock;
		
		// 1st time: purchase all bags
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + bagsInStock);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);  
		
		// 2nd time: purchase 1 bag while there is no bag left
		e = new ActionEvent(this, 0, "NUMBER_BAGS: 1");
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);   

		assertTrue(outOfStock);
		assertTrue(sc.getBagInStock() == 0);	//only dispensed the 1st purchase bags
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test loading bags when the bag dispenser is empty
	 * @throws OverloadException if fails
	 */
	@Test
	public void testLoadBagsWhenEmpty(){
		// purchase all bags in stock
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + bagsInStock);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 
		assertTrue(sc.getBagInStock() == 0);
		
		// attendant loads bag
		e = new ActionEvent(this, 0, "addBag");
		sc.getAttendantControl().actionPerformed(e);
		assertTrue(sc.getBagInStock() == capacity);
	}
	
	/**
	 * Test loading bags when the bag dispenser is full
	 * @throws OverloadException if fails
	 */
	@Test
	public void testLoadBagsWhenFull() throws OverloadException{
		// purchase all bags in stock
		assertTrue(sc.getBagInStock() == bagsInStock);
		
		// attendant loads bag
		ActionEvent e = new ActionEvent(this, 0, "addBag");
		sc.getAttendantControl().actionPerformed(e);
		assertTrue(sc.getBagInStock() == bagsInStock);		// no bags added
	}
	
	/**
	 * Test loading bags when the bag dispenser is neither full nor empty
	 * @throws OverloadException if fails
	 */
	@Test
	public void testLoadBagsWhenNotFull(){
		// purchase a random number of bags in stock
		int rand = (int)Math.random()*(bagsInStock-2);
		int expected = sc.getBagInStock() - rand; 
		
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + rand);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 
		assertTrue(sc.getBagInStock() == expected);
		
		// attendant loads bags to full capacity again
		e = new ActionEvent(this, 0, "addBag");
		sc.getAttendantControl().actionPerformed(e);
		assertTrue(sc.getBagInStock() == capacity);
	}
	
	/**
	 * Test input the amount of bags wanted
	 * @throws OverloadException 
	 */
	@Test
	public void testInputNumBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*12;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*12;
		
		// input 1 
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: 1");
		bdc.actionPerformed(e);
		assertTrue(input.equals("1"));
		
		// type 2
		e = new ActionEvent(this, 0, "NUMBER_BAGS: " + 2);
		bdc.actionPerformed(e);
		assertTrue(input.equals("12"));		// input is updated to 2
		
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 
		
		assertTrue(sc.getBagInStock() == bagsInStock-12);	//dispensed the 12 bags
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test cancel input the number of purchase bags
	 * @throws OverloadException 
	 */
	@Test
	public void testCancelInputNumBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight();
		double expectedTotal = ic.getCheckoutTotal();
		
		// input 1
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: 1");
		bdc.actionPerformed(e);
		assertTrue(input.equals("1"));
		
		// cancel input
		e = new ActionEvent(this, 0, "cancel");
		bdc.actionPerformed(e);
		
		assertTrue(input.equals("")); 
		assertTrue(sc.getBagInStock() == bagsInStock);	// no bag has been dispensed 
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test correct the amount of bags inputing on screen
	 * Input a wrong number 12
	 * then remove 2 to make it 1
	 * Expected to dispense only 1 bag
	 * @throws OverloadException 
	 */
	@Test
	public void testCorrectInputNumBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight();
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice();
		
		// input 12
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: 12");
		bdc.actionPerformed(e);
		assertTrue(input.equals("12"));
		
		// click correct
		e = new ActionEvent(this, 0, "correct");
		bdc.actionPerformed(e);
		assertTrue(input.equals("1"));
		
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e); 
		
		assertTrue(input.equals(""));			// input is reset after submit
		assertTrue(sc.getBagInStock() == bagsInStock-1);	//dispensed the 12 bags
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
		
	}
	
	/**
	 * Test correct the amount of bags inputing on screen
	 * No input has been typed in
	 * then choose correct
	 * Expected input = ""
	 */
	@Test
	public void testCorrectInputNumBagWhileEmpty(){		
		ActionEvent e = new ActionEvent(this, 0, "correct");
		bdc.actionPerformed(e);
		assertTrue(input.equals(""));	
	}
	
	/**
	 * Test when there are not enough bags 
	 * and customer are willing to purchase all bags left
	 */
	@Test
	public void testDispenseRemaining() throws OverloadException {	
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*bagsInStock;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*bagsInStock;
		
		// customer purchased some bags
		// only 2 bags left
		ActionEvent e = new ActionEvent(this, 0, "NUMBER_BAGS: " + (sc.getBagInStock()-2));
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);
		
		// customer wants to purchase 3 more bags
		e = new ActionEvent(this, 0, "NUMBER_BAGS: " + 3);
		bdc.actionPerformed(e);
		e = new ActionEvent(this, 0, "submit");
		bdc.actionPerformed(e);
		assertTrue(notEnough);					// listener notifies not enough bag
		
		// customer decides to purchase all bags that are left instead
		e = new ActionEvent(this, 0, "dispense remaining");
		bdc.actionPerformed(e);
		
		assertTrue(sc.getBagInStock() == 0);	//all bags are dispensed
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	
	StationControlListener stationListener = new StationControlListener() {

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked) {}

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {}

		@Override
		public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {}

		@Override
		public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {}

		@Override
		public void paymentsHaveBeenEnabled(StationControl systemControl) {}

		@Override
		public void startMembershipCardInput(StationControl systemControl) {}

		@Override
		public void membershipCardInputFinished(StationControl systemControl) {}

		@Override
		public void membershipCardInputCanceled(StationControl systemControl, String reason) {}

		@Override
		public void initiatePinInput(StationControl systemControl, String kind) {}

		@Override
		public void triggerPanelBack(StationControl systemControl) {}

		@Override
		public void triggerInitialScreen(StationControl systemControl) {}

		@Override
		public void triggerPaymentWorkflow(StationControl systemControl) {}

		@Override
		public void triggerMembershipWorkflow(StationControl systemControl) {}

		@Override
		public void triggerPLUCodeWorkflow(StationControl systemControl) {}

		@Override
		public void triggerPurchaseBagsWorkflow(StationControl systemControl) {
			workflowStarted = true;
		}

		@Override
		public void noBagsInStock(StationControl systemControl) {
			outOfStock = true;
		}

		@Override
		public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
			notEnough = true;
		}

		@Override
		public void triggerBrowsingCatalog(StationControl systemControl) {}

		@Override
		public void triggerReceiptScreen(StationControl systemControl) {}
		
	};
	

	BagDispenserControlListener bagListener = new BagDispenserControlListener() {

		@Override
		public void numberFieldHasBeenUpdated(BagDispenserControl bdp, String numBag) {
			input = numBag;
		}
		
	};
	/**
	 * Check weight discrepancy between bagging area current weight and the expected weight
	 * @param expected the expected weight value for bagging area
	 * @return true if there is no weight discrepancy
	 * 		   false otherwise
	 * @throws OverloadException
	 */
	private boolean checkWeight(double expected) throws OverloadException {
		return (Math.abs(sc.station.baggingArea.getCurrentWeight() - expected) <= 1);
	}

}
