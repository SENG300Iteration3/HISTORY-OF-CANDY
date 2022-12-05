package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.util.MathUtils;

public class VirtualKeyboardControl extends KeyboardControl implements ActionListener {
	private StationControl sc;
	
	private static final String KEY_PRESSED = "KEY_PRESS: ";
	
	public VirtualKeyboardControl(StationControl sc) {
		super();
		this.sc = sc;
	}
	
	public void pressKey(String key) {
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, this.text, key, this.pointer);
	}

	public void releaseKey(String key) {
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith(KEY_PRESSED)) {
			String key = c.substring(KEY_PRESSED.length());
			this.pressKey(key);
		}
	}
}
