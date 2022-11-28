package com.diy.software.controllers;

import java.util.HashMap;
import java.util.Objects;

public class AttendantLogin {
	
	public static final HashMap<String,String> loginMap = new HashMap<String,String>();
	
public static boolean login(String username, String password) {
	return Objects.equals(loginMap.get(username), password) && password != null;
	}

}
