package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;

import ca.powerutility.PowerGrid;
import swing.screens.AttendantLoginScreen;

public class AttendantLoginScreenTest {
	StationControl sc;
	ArrayList<StationControl> stationControls;
	AttendantControlStub acStub;
	AttendantLoginScreen als;

	@Before
	public void setUp() {
		PowerGrid.engageUninterruptiblePowerSource();
		stationControls = new ArrayList<StationControl>();
		sc = new StationControl();
		acStub = new AttendantControlStub();
		sc.getAttendantControl().addListener(acStub);
		stationControls.add(sc);
		
		als = new AttendantLoginScreen(stationControls);
	}

	@After
	public void tearDown() {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void testLoginFail() {
		assertTrue(als.loginFail.getText().equals(""));
		als.loginFail();
		assertTrue(als.loginFail.getText().equals("INCORRECT LOGIN INFORMATION"));
	}

	@Test
	public void testActionPerformed() {
		acStub.loggedIn = true;
		als.actionPerformed(null);
		assertFalse(acStub.loggedIn);
	}

}
