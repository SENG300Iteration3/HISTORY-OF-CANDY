package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.BagDispenserControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
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
	boolean notEnough;
	boolean outOfStock;
	
	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fakeData = new FakeDataInitializer();
		sc = new StationControl(fakeData);
		
		bdc = sc.getBagDispenserControl();
		ic = sc.getItemsControl();
		
		sc.register(theListener);
		bag = new ReusableBag();
		notEnough = false;
		outOfStock = false;
		capacity = sc.station.reusableBagDispenser.getCapacity();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test purchase 0 bag
	 */
	@Test
	public void testPurchaseNoBag() {
		double expectedWeight = sc.getExpectedWeight();
		double expectedTotal = ic.getCheckoutTotal();
		
		bdc.setNumBag(0);
		bdc.checkBagInStock();
		
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
		
		bdc.setNumBag(1);
		bdc.checkBagInStock();

		assertTrue(sc.getBagInStock() == (capacity-1));
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase number of bags = bagDispenser.capacity
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseCapacityBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*capacity;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*capacity;
		
		bdc.setNumBag(capacity);	
		bdc.checkBagInStock();

		assertTrue(sc.getBagInStock() == 0);
		assertTrue(sc.getExpectedWeight() == expectedWeight);
		assertTrue(checkWeight(expectedWeight));
		assertTrue(ic.getCheckoutTotal() == expectedTotal);
	}
	
	/**
	 * Test purchase number of bags = bagDispenser.capacity
	 * @throws OverloadException 
	 */
	@Test
	public void testPurchaseOverCapacityBag() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight();
		double expectedTotal = ic.getCheckoutTotal();
		
		bdc.setNumBag(capacity + 1);
		bdc.checkBagInStock();

		// no bag is dispensed
		assertTrue(notEnough);
		assertTrue(sc.getBagInStock() == capacity);
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
		
		bdc.setNumBag(numBag);
		bdc.checkBagInStock();

		assertTrue(sc.getBagInStock() == capacity - numBag);
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
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*capacity;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*capacity;
		
		// 1st time: purchase 3 bags
		bdc.setNumBag(3);
		bdc.checkBagInStock();
		
		// 2nd time: purchase capacity - 3 bags
		// there is no bag left after this purchase
		bdc.setNumBag(capacity - 3);
		bdc.checkBagInStock();

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
		bdc.setNumBag(numBag);
		bdc.checkBagInStock();
		
		// 2nd time: purchase capacity - 3 bags
		// there is no bag left after this purchase
		bdc.setNumBag(capacity - numBag + 1);
		bdc.checkBagInStock();

		assertTrue(notEnough);
		assertTrue(sc.getBagInStock() == capacity-numBag);	//only dispensed the 1st purchase bags
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
	public void testPurchaseBagsWhenEmpty() throws OverloadException {
		double expectedWeight = sc.getExpectedWeight() + bag.getWeight()*capacity;
		double expectedTotal = ic.getCheckoutTotal() + fakeData.getReusableBagPrice()*capacity;
		
		// 1st time: purchase 3 bags
		bdc.setNumBag(capacity);
		bdc.checkBagInStock();
		
		// 2nd time: purchase capacity - 3 bags
		// there is no bag left after this purchase
		bdc.setNumBag(1);
		bdc.checkBagInStock();

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
		bdc.setNumBag(sc.getBagInStock());
		bdc.checkBagInStock();
		assertTrue(sc.getBagInStock() == 0);
		
		// load
		sc.loadBags();
		assertTrue(sc.getBagInStock() == capacity);
	}
	
	/**
	 * Test loading bags when the bag dispenser is full
	 * @throws OverloadException if fails
	 */
	@Test
	public void testLoadBagsWhenFull() throws OverloadException{
		// purchase all bags in stock
		assertTrue(sc.getBagInStock() == capacity);
		
		// load
		sc.loadBags();
		assertTrue(sc.getBagInStock() == capacity);
	}
	
	/**
	 * Test loading bags when the bag dispenser is neither full nor empty
	 * @throws OverloadException if fails
	 */
	@Test
	public void testLoadBagsWhenNotFull(){
		// purchase a random number of bags in stock
		int rand = (int)Math.random()*(capacity-2);
		int expected = sc.getBagInStock() - rand;
		bdc.setNumBag(rand);
		bdc.checkBagInStock();
		assertTrue(sc.getBagInStock() == expected);
		
		// load bags to full capacity again
		sc.loadBags();
		assertTrue(sc.getBagInStock() == capacity);
	}
	
	StationControlListener theListener = new StationControlListener() {

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
		public void triggerPurchaseBagsWorkflow(StationControl systemControl) {}

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
