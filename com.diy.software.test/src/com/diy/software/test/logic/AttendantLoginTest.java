package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;

public class AttendantLoginTest {

	public AttendantControl ac;
	public StationControl sc;

	@Before
	public void setUp() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		fdi.addFakeAttendantLogin();
		sc = new StationControl();
		ac = new AttendantControl(sc);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testLoginPass() {
		assertTrue(ac.login("A1", "password"));
	}
	
	@Test
	public void testLoginWrongName() {
		assertFalse(ac.login("chadwick", "password"));
	}
	
	@Test
	public void testLoginWrongPassword() {
		assertFalse(ac.login("A1", "pass"));
	}
	
	@Test
	public void testLoginWrongNull() {
		assertFalse(ac.login(null, null));
	}
	
	@Test
	public void testLoginWrongNullUsername() {
		assertFalse(ac.login(null, "test"));
	}
	
	@Test
	public void testLoginWrongNullPassword() {
		assertFalse(ac.login("test2", null));
	}

}
