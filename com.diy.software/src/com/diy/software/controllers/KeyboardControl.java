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

	protected void completedInput() {
		for (KeyboardControlListener l : listeners)
			l.keyboardInputCompleted(this, this.query);
	}

	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R
	// arrows, L/R shift and enter
	protected void keyAction(String key) {
		if (key.length() == 1) {
			addToString(key); // Alphanumeric key
		} else if (key.startsWith("Right")) {
			movePointer(1);
		} else if (key.startsWith("Left")) {
			movePointer(-1);
		} else if (key.equals("Backspace")) {
			removeCharAtPointer(true);
		} else if (key.equals("Delete")) {
			removeCharAtPointer(false);
		} else if (key.equals("CapsLock")) {
			capsLockOn = !capsLockOn;
		} else if (key.startsWith("Shift")) {
			shiftPressed = !shiftPressed;
		} else if (key.equals("Enter")) {
			query += chars.toString();
			completedInput();
		}
	}
	
	// Delta is how many spaces the pointer will be moved
	protected void movePointer(int delta) {
		int queryLength = chars.size();
		// Update the pointer by moving it delta delta spaces to the left or right depending on sign
		// Value of pointer is safely clamped between a min of 0 and a max of queryLength
		pointer = MathUtils.clamp(pointer + delta, 0, queryLength);
	}
	
	protected void removeCharAtPointer(boolean updatePointer) {
		int queryLength = chars.size();
		if (updatePointer) {
			if (pointer != 0) {
				chars.remove(--pointer);
			}
		} else if (pointer < queryLength) {
			chars.remove(pointer);
		}
	}

	protected void addToString(String key) {
		if (key.length() == 1) { // identifies letter key labels
			char c = key.charAt(0);
			chars.add(++pointer, getLetterCase(c));
		} else if (key.equals("Spacebar")) {
			chars.add(++pointer, " ");
		} else if (isNumOrSymbol(key)) {
			chars.add(++pointer, getNumberOrSymbol(key));
		}
	}

	protected String getLetterCase(char c) {
		String letter = String.valueOf(c);
		if (shiftPressed == capsLockOn) {
			letter.toLowerCase();
		}
		return letter;
	}
	
	private boolean isNumOrSymbol(String key) {
		int ascii = (int) key.toCharArray()[0];
		return ascii >= 48 && ascii <= 57; // identifies number/symbol key labels
	}

	protected String getNumberOrSymbol(String key) {
		return String.valueOf(key.charAt(!shiftPressed ? 0 : 2)); // gets symbol or number from label
	}

	public void keyPressed(String key) {
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, key);
	}

	public void keyReleased(String key) {
		//keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}
}