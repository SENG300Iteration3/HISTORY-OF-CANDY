package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;

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


    @Before
    public void setup() {
    	PowerGrid.engageUninterruptiblePowerSource();
    	
//    	FakeDataInitializer fdi = new FakeDataInitializer();
//    	fdi.addProductAndBarcodeData();
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
//    @Test (expected = InvalidArgumentSimulationException.class)
//    public void testRemoveLastBaggedItemNoItem() {
//    	ac.addListener(als);
//    	assertFalse(als.ini);
//    	ac.removeLastBaggedItem();
//    }
    
    @Test
    public void testActionPerformedNoBagging() {
    	ActionEvent e = new ActionEvent(this, 0, "no_bagging");
    	
    	ac.actionPerformed(e);
    	assertFalse(als.noBagging);
    }
    
//    @Test 
//    public void testRemoveLastBaggedItemWithItem() {
//    	ac.addListener(als);
//    	assertFalse(als.ini);
//    	Item wbi = new Item(235) {};
//		sc.station.scale.add(wbi);
//    	ac.removeLastBaggedItem();
//    	assertTrue(als.ini);
//    }
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
    
    // Capacity of storage is 1000 in DoItYourselfStation
    @Test
    public void testAdjustBanknoteForChange() throws SimulationException, TooMuchCashException {
//    	sc.station.banknoteStorage.getCapacity();
//    	ac.adjustBanknotesForChange();
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
    
    @Test (expected = NullPointerException.class)
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
    
    @Test (expected = OverloadException.class)
    public void testAddInkOverload() throws OverloadException {
    	ac.addListener(als);
    	ac.addInk();
    	ac.addInk();
    	ac.addInk();
    	ac.addInk();
    	ac.addInk();
    	ac.addInk();
    	
    }
    
    @Test (expected = OverloadException.class)
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
//    @Test
//    public void testApproveNoBaggingRequest() {
//    	ac.addListener(als);
//    	assertFalse(als.noBagging);
//    	ac.approveNoBaggingRequest();
//    	assertTrue(als.noBagging);
//    }

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
    	
    }
    

    public class AttendantListenerStub implements AttendantControlListener {
    	boolean attendantBags = false;
		boolean attendantUse = false;
    	boolean lowState = false;
    	boolean addPaper = false;
    	boolean addInk = false;
    	boolean banknoteLowState = false;
		public boolean noBagging = false;
		String testMsg = "";
		boolean ini = false;
    	
    	@Override
    	public void attendantApprovedBags(AttendantControl ac) {
    		attendantBags = true;
    	}

		public boolean getAttendantBags() {
    		return attendantBags;
    	}

		@Override
		public void attendantPreventUse(AttendantControl ac) { attendantUse = true; }

		public boolean getPreventUse() { return attendantUse; }

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
			banknoteLowState = true;
		}
    }






}
