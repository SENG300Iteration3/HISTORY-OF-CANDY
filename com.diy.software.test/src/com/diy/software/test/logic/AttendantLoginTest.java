package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.fakedata.FakeDataInitializer;

public class AttendantLoginTest {

	@Before
	public void setUp() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		fdi.addFakeAttendantLogin();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testLoginPass() {
		assertTrue(AttendantControl.login("A1", "password"));
	}
	
	@Test
	public void testLoginWrongName() {
		assertFalse(AttendantControl.login("chadwick", "password"));
	}
	
	@Test
	public void testLoginWrongPassword() {
		assertFalse(AttendantControl.login("A1", "pass"));
	}
	
	@Test
	public void testLoginWrongNull() {
		assertFalse(AttendantControl.login(null, null));
	}
	
	@Test
	public void testLoginWrongNullUsername() {
		assertFalse(AttendantControl.login(null, "test"));
	}
	
	@Test
	public void testLoginWrongNullPassword() {
		assertFalse(AttendantControl.login("test2", null));
	}

}
