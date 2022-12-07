package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.ReceiptPrinterND;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

public class TestAttendantControl {

	AttendantControl ac;
	StationControl sc;
	AttendantListenerStub als;
	FakeDataInitializer fdi;
	SystemControlListenerStub scl;
	ReceiptPrinterND rp;
	ItemsControl ic;
  Currency currency;

	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();

		// FakeDataInitializer fdi = new FakeDataInitializer();
		// fdi.addProductAndBarcodeData();
		sc = new StationControl();
		ic = new ItemsControl(sc);
		ac = new AttendantControl(sc);
		als = new AttendantListenerStub();
		scl = new SystemControlListenerStub();
		rp = sc.station.printer;
		rp.register(ac);
		rp.plugIn();
		rp.turnOff();
		rp.turnOn();
		rp.enable();
    
    this.currency = Currency.getInstance("CAD");
	}

	@Test
	public void testAddListener() {
		als.attendantBags = false;
		ac.approveBagsAdded();
		assertFalse(als.attendantBags);

		ac.addListener(als);

		als.attendantBags = false;
		ac.approveBagsAdded();
		assertTrue(als.attendantBags);
	}

	@Test
	public void testRemoveListener() {
		ac.addListener(als);

		als.attendantBags = false;
		ac.approveBagsAdded();
		assertTrue(als.attendantBags);

		ac.removeListener(als);

		als.attendantBags = false;
		ac.approveBagsAdded();
		assertFalse(als.attendantBags);
	}

	@Test
	public void testLoginPass() {
		AttendantControl.logins.add("password");
		ac.addListener(als);
		als.isLoggedIn = false;
		ac.login("password");
		assertTrue(als.isLoggedIn);
	}

	@Test
	public void testLoginWrongPassword() {
		AttendantControl.logins.add("password");
		ac.addListener(als);
		als.isLoggedIn = true;
		ac.login("pass");
		assertFalse(als.isLoggedIn);
	}

	@Test
	public void testLoginWrongNull() {
		AttendantControl.logins.add("password");
		ac.addListener(als);
		als.isLoggedIn = true;
		ac.login(null);
		assertFalse(als.isLoggedIn);
	}

	@Test
	public void testAddPaper() throws OverloadException {
		ac.addListener(als);
		assertFalse(als.lowState);
		ac.addPaper();
		assertTrue(als.lowState);
	}

	@Test
	public void testAddInk() throws OverloadException {
		ac.addListener(als);
		assertFalse(als.lowState);
		ac.addInk();
		assertTrue(als.lowState);
	}

	@Test
	public void testUpdateWeightDescrepancyMessage() {
		ac.addListener(als);
		assertFalse(als.testMsg.equals("test"));
		ac.updateWeightDescrepancyMessage("test");
		assertTrue(als.testMsg.equals("test"));
	}

	// FIXME: Need to rewrite - Anh
	// @Test (expected = InvalidArgumentSimulationException.class)
	// public void testRemoveLastBaggedItemNoItem() {
	// ac.addListener(als);
	// assertFalse(als.ini);
	// ac.removeLastBaggedItem();
	// }

	@Test
	public void testActionPerformedNoBagging() {
		ActionEvent e = new ActionEvent(this, 0, "no_bagging");

		ac.actionPerformed(e);
		assertFalse(als.noBagging);
	}

	// @Test
	// public void testRemoveLastBaggedItemWithItem() {
	// ac.addListener(als);
	// assertFalse(als.ini);
	// Item wbi = new Item(235) {};
	// sc.station.scale.add(wbi);
	// ac.removeLastBaggedItem();
	// assertTrue(als.ini);
	// }
	//

	@Test
	public void testLowInk() {
		ac.addListener(als);
		assertFalse(als.addInk);
		ac.lowInk(rp);
		assertTrue(als.addInk);

	}

	@Test
	public void testNoInk() {
		ac.addListener(als);
		assertFalse(als.addInk);
		ac.outOfInk(rp);
		assertTrue(als.addInk);

	}

	@Test
	public void testLowPaper() {
		ac.addListener(als);
		assertFalse(als.addPaper);
		ac.lowPaper(rp);
		assertTrue(als.addPaper);
	}

	@Test
	public void testOutOfPaper() {
		ac.addListener(als);
		assertFalse(als.addPaper);
		ac.outOfPaper(rp);
		assertTrue(als.addPaper);
	}

	@Test
	public void testApproveBagsStationBlocked() {
		sc.listeners.add(scl);
		ac.addListener(als);
		sc.blockStation();
		assertFalse(als.getAttendantBags());
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());

		ac.approveBagsAdded();

		assertTrue(als.getAttendantBags());
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
	}

	@Test
	public void testApproveBagsStationUnblocked() {
		sc.unblockStation();
		ac.addListener(als);
		assertFalse(als.getAttendantBags());
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());

		ac.approveBagsAdded();

		assertTrue(als.getAttendantBags());
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
	}

	@Test
	public void testActionPerformedNullEventName() {
		ActionEvent e = new ActionEvent(this, 0, null);
		ac.addListener(als);
		sc.blockStation();
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertFalse(als.getAttendantBags());

		ac.actionPerformed(e);

		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertFalse(als.getAttendantBags());
	}

	@Test
	public void testActionPerformedApproveAdded() {
		ActionEvent e = new ActionEvent(this, 0, "approve added bags");
		sc.listeners.add(scl);
		sc.blockStation();
		ac.addListener(als);

		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertFalse(als.getAttendantBags());

		ac.actionPerformed(e);

		assertTrue(als.getAttendantBags());
		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
	}

	@Test(expected = NullPointerException.class)
	public void testActionPerformedNullAction() {
		ActionEvent e = null;
		sc.blockStation();
		ac.addListener(als);
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertFalse(als.getAttendantBags());
		ac.actionPerformed(e);
	}

	@Test
	public void testActionPerformedDefault() {
		ActionEvent e = new ActionEvent(this, 0, "");
		sc.blockStation();
		ac.addListener(als);
		assertFalse(als.getAttendantBags());

		ac.actionPerformed(e);

		assertFalse(als.getAttendantBags());
	}

	@Test(expected = OverloadException.class)
	public void testAddInkOverload() throws OverloadException {
		ac.addListener(als);
		ac.addInk();
		ac.addInk();
		ac.addInk();
		ac.addInk();
		ac.addInk();
		ac.addInk();

	}

	@Test(expected = OverloadException.class)
	public void testAddPaperOverload() throws OverloadException {
		ac.addListener(als);
		ac.addPaper();
		ac.addPaper();
		ac.addPaper();
	}

	@Test
	public void testActionPerformedAddInk() {
		ActionEvent e = new ActionEvent(this, 0, "addInk");
		ac.addListener(als);
		assertFalse(als.lowState);
		ac.actionPerformed(e);
		assertTrue(als.lowState);
	}

	@Test
	public void testActionPerformedAddPaper() {
		ActionEvent e = new ActionEvent(this, 0, "addPaper");
		ac.addListener(als);
		assertFalse(als.lowState);
		ac.actionPerformed(e);
		assertTrue(als.lowState);
	}

	// FIXME: Need to rewrite - Anh
	// @Test
	// public void testApproveNoBaggingRequest() {
	// ac.addListener(als);
	// assertFalse(als.noBagging);
	// ac.approveNoBaggingRequest();
	// assertTrue(als.noBagging);
	// }
  
  /*
     * Test adjust banknote for change when storage is empty
     * Should fill up storage completely
     * 
     * Capacity of storage is 1000 in DoItYourselfStation
     */
    @Test
    public void testAdjustBanknoteForChangeEmptyStorage() throws SimulationException, TooMuchCashException {
    	ac.addListener(als);
    	assertFalse(als.banknoteAdjusted);
    	ac.adjustBanknotesForChange();
    	assertTrue(als.banknoteAdjusted);
    	ac.loadBanknotesToStorage(this.sc.station.banknoteStorage);
    	assertEquals(1000, sc.station.banknoteStorage.getBanknoteCount());
    }
    
    /*
     * Test adjust banknote for change when storage is already full and not below a threshold
     * Loads 500 banknotes to storage. Should not adjust for change
     */
    @Test
    public void testAdjustBanknoteForChangeFullStorage() throws SimulationException, TooMuchCashException {
    	ac.addListener(als);
    	assertFalse(als.banknoteAdjusted);
    	for (int i = 0; i < 100; i++) {
    		sc.station.banknoteStorage.load(new Banknote(currency, 5));
    		sc.station.banknoteStorage.load(new Banknote(currency, 10));
    		sc.station.banknoteStorage.load(new Banknote(currency, 20));
    		sc.station.banknoteStorage.load(new Banknote(currency, 50));
    		sc.station.banknoteStorage.load(new Banknote(currency, 100));
    	}
    	assertEquals(500, sc.station.banknoteStorage.getBanknoteCount());
    	ac.adjustBanknotesForChange();
    	assertFalse(sc.getCashControl().banknotesInStorageLow(sc.station.banknoteStorage));
    	assertFalse(als.banknoteAdjusted);
    }
    
    /*
     * Test adjust banknote for change when amount of banknotes is exactly at threshold
     * Loads 100 banknotes to storage (threshold set to 1/20 of storage (i.e. 50))
     * More banknotes should be loaded
     */
    @Test
    public void testAdjustBanknoteForChangeAtThreshold() throws SimulationException, TooMuchCashException {
    	ac.addListener(als);
    	assertFalse(als.banknoteAdjusted);
    	for (int i = 0; i < 10; i++) {
    		sc.station.banknoteStorage.load(new Banknote(currency, 5));
    		sc.station.banknoteStorage.load(new Banknote(currency, 10));
    		sc.station.banknoteStorage.load(new Banknote(currency, 20));
    		sc.station.banknoteStorage.load(new Banknote(currency, 50));
    		sc.station.banknoteStorage.load(new Banknote(currency, 100));
    	}
    	assertEquals(50, sc.station.banknoteStorage.getBanknoteCount());
    	ac.adjustBanknotesForChange();
    	assertTrue(als.banknoteAdjusted);
    	ac.loadBanknotesToStorage(this.sc.station.banknoteStorage);
    	assertEquals(1000, sc.station.banknoteStorage.getBanknoteCount());
    }
    
    /*
     * Test action for adjusting banknote storage for change
     * The storage is initially empty and should be reloaded
     */
    @Test
    public void testActionPerformedAdjustBanknote() {
    	ActionEvent e = new ActionEvent(this, 0, "adjustBanknotesForChange");
    	ac.addListener(als);
    	assertFalse(als.banknoteAdjusted);
    	ac.actionPerformed(e);
    	assertTrue(als.banknoteAdjusted);
    }

	@Test
	public void testActionPerformedPreventUse() {
		ActionEvent e = new ActionEvent(this, 0, "prevent_use");
		sc.listeners.add(scl);
		ac.addListener(als);

		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertFalse(als.getPreventUse());

		ac.actionPerformed(e);

		assertTrue(als.getPreventUse());
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
	}

	@Test
	public void testActionPerformedPermitUse() {
		ActionEvent e = new ActionEvent(this, 0, "permit_use");
		sc.listeners.add(scl);
		ac.addListener(als);

		sc.blockStation();
		assertTrue(sc.station.handheldScanner.isDisabled());
		assertTrue(sc.station.cardReader.isDisabled());
		assertFalse(als.stationPermitted);

		ac.actionPerformed(e);

		assertFalse(sc.station.handheldScanner.isDisabled());
		assertFalse(sc.station.cardReader.isDisabled());
		assertTrue(als.stationPermitted);
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		rp.disable();
		rp.turnOff();
		rp.unplug();
	}

	public class SystemControlListenerStub implements StationControlListener {

		boolean locked = false;

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked) {
			locked = isLocked;

		}

		@Override
		public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {
			// TODO Auto-generated method stub

		}

		@Override
		public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {
			// TODO Auto-generated method stub

		}

		@Override
		public void paymentsHaveBeenEnabled(StationControl systemControl) {
			// TODO Auto-generated method stub

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

	}

	public class AttendantListenerStub implements AttendantControlListener {
		boolean attendantBags = false;
		boolean attendantUse = false;
		boolean banknoteAdjusted = false;
		boolean lowState = false;
		boolean addPaper = false;
		boolean addInk = false;
		public boolean noBagging = false;
		String testMsg = "";
		boolean ini = false;
		boolean stationPermitted = false;
		boolean isLoggedIn = false;

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			attendantBags = true;
		}

		public boolean getAttendantBags() {
			return attendantBags;
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			attendantUse = true;
		}

		public boolean getPreventUse() {
			return attendantUse;
		}

		@Override
		public void addPaperState() {
			addPaper = true;

		}

		@Override
		public void addInkState() {
			addInk = true;

		}

		@Override
		public void printerNotLowState() {
			lowState = true;

		}

		@Override
		public void signalWeightDescrepancy(String updateMessage) {
			testMsg = updateMessage;

		}

		@Override
		public void initialState() {
			ini = true;

		}

		@Override
		public void noBagRequest() {
			noBagging = true;
		}

		@Override
		public void banknotesInStorageLowState() {
			banknoteAdjusted = true;
		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {
			stationPermitted = true;

		}

		@Override
		public void loggedIn(boolean isLoggedIn) {
			this.isLoggedIn = isLoggedIn;

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
		public void coinIsLowState(CoinStorageUnit unit, int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemBagged() {
			// TODO Auto-generated method stub
			
		}
	}
}
