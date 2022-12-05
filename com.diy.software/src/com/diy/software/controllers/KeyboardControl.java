package com.diy.software.controllers;

import java.util.ArrayList;

import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.util.MathUtils;

public class KeyboardControl {
	protected ArrayList<KeyboardControlListener> listeners;
	private ArrayList<String> chars;
	protected boolean capsLockOn;
	protected boolean shiftPressed;
	private int pointer;
	protected String query;

	public KeyboardControl() {
		this.listeners = new ArrayList<>();
		this.chars = new ArrayList<String>();
		this.capsLockOn = false; // assumes caps lock is initially off
		this.shiftPressed = false;
		this.pointer = -1;
		this.query = "";
	}

	public void addListener(KeyboardControlListener l) {
		listeners.add(l);
	}

	public void removeListener(KeyboardControlListener l) {
		listeners.remove(l);
	}
	
	public void keyPressed(String key) {
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, key);
	}

	public void keyReleased(String key) {
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}
	
	public void completedInput() {
		for (KeyboardControlListener l : listeners)
			l.keyboardInputCompleted(this, this.query);
	}
	
	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R
	// arrows, L/R shift and enter
	private void keyAction(String key) {
		if (key.length() == 1) {
			key = getLetterCase(key);
			addToString(key); // Alphanumeric key
		} else if (key.equals("CapsLock")) {
			capsLockOn = !capsLockOn;
		} else if (key.startsWith("Shift")) {
			shiftPressed = !shiftPressed;
		} else if (key.equals("Spacebar")) {
			addToString(" ");
		} else if (isNumOrSymbol(key)) {
			key = getNumberOrSymbol(key);
			addToString(key);
		} else if (key.startsWith("Right")) {
			movePointer(1);
		} else if (key.startsWith("Left")) {
			movePointer(-1);
		} else if (key.equals("Backspace")) {
			removeCharAtPointer(true);
		} else if (key.equals("Delete")) {
			removeCharAtPointer(false);
		} else if (key.equals("Enter")) {
			query += chars.toString();
			completedInput();
		}
	}
	
	// Delta is how many spaces the pointer will be moved
	private void movePointer(int delta) {
		int queryLength = chars.size();
		// Update the pointer by moving it delta spaces to the left or right depending on sign
		// Value of pointer is safely clamped between a min of 0 and a max of queryLength
		pointer = MathUtils.clamp(pointer + delta, 0, queryLength);
	}
	
	private void removeCharAtPointer(boolean updatePointer) {
		int queryLength = chars.size();
		if (updatePointer) {
			if (pointer != 0) {
				chars.remove(--pointer);
			}
		} else if (pointer < queryLength) {
			chars.remove(pointer);
		}
	}

	private void addToString(String key) {
		chars.add(++pointer, key);
	}

	private String getLetterCase(String key) {
		return capsLockOn ? key.toUpperCase() : key.toLowerCase();
	}
	
	// Identifies whether or not a key is 0-9 or one of !@#$%^&*()
	private boolean isNumOrSymbol(String key) {
		int ascii = (int) key.toCharArray()[0];
		return ascii >= 48 && ascii <= 57; // identifies number/symbol key labels
	}

	private String getNumberOrSymbol(String key) {
		return String.valueOf(key.charAt(!shiftPressed ? 0 : 2)); // gets symbol or number from label
	}
}