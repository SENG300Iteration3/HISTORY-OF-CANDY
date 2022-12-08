package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;
import swing.screens.AttendantStationScreen;

public class AttendantStationScreenTest {
	
	FakeDataInitializer fdi;
	StationControl sc;
	AttendantStationScreen screen;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addCardData();
		fdi.addFakeAttendantLogin();
		fdi.addFakeMembers();
		fdi.addPLUCodedProduct();
		fdi.addProductAndBarcodeData();
		
		sc = new StationControl();
		
		screen = new AttendantStationScreen(sc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartUp() {
		sc.station.unplug();
		screen.getStartUpButton().doClick();
		assertTrue(sc.station.screen.isPluggedIn());
		assertTrue(sc.station.screen.isPoweredUp());
	}
	
	public class AttendantStationListenerStub implements AttendantControlListener {
		
		public boolean isLoggedIn = false;
		public boolean bagsApproved = false;
		public boolean permitUse = true;
		public boolean removeItemApproved = false;

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			permitUse = false;
			
		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			removeItemApproved = true;
			
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
		public void printerNotLowState() {
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
		public void addInkState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addPaperState() {
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
		public void itemBagged() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void initialState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void banknotesInStorageLowState() {
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
			this.isLoggedIn = isLoggedIn;
			
		}
		
	}

}
