package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.StationControl;

import swing.screens.AddOwnBagsPromptScreen;

public class AddOwnBagsPromptScreenTest {
	
	private AddOwnBagsPromptScreen screen;
	private StationControl sc;
	private BagsControl bc;

	@Before
	public void setUp() throws Exception {
		sc = new StationControl();
		bc = sc.getBagsControl();
		
		screen = new AddOwnBagsPromptScreen(sc, "message");
	}

	@After
	public void tearDown() throws Exception {
		sc = null;
		bc = null;
		screen = null;
	}

	@Test
	public void testDoneAddingBagsButton() {
		screen.getDoneAddingBagsButton().doClick();
		assertFalse(screen.getDoneAddingBagsButton().isVisible());
	}
	
	@Test
	public void testAwaitingCustomerToFinishPlacingBagsInBaggingArea() {
		screen.awaitingCustomerToFinishPlacingBagsInBaggingArea(bc);
		assertTrue("This method doesn't do anything", true);
	}
	
	@Test
	public void testReadyToAcceptNewBagsInBaggingArea() {
		screen.readyToAcceptNewBagsInBaggingArea(bc);
		assertTrue("This method doesn't do anything", true);
	}

}
