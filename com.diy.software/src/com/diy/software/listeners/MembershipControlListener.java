package com.diy.software.listeners;

import com.diy.software.controllers.MembershipControl;

public interface MembershipControlListener {
	
	/**
	 * display welcome member message upon successful membership number entry
	 * @param memberName 
	 */
	public void welcomeMember(MembershipControl mc, String memberName);

	public void memberFieldHasBeenUpdated(MembershipControl mc, String memberNumber);

	public void scanSwipeSelected(MembershipControl mc);
}
