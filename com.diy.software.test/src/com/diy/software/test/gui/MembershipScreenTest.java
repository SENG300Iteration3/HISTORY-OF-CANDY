package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.MembershipControl;
import com.diy.software.listeners.MembershipControlListener;

public class MembershipScreenTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
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

}
