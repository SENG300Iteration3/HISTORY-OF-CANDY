package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;

import swing.screens.ReceiptScreen;

public class ReceiptScreenTest {
	
	StationControl sc;
	ReceiptScreen screen;

	@Before
	public void setUp() throws Exception {
		
		sc = new StationControl();
		screen = new ReceiptScreen(sc);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
