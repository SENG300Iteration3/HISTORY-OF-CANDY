package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.diy.software.listeners.KeyboardControlListener;

public class VirtualKeyboardControl extends KeyboardControl implements ActionListener {
	private StationControl sc;
	
	private static final String KEY_PRESSED = "KEY_PRESS: ";
	
	public VirtualKeyboardControl(StationControl sc) {
		super();
		this.sc = sc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith(KEY_PRESSED)) {
			String key = c.substring(KEY_PRESSED.length());
			System.out.println(c);
			this.keyPressed(key);
		} else {
			
		}
	}
}
