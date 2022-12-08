//NOTE: also testing receipt related methods in attendant control

package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import com.diy.software.fakedata.FakeDataInitializer;
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
	FakeDataInitializer fdi;
	Tuple<String, Double> itemTuple1;
	Tuple<String, Double> itemTuple2;

   
	@Before
	public void setup() throws EmptyException, OverloadException {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addFakeMembers();
		fdi.addProductAndBarcodeData();
		
		sc = new StationControl(fdi);
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
		sc.getReceiptControl().finalReceiptToShowOnScreen.setLength(0);
		
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
		rp.disable();
		rp.turnOff();
		rp.unplug();
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
		assertFalse(acl.outOfInk);
		assertTrue(acl.lowInk);
    	sc.getReceiptControl().printReceipt("a");
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
		assertFalse(acl.outOfPaper);
		assertTrue(acl.lowPaper);
    	sc.getReceiptControl().printReceipt("\n");
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
		assertFalse(acl.lowInk);
		sc.getReceiptControl().printReceipt("a");
		assertFalse(acl.lowInk);
    	sc.getReceiptControl().printReceipt("a");
    	assertTrue(acl.lowInk);
	}
	
	/*
	 * Tests low ink when printing something that crosses the threshold 
	 * between not being low on paper and being low on paper
	 */
	@Test
	public void testLowPaperThreshold() {
		ac.addPaper(sc.getReceiptControl().paperLowThreshold);
		assertFalse(acl.lowPaper);
		sc.getReceiptControl().printReceipt("\n");
		assertFalse(acl.lowPaper);
    	sc.getReceiptControl().printReceipt("\n");
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
     *  TEST INTENTIONALLY FAILS TO SHOWCASEBUG
     *  Test EmptyException thrown when printing when out of paper
     *  
     *  BUG FOUND IN HARDWARE: If printing \n to use more paper when the printer
     *  is out of paper than an empty exception will not be thrown and the paper
     *  count will be decremented into the negatives.
     *  
     *  In order to get an empty exception a character must be printed
     *  on the new line of paper
     */
    @Test
    public void BUGSHOWCASEtestPrintingWhenOutOfPaper() throws EmptyException, OverloadException{
       	sc.getReceiptControl().printReceipt("\n");
    	sc.getReceiptControl().printReceipt("\n");
    	//This is false when it should be true because an empty exception is not thrown
    	//by the hardware even though a new line was printed when out of paper
    	assertTrue(rcl.takeIncompleteReceipt);
    	assertFalse(rcl.takeReceipt);
    }
    
    /*
     *  Test EmptyException thrown when printing when out of paper
     *  
     *  This test case works, unlike the one with the bug, because
     *  the bug since the empty exception registers when printing a
     *  character that consumes ink
     */
    @Test
    public void testPrintingWhenOutOfPaper() throws EmptyException, OverloadException{
       	sc.getReceiptControl().printReceipt("\n");
    	sc.getReceiptControl().printReceipt("a");
    	//This is false when it should be true because an empty exception is not thrown
    	//by the hardware even though a new line was printed when out of paper
    	assertTrue(rcl.takeIncompleteReceipt);
    	assertFalse(rcl.takeReceipt);
    }
    
	/*
	 * Tests overloading ink
	 */
	@Test
	public void overloadInk() {
		assertFalse(acl.tooMuchInk);
		ac.addInk(ReceiptPrinterND.MAXIMUM_INK + 1);
		assertTrue(acl.tooMuchInk);
	}
	
	/*
	 * Tests overloading paper
	 */
	@Test
	public void overloadPaper() {
		assertFalse(acl.tooMuchPaper);
		ac.addPaper(ReceiptPrinterND.MAXIMUM_PAPER + 1);
		assertTrue(acl.tooMuchPaper);
	}
    
    /*
     *  Test that printItems prints items properly when no items are bought
     */
    @Test
    public void testPrintItemsNoItems(){
        ac.addInk(2000);
        ac.addPaper(500);
        
    	sc.getReceiptControl().printItems();
    	
    	assertEquals(rcl.checkedOutItemsMessage, "");
    }
    
    /*
     *  Test that printItems prints items properly when one item is bought
     */
    @Test
    public void testPrintItemsOneItem(){
        ac.addInk(2000);
        ac.addPaper(500);
        
    	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[0]);
		
    	itemTuple1 = sc.getItemsControl().getItemDescriptionPriceList().get(0);
    	
    	sc.getReceiptControl().printItems();
    	
    	assertEquals(rcl.checkedOutItemsMessage, itemTuple1.x + " , $" + itemTuple1.y + "\n");
    }
    
    /*
     *  Test that printItems prints items properly when two items are bought
     */
    @Test
    public void testPrintItemsTwoItems(){
        ac.addInk(2000);
        ac.addPaper(500);
        
    	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[0]);
    	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[1]);
		
    	itemTuple1 = sc.getItemsControl().getItemDescriptionPriceList().get(0);
		itemTuple2 = sc.getItemsControl().getItemDescriptionPriceList().get(1);
    	
    	sc.getReceiptControl().printItems();
    	
    	assertEquals(rcl.checkedOutItemsMessage, itemTuple1.x + " , $" + itemTuple1.y + "\n" + itemTuple2.x + " , $" + itemTuple2.y + "\n");
    }
    
    /*
     *  Test that TotalCost prints items properly when no items are bought
     */
    @Test
    public void testPrintTotalCostNoItems(){
        ac.addInk(2000);
        ac.addPaper(500);
        
    	sc.getReceiptControl().printTotalCost();
    	
    	assertEquals(rcl.totalCostMessage, "Total: $0.0\n");
    }
    
    /*
     *  Test that TotalCost prints items properly when one item is bought
     */
    @Test
    public void testPrintTotalCostOneItem(){
        ac.addInk(2000);
        ac.addPaper(500);
        
      	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[0]);
  		
      	itemTuple1 = sc.getItemsControl().getItemDescriptionPriceList().get(0);
      	
      	sc.getReceiptControl().printTotalCost();
      	
      	assertEquals(rcl.totalCostMessage, "Total: $" + itemTuple1.y + "\n");
        
    	
    }
    
    /*
     *  Test that TotalCost prints items properly when two items are bought
     */
    @Test
    public void testPrintTotalCostTwoItems(){
        ac.addInk(2000);
        ac.addPaper(500);
        
    	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[0]);
    	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[1]);
		
    	itemTuple1 = sc.getItemsControl().getItemDescriptionPriceList().get(0);
		itemTuple2 = sc.getItemsControl().getItemDescriptionPriceList().get(1);
    	
    	sc.getReceiptControl().printTotalCost();
    	
    	double tempTotal = itemTuple1.y + itemTuple2.y;
    	assertEquals(rcl.totalCostMessage, "Total: $" + tempTotal + "\n");
    }
    
    /*
     *  Test that printMembership with membership number
     */
    @Test
    public void testPrintMembershipWithMembership(){
        ac.addInk(2000);
        ac.addPaper(500);
        
        sc.getMembershipControl().checkMembership(1234);
        sc.getReceiptControl().printMembership();
        assertEquals(rcl.membershipMessage, "Membership number: " + sc.getMembershipControl().getValidMembershipNumber() + "\n");
    }
    
    /*
     *  Test that printMembership with no membership number
     */
    @Test
    public void testPrintMembershipNoMembership(){
        ac.addInk(2000);
        ac.addPaper(500);
        
        sc.getReceiptControl().printMembership();
        assertEquals(rcl.membershipMessage, "");
    }
    
    /*
     *  Test print date time
     */
    @Test
    public void TestPrintDateTime(){
        ac.addInk(2000);
        ac.addPaper(500);
        
    	sc.getReceiptControl().printDateTime();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");  
	    Date receiptPrintDate = new Date();
	    String formattedDate = formatter.format(receiptPrintDate) + "\n";
	    assertEquals(rcl.dateandTimeMessage, formattedDate);
    }
    
    /*
     *  Test print thank you message with membership
     */
    @Test
    public void testPrintThankyouMsgWithMembership(){
        sc.getMembershipControl().checkMembership(1234);
        sc.getReceiptControl().printMembership();//update retrivedMemNum
        sc.getReceiptControl().printThankyouMsg();
        
        assertEquals(rcl.thankyouMessage, "Thank you for shopping with us " + sc.getMembershipControl().memberName + " !\n");
    }
    
    /*
     *  Test print thank you message no membership
     */
    @Test
    public void testPrintThankyouMsgNoMembership(){
    	sc.getReceiptControl().printThankyouMsg();
    	assertEquals(rcl.thankyouMessage, "Thank you for shopping with us  !\n");
    }
    
    /*
     * test reset State to see if recived mem number reset
     */
    @Test
    public void testResetState(){
        ac.addInk(2000);
        ac.addPaper(500);
        
        sc.getMembershipControl().checkMembership(1234);
        sc.getReceiptControl().printMembership();//update retrivedMemNum
        sc.getReceiptControl().printThankyouMsg();
        
        assertEquals(rcl.thankyouMessage, "Thank you for shopping with us " + sc.getMembershipControl().memberName + " !\n");
        
        sc.getReceiptControl().resetState();
        
        //prints like no membership number occured because reset state
    	sc.getReceiptControl().printThankyouMsg();
    	assertEquals(rcl.thankyouMessage, "Thank you for shopping with us!\n");
    }
    
    /*
     *  Test printFullReceipt
     */
    @Test
    public void testPrintFullReceipt(){
        ac.addInk(2000);
        ac.addPaper(500);
    	
		ActionEvent e = new ActionEvent(this, 0, "printReceipt");
    	sc.getItemsControl().addScannedItemToCheckoutList(fdi.getBarcodes()[0]);
    	itemTuple1 = sc.getItemsControl().getItemDescriptionPriceList().get(0);
    	sc.getMembershipControl().checkMembership(1234);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");  
	    Date receiptPrintDate = new Date();
	    String formattedDate = formatter.format(receiptPrintDate) + "\n";
    	
    	sc.getReceiptControl().actionPerformed(e);
    	
    	assertEquals(rcl.checkedOutItemsMessage + 
    			rcl.totalCostMessage + rcl.membershipMessage + 
    			rcl.dateandTimeMessage + rcl.thankyouMessage,
    			itemTuple1.x + " , $" + itemTuple1.y + 
    			"\n" + "Total: $" + itemTuple1.y + "\n" +
    			"Membership number: " + sc.getMembershipControl().getValidMembershipNumber() + "\n" +
    			formattedDate +
    			"Thank you for shopping with us " + sc.getMembershipControl().memberName + " !\n");
    }
    
    /*
     *  Test the taking of receipt (using action performed)
     */
    @Test
    public void testCutAndTakeReceipt(){
        ac.addInk(2000);
        ac.addPaper(500);
        
        ActionEvent e = new ActionEvent(this, 0, "takeReceipt");
        
        sc.getReceiptControl().printReceipt("a");
        assertTrue(rcl.takeReceipt);
        
        sc.getReceiptControl().actionPerformed(e);
        assertFalse(rcl.takeReceipt);
    }
    
    /*
     *  Test the taking of receipt (using action performed)
     */
    @Test
    public void testCutAndTakeReceiptIncomplete(){
        
        ActionEvent e = new ActionEvent(this, 0, "takeIncompleteReceipt");
        
        sc.getReceiptControl().printReceipt("aa");
        assertTrue(rcl.takeIncompleteReceipt);
        
        sc.getReceiptControl().actionPerformed(e);
        assertFalse(rcl.takeIncompleteReceipt);
    }
    
    /*
     *  Test print an incomplete than an complete receipt than taking both
     */
    @Test
    public void testCutAndTakeReceiptIncompleteThenComplete(){
        
        ActionEvent e = new ActionEvent(this, 0, "takeReceipt");
        
        sc.getReceiptControl().printReceipt("aa");
        assertTrue(rcl.takeIncompleteReceipt);
        
        ac.addInk(2000);
        ac.addInk(500);
        
        sc.getReceiptControl().printReceipt("a");
        
        assertTrue(rcl.takeReceipt);
     
        
        sc.getReceiptControl().actionPerformed(e);
        assertFalse(rcl.takeIncompleteReceipt);
        assertFalse(rcl.takeReceipt);
    }
    
    /*
     *  Test invalid argument actionPerformed
     */
    @Test
    public void testInvalidActionEvent(){
		ActionEvent e = new ActionEvent(this, -888, "frick");
    	sc.getReceiptControl().actionPerformed(e);
    }
    
    /*
     *  Test adding ink using action event
     */
    @Test
    public void testAddInkActionEvent(){
		ActionEvent e = new ActionEvent(this, 0, "addInk");
    	sc.getAttendantControl().actionPerformed(e);
    	assertEquals(sc.getReceiptControl().currentInkCount, 208001);
    }
    
    
    /*
     *  Test adding paper using action event
     */
    @Test
    public void testPaperActionEvent(){
		ActionEvent e = new ActionEvent(this, 0, "addPaper");
		sc.getAttendantControl().actionPerformed(e);
		assertEquals(sc.getReceiptControl().currentPaperCount, 501);
    }
    
    /*
     * If add Integer.MAX_VALUE ink to the printer while it is not empty
     * than the printer will not throw an overload exception even though it should
     * This is an issue with the hardware/java itself
     */
    /*
    @Test
    public void BUGSHOWCASEtestAddingOverMaxValue(){
    	//sc.getReceiptControl().printReceipt("a");
		assertFalse(acl.tooMuchInk);
		ac.addInk(Integer.MAX_VALUE);
		assertTrue(acl.tooMuchInk);
	}
	*/
    
    /*
     *  Test that a new line is added when 60+ characters are printed on the same line
     */
    @Test
    public void testCharactersPerLine(){
        ac.addInk(2000);
        ac.addPaper(500);
        
        //60 characters in line1 and then a new line is made
    	sc.getReceiptControl().printReceipt("LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE2");
    	System.out.println(sc.getReceiptControl().finalReceiptToShowOnScreen);
    	String finalReceiptString = sc.getReceiptControl().finalReceiptToShowOnScreen.toString();
    	assertEquals(finalReceiptString, "LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1LINE1" + "\n" + "LINE2");
    }
  

	public class ReceiptControlListenerStub implements ReceiptControlListener{
    	String checkedOutItemsMessage = "";
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
			checkedOutItemsMessage = message;
		}

		@Override
		public void setTotalCost(ReceiptControl rc, String totalCost) {
			totalCostMessage = totalCost;	
		}
		
		@Override
		public void setMembership(ReceiptControl rc, String membership) {
			membershipMessage = membership;
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
    	boolean tooMuchInk = false;
    	boolean tooMuchPaper = false;

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
			tooMuchInk = true;
		}

		@Override
		public void addTooMuchPaperState() {
			tooMuchPaper = true;
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

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemBagged() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void loggedIn(boolean isLoggedIn) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void banknotesInStorageLowState() {
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exitTextSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void printerNotLowState() {
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

		@Override
		public void triggerPLUCodeWorkflow(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerBrowsingCatalog(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerReceiptScreen(StationControl systemControl) {
			// TODO Auto-generated method stub
			
		}
	}
}