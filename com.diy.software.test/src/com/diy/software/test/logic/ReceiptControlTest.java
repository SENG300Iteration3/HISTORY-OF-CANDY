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
	}
	
	@After
	public void teardown() {
		//TODO add anything that could affect future states
		//(keep in mind when writing tests)
	}
	
	@Test
	public void test1() {
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