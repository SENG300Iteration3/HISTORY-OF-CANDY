//NOTE: also testing receipt related methods in attendant control

package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;

import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.jimmyselectronics.abagnale.ReceiptPrinterND;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.unitedbankingservices.coin.CoinStorageUnit;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.EmptyException;
import com.jimmyselectronics.OverloadException;

import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.ReceiptControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.StationControlListener;
import com.diy.software.listeners.ReceiptControlListener;
import com.diy.software.util.Tuple;

import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;


public class ReceiptControlTest {
	StationControl sc;
    AttendantControl ac;
    ReceiptControl rc;
    StationControlListenerStub scl;
    AttendantControlListenerStub acl;
    ReceiptControlListenerStub rcl;
    ReceiptPrinterND rp;

   
	@Before
	public void setup() throws EmptyException, OverloadException {
		PowerGrid.engageUninterruptiblePowerSource();
		
		sc = new StationControl();
		ac = sc.getAttendantControl();
		rc = sc.getReceiptControl();
		scl = new StationControlListenerStub();
    	acl = new AttendantControlListenerStub();
		rcl = new ReceiptControlListenerStub();
		rp = sc.station.printer;
		rp.register(ac);
		rp.register(acl);
		rp.register(rc);
		rp.plugIn();
		rp.turnOff();
		rp.turnOn();
		rp.enable();
		
		ac.addListener(acl);
		rc.addListener(rcl);
		
		/*
		 * Clearing contents of the printer
		 * (dependent on the ink/paper added in station control)
		 */
		sc.getReceiptControl().printReceipt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n");
		
		/*
		 * Due to limits in hardware out of ink and out of paper will only 
		 * register when ink/paper has been added and then removed, meaning paper and ink
		 * must be added at the start
		 * 
		 * must use the attendant station to add paper/ink in order for
		 * low paper and low ink to work
		 */
        ac.addInk(1);
        ac.addPaper(1);
	}
	
	@After
	public void teardown() {
		ac.removeListener(acl);
	}
	
	/*
	 * Tests out of ink
	 */
	@Test
	public void testOutOfInk() {
    	sc.getReceiptControl().printReceipt("a");
    	assertTrue(acl.outOfInk);
	}
	
	/*
	 * Tests out of paper
	 */
	@Test
	public void testOutOfPaper() {
    	sc.getReceiptControl().printReceipt("\n");
    	assertTrue(acl.outOfPaper);
	}
	
	/*
	 * Tests adding ink
	 */
	@Test
	public void testAddInk() throws EmptyException, OverloadException {
    	sc.getReceiptControl().printReceipt("a");
    	assertTrue(acl.outOfInk);
        ac.addInk(1);
    	assertFalse(acl.outOfInk);
	}
	
	/*
	 * Tests adding paper
	 */
	@Test
	public void testAddPaper() throws EmptyException, OverloadException {
    	sc.getReceiptControl().printReceipt("\n");
    	assertTrue(acl.outOfPaper);
        ac.addPaper(1);
    	assertFalse(acl.outOfPaper);
	}
	
	/*
	 * Tests low ink
	 */
	@Test
	public void testLowInk() {
		ac.addInk(1);
		sc.getReceiptControl().printReceipt("a");
		updateLowInkAndPaper();
		assertFalse(acl.outOfInk);
		assertTrue(acl.lowInk);
    	sc.getReceiptControl().printReceipt("a");
    	updateLowInkAndPaper();
    	assertTrue(acl.outOfInk);
    	assertTrue(acl.lowInk);
	}
	
	/*
	 * Tests low paper
	 */
	@Test
	public void testLowPaper() {
		ac.addPaper(1);
		sc.getReceiptControl().printReceipt("\n");
		updateLowInkAndPaper();
		assertFalse(acl.outOfPaper);
		assertTrue(acl.lowPaper);
    	sc.getReceiptControl().printReceipt("\n");
    	updateLowInkAndPaper();
    	assertTrue(acl.outOfPaper);
    	assertTrue(acl.lowPaper);
	}
	
