package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;

import com.jimmyselectronics.abagnale.ReceiptPrinterND;
import com.jimmyselectronics.opeechee.Card.CardData;
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
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		
		sc = new StationControl();
		ac = new AttendantControl(sc);
		rc = new ReceiptControl(sc);
		scl = new StationControlListenerStub();
    	acl = new AttendantControlListenerStub();
		rcl = new ReceiptControlListenerStub();
		rp = sc.station.printer;
		rp.register(ac);
		rp.plugIn();
		rp.turnOff();
		rp.turnOn();
		rp.enable();
		
		//TODO currently, broken should be fixed when updated with attendant control changes
		
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
		//TODO add anything that could affect future states
		//(keep in mind when writing tests)
	}
	
	/*
	 * Tests adding ink
	 */
	@Test
	public void testAddInk() {
	}
	
	/*
	 * Tests adding paper
	 */
	@Test
	public void testAddPaper() {
	}
	
	/*
	 * Tests out of ink
	 */
	@Test
	public void testOutOfInk() {
	}
	
	/*
	 * Tests out of paper
	 */
	@Test
	public void testOutOfPaper() {
	}
	
	/*
	 * Tests low ink
	 */
	@Test
	public void testsLowInk() {
	}
	
	/*
	 * Tests low paper
	 */
	@Test
	public void testLowPaper() {
	}
	
	/*
	 * Tests low ink when printing something that crosses the threshold 
	 * between not being low on ink and being low on ink
	 */
	@Test
	public void testsLowInkThreshold() {
	}
	
	/*
	 * Tests low ink when printing something that crosses the threshold 
	 * between not being low on paper and being low on paper
	 */
	@Test
	public void testLowPaperThreshold() {
	}
	
	/*
	 * Tests adding ink after running out
	 */
	@Test
	public void testAddInkAfterOut() {
	}
	
	/*
	 * Tests adding paper after running out
	 */
	@Test
	public void testAddPaperAfterOut() {
	}
	
	/*
	 * Test that printer is disabled when out of paper
	 */
	@Test
	public void testDisabledWhenOutPaper() {
	}
	
	/*
	 * Test that printer is disabled when out of ink
	 */
	@Test
	public void testDisabledWhenOutInk() {
	}
	
    /*
     *  Test EmptyException thrown when printing when out of ink
     */
    @Test (expected = EmptyException.class)
    public void testPrintingWhenOutOfInk() throws EmptyException, OverloadException{
    }
    
    /*
     *  Test EmptyException thrown when printing when out of paper
     */
    @Test (expected = EmptyException.class)
    public void testPrintingWhenOutOfPaper() throws EmptyException, OverloadException{
    }
    
    /*
     *  Test that a new line is added when 60+ characters are printed on the same line
     */
    @Test
    public void testCharactersPerLine(){
    	
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
	
	public class AttendantControlListenerStub implements AttendantControlListener {

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
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
		public void initialState() {
			// TODO Auto-generated method stub
			
		}
	}
		
	public class ReceiptControlListenerStub implements ReceiptControlListener {

		@Override
		public void outOfInkOrPaper(ReceiptControl rc, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setCheckedoutItems(ReceiptControl rc, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setTotalCost(ReceiptControl rc, String totalCost) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDateandTime(ReceiptControl rc, String dateTime) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setThankyouMessage(ReceiptControl rc, String dateTime) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setTakeReceiptState(ReceiptControl rc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setNoReceiptState(ReceiptControl rc) {
			// TODO Auto-generated method stub
			
		}
	}
}