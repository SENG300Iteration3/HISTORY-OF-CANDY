package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.MembershipControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.fakedata.MembershipDatabase;
import com.diy.software.listeners.MembershipControlListener;

public class MembershipControlTest {

	public MembershipControl mc;
	public StationControl sc;
	public FakeDataInitializer fdi;
	public MembershipControlListenerStub mcStub;

	@Before
	public void setUp() throws Exception {
		fdi = new FakeDataInitializer();
		sc = new StationControl(fdi);
		mc = new MembershipControl(sc);
		mcStub = new MembershipControlListenerStub();
		
		mc.addListener(mcStub);
		
		//MembershipDatabase.membershipMap.put(1234, "Itadori");
		//MembershipDatabase.membershipMap.put(1235, "Customer1");
		//MembershipDatabase.membershipMap.put(1236, "Customer2");
		//MembershipDatabase.membershipMap.put(1237, "Customer3");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheckMembership() {
		mc.checkMembership(1234);
		assertTrue(mcStub.message.equals("Welcome! Itadori"));
		
		mc.checkMembership(1239);
		assertTrue(mcStub.message.equals("Member not found try again!"));
		
		
	}
	
	@Test
	public void testMembership() {
		
		assertTrue(MembershipDatabase.membershipMap.containsKey(1234));
		assertTrue(MembershipDatabase.membershipMap.get(1234).equals("Itadori"));
		//assertNull(mcStub.message.equals("Welcome! Itadori"));
		mc.checkMembership(1234);
		assertTrue(mcStub.message.equals("Welcome! Itadori"));
		//assertFalse(mc.checkMembership(-1));
		
	}
	
	@Test
	public void testActionPerformed() {
		ActionEvent e = new ActionEvent(this, 0, "MEMBER_INPUT_BUTTON: 123");
		mc.actionPerformed(e);
		assertTrue(mcStub.message.equals("123"));
		
		
	}
	
	@Test
	public void testActionPerformedCancel() {
		ActionEvent e = new ActionEvent(this, 0, "cancel");
		mc.actionPerformed(e);
	}
	
	@Test
	public void testActionPerformedCorrect() {
		ActionEvent e = new ActionEvent(this, 0, "MEMBER_INPUT_BUTTON: 123");
		mc.actionPerformed(e);
		
		ActionEvent e2 = new ActionEvent(this, 0, "correct");
		mc.actionPerformed(e2);
		assertTrue(mcStub.message.equals("12"));
		
		
	}
	
	@Test
	public void testActionPerformedSubmit() {
		ActionEvent e = new ActionEvent(this, 0, "MEMBER_INPUT_BUTTON: 1234");
		mc.actionPerformed(e);
		
		ActionEvent e2 = new ActionEvent(this, 0, "submit");
		mc.actionPerformed(e2);
		assertTrue(mcStub.message.equals("1234"));
	}
	
	
	
	
	
	public class MembershipControlListenerStub implements MembershipControlListener{

		public String message;

		@Override
		public void welcomeMember(MembershipControl mc, String memberName) {
			message = memberName;
		}

		@Override
		public void memberFieldHasBeenUpdated(MembershipControl mc, String memberNumber) {
			message = memberNumber;
			
		}
		
	}

}

