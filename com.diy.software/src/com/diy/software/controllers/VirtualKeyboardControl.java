package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.diy.software.listeners.KeyboardControlListener;

public class VirtualKeyboardControl extends KeyboardControl implements ActionListener {
	private StationControl sc;
	
	public VirtualKeyboardControl(StationControl sc) {
		super();
		this.sc = sc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("KEY_PRESS: ")) {
			System.out.println("key press");
			for (KeyboardControlListener l: listeners)
				l.keyboardInputRecieved(null);
		} else {
			
		}
	}
}
