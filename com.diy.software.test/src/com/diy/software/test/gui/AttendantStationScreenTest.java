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
	AttendantControlListenerStub aStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addCardData();
		fdi.addFakeAttendantLogin();
		fdi.addFakeMembers();
		fdi.addPLUCodedProduct();
		fdi.addProductAndBarcodeData();
		
		sc = new StationControl(fdi);
		
		screen = new AttendantStationScreen(sc);
		
		aStub = new AttendantControlListenerStub();
		sc.getAttendantControl().addListener(aStub);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartUpButton() {
		sc.station.unplug();
		screen.getStartUpButton().setEnabled(true);
		screen.getStartUpButton().doClick();
		assertTrue(aStub.startup);
	}
	
	@Test
	public void testShutdownButton() {
		screen.getShutDownButton().setEnabled(true);
		screen.getShutDownButton().doClick();
		assertTrue(aStub.shutdown);
	}
	
	@Test
	public void testPermitButton() {
		screen.getPermitButton().setEnabled(true);
		screen.getPermitButton().doClick();
		assertFalse(sc.station.handheldScanner.isDisabled());
		
	}
	
	@Test
	public void testPreventButton() {
		sc.station.handheldScanner.disable();
		screen.getPreventButton().setEnabled(true);
		screen.getPreventButton().doClick();
		assertTrue(sc.station.handheldScanner.isDisabled());
	}
	
	@Test
	public void testAddItemButton() {
		screen.getAddItemButton().doClick();
		assertTrue(aStub.triggerTextSearch);
	}
	
	@Test
	public void testRemoveItemButton() {
		
	}
	
	@Test
	public void testLogoutButton() {
		
	}
	
	@Test
	public void testApproveAddedBagsButton() {
		
	}
	
	public class AttendantControlListenerStub implements AttendantControlListener {
		
		public boolean isLoggedIn = false;
		public boolean bagsApproved = false;
		public boolean permitUse = true;
		public boolean removeItemApproved = false;
		public boolean initialState = false;
		public boolean attendantApprovedBags = false;
		public boolean shutdown = false;
		public boolean startup = false;
		public boolean triggerTextSearch = false;

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			attendantApprovedBags = true;
			
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
			initialState = true;
			
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
		public void loggedIn(boolean isLoggedIn) {
			this.isLoggedIn = isLoggedIn;
			
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
			triggerTextSearch = true;
			
		}

		@Override
		public void exitTextSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stationShutDown(AttendantControl ac) {
			shutdown = true;
			
		}

		@Override
		public void stationStartedUp(AttendantControl ac) {
			startup = true;
			
		}
		
	}

}
