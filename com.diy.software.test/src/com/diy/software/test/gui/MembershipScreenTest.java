package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.MembershipControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.MembershipControlListener;
import com.diy.software.listeners.StationControlListener;
import com.diy.software.test.logic.MembershipControlTest.MembershipControlListenerStub;
import com.jimmyselectronics.opeechee.Card.CardData;

import ca.powerutility.PowerGrid;
import swing.screens.MembershipScreen;

public class MembershipScreenTest {
	
	public MembershipScreen screen;
	public MembershipControl mc;
	public StationControl sc;
	public FakeDataInitializer fdi;
	public MembershipControlListenerStub mStub;
	public StationControlListenerStub sStub;

	@Before
	public void setUp() throws Exception {
		
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		mc = sc.getMembershipControl();
		
		mStub = new MembershipControlListenerStub();
		mc.addListener(mStub);
		
		sStub = new StationControlListenerStub();
		sc.register(sStub);
		
		screen = new MembershipScreen(sc);
		mc.addListener(screen);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNumberPadButton() {
		screen.getNumberPadButtons()[0].doClick();
		assertEquals("1", mStub.memberNumber);
	}
	
	@Test
	public void testCancelButton() {
		screen.getCancelButton().doClick();
		assertTrue(sStub.panelTriggeredBack);
	}
	
	@Test
	public void testCorrectButton() {
		screen.getNumberPadButtons()[0].doClick();
		screen.getCorrectButton().doClick();
		assertEquals("", mStub.memberNumber);
	}
	
	@Test
	public void testCorrectButtonNothingInputted() {
		screen.getCorrectButton().doClick();
		assertEquals("", mStub.memberNumber);
	}
	
	@Test
	public void testSubmitButtonIncorrectNumber() {
		screen.getNumberPadButtons()[0].doClick();
		screen.getSubmitButton().doClick();
		assertEquals("Member not found try again!", mStub.memberName);
	}
	
	@Test
	public void testSubmitButtonCorrectNumber() {
		screen.getNumberPadButtons()[0].doClick();
		screen.getNumberPadButtons()[1].doClick();
		screen.getNumberPadButtons()[2].doClick();
		screen.getNumberPadButtons()[3].doClick();
		screen.getSubmitButton().doClick();
		assertEquals("Welcome! Itadori", mStub.memberName);
	}
	
	@Test
	public void testScanScwipButton() {
		screen.getScanSwipeButton().doClick();
		assertTrue(mStub.scanSwipeSelected);
	}
	
	public class MembershipControlListenerStub implements MembershipControlListener{
		
		public String memberName;
		public String memberNumber;
		public boolean scanSwipeSelected = false;
		public boolean membershipInput = true;

		@Override
		public void welcomeMember(MembershipControl mc, String memberName) {
			this.memberName = memberName;
			
		}

		@Override
		public void memberFieldHasBeenUpdated(MembershipControl mc, String memberNumber) {
			this.memberNumber = memberNumber; 
			
		}

		@Override
		public void scanSwipeSelected(MembershipControl mc) {
			scanSwipeSelected = true;
			
		}

		@Override
		public void disableMembershipInput(MembershipControl mc) {
			membershipInput = false;
			
		}
	}
	
	public class StationControlListenerStub implements StationControlListener{
		
		public boolean panelTriggeredBack = false;

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
			panelTriggeredBack = true;
			
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
		
	}

}
