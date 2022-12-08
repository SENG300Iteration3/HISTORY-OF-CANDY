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
import swing.panels.AttendantActionsPanel;
import swing.panes.AttendantActionsPane;
import swing.screens.AttendantStationScreen;

public class AttendantActionsPanelTest {
	
	FakeDataInitializer fdi;
	StationControl sc;
	AttendantActionsPanel screen;
	AttendantStationScreen aScreen;
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
		
		screen = new AttendantActionsPanel(sc);
		aScreen = new AttendantStationScreen(sc);
		
		aStub = new AttendantControlListenerStub();
		sc.getAttendantControl().addListener(aStub);
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitialState() {
		assertTrue(screen.getInkButton().isEnabled());
		assertTrue(screen.getPaperButton().isEnabled());
		assertFalse(screen.getBagDispenserButton().isEnabled());
		assertFalse(screen.getCoinButton().isEnabled());
		assertFalse(screen.getBanknoteButton().isEnabled());
	}
	
	@Test
	public void testPreventButton() {
		aScreen.getPreventButton().doClick();
		assertTrue(aStub.stationBlocked);
		assertTrue(screen.getInkButton().isEnabled());
		assertTrue(screen.getPaperButton().isEnabled());
		assertTrue(screen.getBagDispenserButton().isEnabled());
		assertTrue(screen.getCoinButton().isEnabled());
		assertTrue(screen.getBanknoteButton().isEnabled());
	}
	
	@Test
	public void testPermitButton() {
		aScreen.getPreventButton().doClick();
		aScreen.getPermitButton().doClick();
		assertFalse(aStub.stationBlocked);
		assertFalse(screen.getInkButton().isEnabled());
		assertFalse(screen.getPaperButton().isEnabled());
		assertFalse(screen.getBagDispenserButton().isEnabled());
		assertFalse(screen.getCoinButton().isEnabled());
		assertFalse(screen.getBanknoteButton().isEnabled());
	}
	
	@Test
	public void testInkButton() {
		aScreen.getPreventButton().doClick();
		screen.getInkButton().doClick();
		if (aStub.inkOverload) {
			assertTrue(aStub.inkAdded);
			assertFalse(screen.getInkButton().isEnabled());
		} else {
			assertFalse(aStub.inkAdded);
			assertTrue(screen.getInkButton().isEnabled());
		}
	}
	
	@Test
	public void testPaperButton() {
		aScreen.getPreventButton().doClick();
		screen.getPaperButton().doClick();
		if (aStub.paperOverload) {
			assertTrue(aStub.paperAdded);
			assertFalse(screen.getPaperButton().isEnabled());
		} else {
			assertFalse(aStub.paperAdded);
			assertTrue(screen.getPaperButton().isEnabled());
		}
	}
	
	@Test
	public void testBagButton() {
		int before = sc.getBagInStock();
		
		aScreen.getPreventButton().doClick();
		screen.getBagDispenserButton().doClick();
		int after = sc.getBagInStock();
		
		assertTrue(after > before);
	}
	
	@Test
	public void testCoinButton() {
		aScreen.getPreventButton().doClick();
		screen.getCoinButton().doClick();
		assertTrue(aStub.coinsAdded);
	}
	 
	@Test
	public void testBanknoteButton() {
		aScreen.getPreventButton().doClick();
		screen.getBanknoteButton().doClick();
		assertTrue(aStub.banknotesAdded);
	}
	
	public class AttendantControlListenerStub implements AttendantControlListener {
		
		public boolean inkAdded = false;
		public boolean paperAdded = false;
		public boolean coinsAdded = false;
		public boolean banknotesAdded = false;
		public boolean stationBlocked = false;
		public boolean inkOverload = false;
		public boolean paperOverload = false;
		
		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			stationBlocked = true;
		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			
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
			
		}

		@Override
		public void banknotesInStorageLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {
			// TODO Auto-generated method stub
			stationBlocked = false;
		}

		@Override
		public void coinIsLowState(int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void loggedIn(boolean isLoggedIn) {
			
		}

		@Override
		public void printerNotLowInkState() {
			// TODO Auto-generated method stub
			inkAdded = true;
		}

		@Override
		public void printerNotLowPaperState() {
			// TODO Auto-generated method stub
			paperAdded = true;
		}

		@Override
		public void addTooMuchInkState() {
			// TODO Auto-generated method stub
			inkOverload = true;
		}

		@Override
		public void addTooMuchPaperState() {
			// TODO Auto-generated method stub
			paperOverload = true;
		}

		@Override
		public void banknotesNotLowState() {
			// TODO Auto-generated method stub
			banknotesAdded = true;
		}

		@Override
		public void coinsNotLowState() {
			// TODO Auto-generated method stub
			coinsAdded = true;
		}

		@Override
		public void triggerItemSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exitTextSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
