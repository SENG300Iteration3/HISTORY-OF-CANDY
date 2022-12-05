package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;
import swing.screens.BlockedPromptScreen;

public class BlockedPromptScreenTest {

	FakeDataInitializer fdi;
	BlockedPromptScreen screen;
	StationControl sc;
	AttendantControlListenerStub aStub;
	
	
	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		screen = new BlockedPromptScreen(sc, "message");
		
		aStub = new AttendantControlListenerStub();
		
		sc.getAttendantControl().addListener(aStub);
	}

	@After
	public void tearDown() throws Exception {
		fdi = null;
		screen = null;
		sc = null;
		aStub = null;
	}

	@Test
	public void testRequestNoBaggingBtn() {
		screen.getRequestNoBaggingBtn().doClick();
		assertTrue(aStub.noBagRequest);
	}
	
	public class AttendantControlListenerStub implements AttendantControlListener{

		public boolean noBagRequest = false;
		
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
			noBagRequest = true;
			
		}

		@Override
		public void initialState() {
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
		
	}
}
