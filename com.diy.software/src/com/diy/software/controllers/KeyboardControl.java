package com.diy.software.controllers;

import java.util.ArrayList;

import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.util.MathUtils;

public class KeyboardControl {
	protected ArrayList<KeyboardControlListener> listeners = new ArrayList<>();
	protected String text = "";
	protected boolean capsLockOn = false; // assumes caps lock is initially off
	protected boolean shiftPressed = false;
	private int pointer = 0;

	public void addListener(KeyboardControlListener l) {
		listeners.add(l);
	}

	public void removeListener(KeyboardControlListener l) {
		listeners.remove(l);
	}
	
	public void keyPressed(String key) {
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, this.text, key);
	}

	public void keyReleased(String key) {
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}
	
	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R
	// arrows, L/R shift and enter
	// @return modified key
	private void keyAction(String key) {
		if (key.length() == 1) {
			addTextAtPointer(getLetterCase(key)); // Alphanumeric key
		} else if (key.equals("CapsLock")) {
			capsLockOn = !capsLockOn;
		} else if (key.startsWith("Shift")) {
			shiftPressed = !shiftPressed;
		} else if (key.equals("Spacebar")) {
			addTextAtPointer(" ");
		} else if (isNumOrSymbol(key)) {
			addTextAtPointer(getNumberOrSymbol(key));
		} else if (key.startsWith("Right")) {
			movePointer(1);
		} else if (key.startsWith("Left")) {
			movePointer(-1);
		} else if (key.equals("Backspace")) {
			removeCharAtPointer(true);
		} else if (key.equals("Delete")) {
			removeCharAtPointer(false);
		}
	}
	
	private void addTextAtPointer(String newText) {
		StringBuilder sb = new StringBuilder(text);
		sb.insert(pointer, newText);
		pointer++;
		this.text = sb.toString();
	}

	private String getLetterCase(String key) {
		return capsLockOn || shiftPressed ? key.toUpperCase() : key.toLowerCase();
	}
	
	// Delta is how many spaces the pointer will be moved
	private void movePointer(int delta) {
		// Update the pointer by moving it delta spaces to the left or right depending on sign
		// Value of pointer is safely clamped between a min of 0 and a max of queryLength
		pointer = MathUtils.clamp(pointer + delta, 0, text.length());
	}
	
	private void removeCharAtPointer(boolean updatePointer) {
		StringBuilder sb = new StringBuilder(text);
		
		sb.deleteCharAt(pointer);
		if (updatePointer && pointer != 0) pointer--;
		
		this.text = sb.toString();
	}
	
	// Identifies whether or not a key is 0-9 or one of !@#$%^&*()
	private boolean isNumOrSymbol(String key) {
		int ascii = (int) key.toCharArray()[0];
		return ascii >= 48 && ascii <= 57; // identifies number/symbol key labels
	}

	// Gets symbol or number from label (there is a space in between)
	private String getNumberOrSymbol(String key) {
		return String.valueOf(key.charAt(!shiftPressed ? 0 : 2));
	}
}