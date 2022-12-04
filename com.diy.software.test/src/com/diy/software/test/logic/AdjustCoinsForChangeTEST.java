package com.diy.software.test.logic;

import static org.junit.Assert.assertEquals;

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
	
	
	@Before
	public void setup() throws SimulationException, TooMuchCashException {
		sc = new StationControl();
		cc = new CashControl(sc);
		ac = new AttendantControl(sc);
		
	
		testUnit = new CoinStorageUnit(50);
		
		sc.station.plugIn();
		sc.station.turnOn();
		
		testUnit.connect();
		testUnit.activate();
		
		lCoin = new Coin(Currency.getInstance("CAD"),100);
		tCoin = new Coin(Currency.getInstance("CAD"),200);
		qCoin = new Coin(Currency.getInstance("CAD"),25);
		dCoin = new Coin(Currency.getInstance("CAD"),10);
		nCoin = new Coin(Currency.getInstance("CAD"),5);
		
		
		for(int i = 0; i < 10; i++) {
			testUnit.load(lCoin,tCoin,qCoin,dCoin,nCoin);
		}
			
	
	}
	
	@After
	public void takeDown() {
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
		
		assertEquals(10,ac.countCoin(5, unloadedCoin));
		assertEquals(10,ac.countCoin(10, unloadedCoin));
		assertEquals(10,ac.countCoin(25, unloadedCoin));
		assertEquals(10,ac.countCoin(100, unloadedCoin));
		assertEquals(10,ac.countCoin(200, unloadedCoin));
	}
	
	
	
	
	//Stub for attendantControlListner
	class ACLS implements AttendantControlListener{

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addPaperState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addInkState() {
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
		public void coinIsLowState(CoinStorageUnit unit, int amount) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
