package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import com.diy.software.listeners.MembershipControlListener;

public class MembershipControl implements ActionListener {
	private StationControl sc;
	private ArrayList<MembershipControlListener> listeners;
	private String memberNumber = "";
	public String memberName;

	public MembershipControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}

	/**
	 * 
	 * Used to check if the membership number entered by the customer is in the
	 * membership database
	 * 
	 * @param memberShipNumber customer entered customer number
	 */
	public void checkMembership(int memberShipNumber) {
		HashMap<Integer, String> tempMembershipMap = new HashMap<Integer, String>();
		tempMembershipMap = sc.fakeData.getMembershipMap();
		if (tempMembershipMap.containsKey(memberShipNumber)) {
			this.memberName = tempMembershipMap.get(memberShipNumber);
			for (MembershipControlListener l : listeners)
				l.welcomeMember(this, "Welcome! " + memberName);
		} else {
			String notFound = "Member not found try again!";
			for (MembershipControlListener l : listeners)
				l.welcomeMember(this, notFound);
		}
	}

	public void addListener(MembershipControlListener l) {
		listeners.add(l);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("MEMBER_INPUT_BUTTON: ")) {
			memberNumber += c.split(" ")[1];
			for (MembershipControlListener l : listeners)
				l.memberFieldHasBeenUpdated(this, memberNumber);
		} else {
			switch (c) {
				case "cancel":
					sc.goBackOnUI();
					break;
				case "correct":
					if (memberNumber.length() > 0)
						memberNumber = memberNumber.substring(0, memberNumber.length() - 1);
					for (MembershipControlListener l : listeners)
						l.memberFieldHasBeenUpdated(this, memberNumber);
					break;
				case "submit":
					if (memberNumber != null && !memberNumber.isEmpty()) {
						int memNum = Integer.parseInt(memberNumber);
						checkMembership(memNum);
					}
					break;
				default:
					break;
			}
		}
	}
}
