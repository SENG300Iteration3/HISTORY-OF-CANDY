package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantLogin;
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
		assertTrue(AttendantLogin.login("A1", "password"));
	}
	
	@Test
	public void testLoginWrongName() {
		assertFalse(AttendantLogin.login("chadwick", "password"));
	}
	
	@Test
	public void testLoginWrongPassword() {
		assertFalse(AttendantLogin.login("A1", "pass"));
	}
	
	@Test
	public void testLoginWrongNull() {
		assertFalse(AttendantLogin.login(null, null));
	}
	
	@Test
	public void testLoginWrongNullUsername() {
		assertFalse(AttendantLogin.login(null, "test"));
	}
	
	@Test
	public void testLoginWrongNullPassword() {
		assertFalse(AttendantLogin.login("test2", null));
	}

}
