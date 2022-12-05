package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.StationControl;

import ca.powerutility.PowerGrid;
import swing.screens.AddOwnBagsPromptScreen;

public class AddOwnBagsPromptScreenTest {
	
	private AddOwnBagsPromptScreen screen;
	private StationControl sc;
	private BagsControl bc;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
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
}
