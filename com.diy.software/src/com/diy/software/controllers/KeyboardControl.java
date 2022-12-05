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
		chars = new ArrayList<String>();
		capsLockOn = false; // assumes caps lock is initially off
		shiftPressed = false;
		pointer = -1;
		query = "";
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

	protected boolean isShiftPressed(String label) {
		if (label.equals("Shift (Right)") || label.equals("Shift (Left)")) {
			shiftPressed = !shiftPressed; // Toggle shift
		}
		return shiftPressed;
	}

	protected boolean isCapsLockOn(String label) {
		if (label.equals("CapsLock")) {
			capsLockOn = !capsLockOn; // Toggle capsLock
		}
		return capsLockOn;
	}

	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R
	// arrows, and enter
	protected void keyAction(String key) {
		switch (key) {
		case "Right Arrow":
			movePointer(1);
			return;
		case "Left Arrow":
			movePointer(-1);
			return;
		case "Backspace":
			removeChar(key, true);
			return;
		case "Delete":
			removeChar(key, false);
			return;
		case "Enter":
			for (String c : chars) {
				query += c;
			}
			completedInput();
			return;
		}
		addToString(key);
	}

	protected void movePointer(int delta) {
		int queryLength = chars.size();
		// Update the pointer by moving it delta delta spaces to the left or right depending on sign
		// Value of pointer is safely clamped between a min of 0 and a max of queryLength
		pointer = MathUtils.clamp(pointer + delta, 0, queryLength);
	}

	protected void removeChar(String label, boolean updatePointer) {
		int queryLength = chars.size();
		if (updatePointer) {
			if (pointer != 0) {
				pointer--;
				chars.remove(pointer);
			}
		} else if (pointer < queryLength) {
			chars.remove(pointer);
		}
	}

	protected void addToString(String label) {
		if (label.length() == 1) { // identifies letter key labels
			char c = label.charAt(0);
			chars.add(++pointer, getLetterCase(c));
		} else if (label.equals("Spacebar")) {
			chars.add(pointer, " ");
			return;
		}
		char[] ch = label.toCharArray();
		int ascii = (int) ch[0];
		if (ascii >= 48 && ascii <= 57) { // identifies number/symbol key labels
			chars.add(pointer, getNumberOrSymbol(ch));
			return;
		}
	}

	protected String getLetterCase(char c) {
		char[] ch = { c };
		String letter = ch.toString();
		if ((capsLockOn == true && shiftPressed == false) || (capsLockOn == false && shiftPressed == true)) {
			return letter;
		} else {
			letter.toLowerCase();
			return letter;
		}
	}

	protected String getNumberOrSymbol(char[] ch) {
		char[] label;
		if (shiftPressed == false) {
			label = new char[] { ch[0] }; // gets number from label
		} else {
			label = new char[] { ch[2] }; // gets symbol from label
		}
		return label.toString();
	}

	public void keyPressed(String key) {
		// TODO: Fix event error
		// isCapsLockOn(key);
		// isShiftPressed(key);
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, key);
	}

	public void keyReleased(String key) {
		isCapsLockOn(key);
		isShiftPressed(key);
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}
}