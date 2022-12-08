package com.diy.software.test.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.coin.Coin;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.SimulationException;

public class AdjustCoinsForChangeTEST {
	StationControl sc;
	CashControl cc;
	AttendantControl ac;
	CoinStorageUnit testUnit;
	Coin lCoin;
	Coin tCoin;
	Coin qCoin;
	Coin dCoin;
	Coin nCoin;
	ACLS acls;
	
	
	@Before
	public void setup() throws SimulationException, TooMuchCashException {
		PowerGrid.engageUninterruptiblePowerSource();
		
		acls = new ACLS();
		sc = new StationControl();
		cc = new CashControl(sc);
		ac = new AttendantControl(sc);
		
		ac.addListener(acls);
		
		
		sc.station.plugIn();
		sc.station.turnOn();
		
		testUnit = sc.station.coinStorage;
		
		
		lCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(1.0));
		tCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(2.0));
		qCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(0.25));
		dCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(0.1));
		nCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(0.05));
		
		
		for(int i = 0; i < 10; i++) {
			testUnit.load(lCoin,tCoin,qCoin,dCoin,nCoin);
		}
			
	
	}
	
	@After
	public void takeDown() {
		acls = null;
		sc = null;
		cc = null;
		ac = null;
		testUnit = null;
		lCoin = null;
		tCoin = null;
		qCoin = null;
		dCoin = null;
		nCoin = null;
	}
	
	@Test
	/**
	 * This test will test if countCoin functions as expected
	 * Fails if the return value does not equal to 10
	 * No exceptions expected
	 */
	
	public void countCoinTest() {
		
		List<Coin> unloadedCoin = testUnit.unload();
		
		assertEquals(10,ac.countCoin(nCoin, unloadedCoin));
		assertEquals(10,ac.countCoin(dCoin, unloadedCoin));
		assertEquals(10,ac.countCoin(qCoin, unloadedCoin));
		assertEquals(10,ac.countCoin(lCoin, unloadedCoin));
		assertEquals(10,ac.countCoin(tCoin, unloadedCoin));
	}
	
	@Test
	/**
	 * This test will test if adjustCoinForChange functions as expected
	 * Fails if the the loaded unit is not filled up to the max
	 * No exception expected
	 */
	public void adjustCoinForChangeSuccessTest() throws SimulationException, TooMuchCashException {
		
		ac.adjustCoinsForChange(testUnit.getCapacity());
		
		assertEquals(testUnit.getCapacity(), testUnit.getCoinCount());
	}
	
	@Test
	/**
	 * This test will test if adjustCoinForChange functions as expected
	 * Fails if no TooMuchCashException was thrown
	 * TooMuchCashException expected
	 */
	public void adjustCoinForChangeTooMuchCashTest() {
		try {
			ac.adjustCoinsForChange(testUnit.getCapacity() + 20);
			fail("TooMuchCashException should have been thrown");
		} catch (TooMuchCashException e) {
			assertEquals(0,0);
		}
		
	}
	
	@Test
	/**
	 * This test will test if notifyListenerCoinForChange works as expected
	 * Fails if coinIsLowCalled is false
	 * No exception expected
	 */
	public void notifyListenerAdjustCoinForChangeTest() throws SimulationException, TooMuchCashException {
		sc.station.coinStorage.unload();
		assertFalse(acls.coinIsLowCalled);
		ac.notifyListenerAdjustCoinForChange();
		assertTrue(acls.coinIsLowCalled);
	}
	
	@Test
	/**
	 * This test will test if notifyListenerAdjustCoinForChange works as expected
	 * Fails if coinIsLowCalled is true
	 * Test the case where the storage is currently full
	 * No exception expected
	 */
	public void notifyListenerAdjustCoinForChangeFulLTest() throws SimulationException, TooMuchCashException {
		assertFalse(acls.coinIsLowCalled);
		ac.adjustCoinsForChange(testUnit.getCapacity());
		ac.notifyListenerAdjustCoinForChange();
		assertFalse(acls.coinIsLowCalled);
	}
	
	@Test
	/**
	 * This test will test if coinInStorageLow works as expected
	 * Fails if the return value is false
	 * Test the case where a storage is low
	 * no exceptions expected
	 */
	public void coinInStorageLowLowCaseTest() {
		testUnit.unload();
		assertTrue(cc.coinInStorageLow(testUnit));
	}
	
	@Test
	/**
	 * This test will test if coinInStorageLow works as expected
	 * Fails if the return value is true
	 * Test the case where a storage is not low
	 * No exceptions expected
	 */
	public void coinInStorageLowFullCaseTest() throws SimulationException, TooMuchCashException {
		ac.adjustCoinsForChange(testUnit.getCapacity());
		assertFalse(cc.coinInStorageLow(testUnit));
	}
	
	
	
	
	//Stub for attendantControlListner
	class ACLS implements AttendantControlListener{
		
		boolean coinIsLowCalled = false;
		
		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void printerNotLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void signalWeightDescrepancy(String updateMessage) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noBagRequest() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void initialState() {
			// TODO Auto-generated method stub
			
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
		public void itemBagged() {
			// TODO Auto-generated method stub
			
		}
		

		@Override
		public void banknotesInStorageLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinIsLowState(int amount) {
			coinIsLowCalled = true;
			
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
		public void attendantApprovedItemRemoval(AttendantControl bc) {
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
		public void addTooMuchInkState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addTooMuchPaperState() {
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

		@Override
		public void stationShutDown(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stationStartedUp(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