	/*
	 * Tests low ink when printing something that crosses the threshold 
	 * between not being low on ink and being low on ink
	 */
	@Test
	public void testsLowInkThreshold() {
		ac.addInk(sc.getReceiptControl().inkLowThreshold);
		updateLowInkAndPaper();
		assertFalse(acl.lowInk);
		sc.getReceiptControl().printReceipt("a");
		updateLowInkAndPaper();
		assertFalse(acl.lowInk);
    	sc.getReceiptControl().printReceipt("a");
    	updateLowInkAndPaper();
    	assertTrue(acl.lowInk);
	}
	
	/*
	 * Tests low ink when printing something that crosses the threshold 
	 * between not being low on paper and being low on paper
	 */
	@Test
	public void testLowPaperThreshold() {
		ac.addPaper(sc.getReceiptControl().paperLowThreshold);
		updateLowInkAndPaper();
		assertFalse(acl.lowPaper);
		sc.getReceiptControl().printReceipt("\n");
		updateLowInkAndPaper();
		assertFalse(acl.lowPaper);
    	sc.getReceiptControl().printReceipt("\n");
    	updateLowInkAndPaper();
    	assertTrue(acl.lowPaper);
	}
	
    /*
     *  Test EmptyException thrown when printing when out of ink
     */
    @Test
    public void testPrintingWhenOutOfInk() throws EmptyException, OverloadException{
    	sc.getReceiptControl().printReceipt("a");
    	sc.getReceiptControl().printReceipt("a");
    	assertTrue(rcl.takeIncompleteReceipt);
    	assertFalse(rcl.takeReceipt);
    }
    
    /*
     *  Test EmptyException thrown when printing when out of paper
     */
    @Test (expected = EmptyException.class)
    public void testPrintingWhenOutOfPaper() throws EmptyException, OverloadException{
    }
    
    /*
     *  Test that printItems prints items properly when no items are bought
     */
    @Test
    public void testPrintItemsNoItems(){
    	
    }
    
    /*
     *  Test that printItems prints items properly when one item is bought
     */
    @Test
    public void testPrintItemsOneItem(){
    	
    }
    
    /*
     *  Test that printItems prints items properly when two items are bought
     */
    @Test
    public void testPrintItemsTwoItems(){
    	
    }
    
    /*
     *  Test that TotalCost prints items properly when no items are bought
     */
    @Test
    public void testPrintTotalCostNoItems(){
    	
    }
    
    /*
     *  Test that TotalCost prints items properly when one item is bought
     */
    @Test
    public void testPrintTotalCostOneItem(){
    	
    }
    
    /*
     *  Test that TotalCost prints items properly when two items are bought
     */
    @Test
    public void testPrintTotalCostTwoItems(){
    	
    }
    
    /*
     *  Test that printMembership with membership number
     */
    @Test
    public void testPrintMembershipWithMembership(){
    	
    }
    
    /*
     *  Test that printMembership with no membership number
     */
    @Test
    public void testPrintMembershipNoMembership(){
    	
    }
    
    /*
     *  Test print date time
     */
    @Test
    public void TestPrintDateTime(){
    	
    }
    
    /*
     *  Test print thank you message
     */
    @Test
    public void testPrintThankyouMsg(){
    	
    }
    
    /*
     *  Test that the receipt is cut after printing a receipt properly
     */
    @Test
    public void testCutReceipt(){
    	
    }
    
    /*
     *  Test printFullReceipt (using action performed)
     */
    @Test
    public void testPrintFullReceipt(){
    	
    }
    
    /*
     *  Test printFullReceipt running out of ink part way though
     */
    @Test
    public void testPrintFullReceiptOutOfInk(){
    	
    }
    
    /*
     *  Test printFullReceipt running out of paper part way though
     */
    @Test
    public void testPrintFullReceiptOutOfPaper(){
    	
    }
    
    /*
     *  Test printFullReceipt running out of ink on last possible character
     */
    @Test
    public void testPrintFullReceiptOutOfInkLastChar(){
    	
    }
    
    /*
     *  Test that the receipt is cut after attempting to print a receipt but running out of paper
     */
    @Test
    public void testCutReceiptOutOfInk(){
    	
    }
    
    /*
     *  Test that the receipt is cut after attempting to print a receipt but running out of ink
     */
    @Test
    public void testCutReceiptOutOfPaper(){
    	
    }
    
    /*
     *  Test the taking of receipt (using action performed)
     */
    @Test
    public void testTakeReceipt(){
    	
    }
    
    /*
     *  Test to make sure state resets
     *  TODO only keep is reset state is not removed
     */
    @Test
    public void testResetState(){
    	
    }
    
    /*
     *  Test to make a completed receipt removal is called
     */
    @Test
    public void testremovedCompleteReceipt(){
    	
    }
    
    /*
     * 	TODO add
     *  Test that a new line is added when 60+ characters are printed on the same line
     */
    @Test
    public void testCharactersPerLine(){
    	
    	
    }
    
    /*
     *  used to update the lowInk and lowPaper variables since they can be
     *	updated in receiptControl without a way to read the updates
     *	in AttendantControl
     */
    private void updateLowInkAndPaper() {
    	if(sc.getReceiptControl().lowInk) {
    		acl.lowInk = true;
    	}
    	else {
    		acl.lowInk = false;
    	}
    	if(sc.getReceiptControl().lowPaper) {
    		acl.lowPaper = true;
    	}
    	else {
    		acl.lowPaper = false;
    	}
    }
  
    
    
    
    
	public class ReceiptControlListenerStub implements ReceiptControlListener{
    	String checkedoutItemsMessage = "";
    	String totalCostMessage = "";
    	String membershipMessage = "";
    	String dateandTimeMessage = "";
    	String thankyouMessage = "";
		boolean takeReceipt = false;
		boolean takeIncompleteReceipt = false;

		@Override
		public void outOfInkOrPaper(ReceiptControl rc, String message) {	
		}

		@Override
		public void setCheckedoutItems(ReceiptControl rc, String message) {
			checkedoutItemsMessage = message;
		}

		@Override
		public void setTotalCost(ReceiptControl rc, String totalCost) {
			totalCostMessage = totalCost;	
		}

		@Override
		public void setDateandTime(ReceiptControl rc, String dateTime) {
			dateandTimeMessage = dateTime;
		}

		@Override
		public void setThankyouMessage(ReceiptControl rc, String thankYou) {
			thankyouMessage = thankYou;
		}

		@Override
		public void setTakeReceiptState(ReceiptControl rc) {
			takeReceipt = true;
		}

		@Override
		public void setNoReceiptState(ReceiptControl rc) {
			takeReceipt = false;
		}

		@Override
		public void setIncompleteReceiptState(ReceiptControl rc) {
			takeIncompleteReceipt = true;
		}

		@Override
		public void setNoIncompleteReceiptState(ReceiptControl rc) {
			takeIncompleteReceipt = false;
		}
	}
	
	public class AttendantControlListenerStub implements AttendantControlListener, ReceiptPrinterListener {
    	boolean lowInk = false;
    	boolean lowPaper = false;
    	boolean outOfInk = true;
    	boolean outOfPaper = true;

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
		}

		@Override
		public void lowInk(AttendantControl ac, String message) {
			lowInk = true;
		}

		@Override
		public void lowPaper(AttendantControl ac, String message) {
			lowPaper = true;
		}

		@Override
		public void printerNotLowInkState() {
			lowInk = false;	
		}

		@Override
		public void printerNotLowPaperState() {
			lowPaper = false;	
		}

		@Override
		public void outOfInk(AttendantControl ac, String message) {
			outOfInk = true;
		}

		@Override
		public void outOfPaper(AttendantControl ac, String message) {
			outOfPaper = true;
		}

		@Override
		public void addTooMuchInkState() {
		}

		@Override
		public void addTooMuchPaperState() {
		}

		@Override
		public void signalWeightDescrepancy(String updateMessage) {
		}

		@Override
		public void noBagRequest() {
		}

		@Override
		public void initialState() {
		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {
		}

		@Override
		public void coinIsLowState(CoinStorageUnit unit, int amount) {
		}

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
		}

		@Override
		public void outOfPaper(IReceiptPrinter printer) {
		}

		@Override
		public void outOfInk(IReceiptPrinter printer) {
		}

		@Override
		public void lowInk(IReceiptPrinter printer) {
		}

		@Override
		public void lowPaper(IReceiptPrinter printer) {
		}

		@Override
		public void paperAdded(IReceiptPrinter printer) {
			outOfPaper = false;
		}

		@Override
		public void inkAdded(IReceiptPrinter printer) {
			outOfInk = false;
		}
	}

	public class StationControlListenerStub implements StationControlListener {

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {
			// TODO Auto-generated method stub
			
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
}