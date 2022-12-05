package com.diy.software.controllers;

import java.util.ArrayList;

import com.diy.software.listeners.KeyboardControlListener;

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
		capsLockOn = false; 				// assumes caps lock is initially off
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
			shiftPressed = !shiftPressed; //Toggle shift
		}
		return shiftPressed;
	}
	
	protected boolean isCapsLockOn(String label) {
		if (label.equals("CapsLock")){
			capsLockOn = !capsLockOn; //Toggle capsLock
		}
		return capsLockOn;
	}
	
	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R arrows, and enter
	protected void keyAction(String label) {
		if (label.equals("Right Arrow") || label.equals("Left Arrow")) {
			movePointer(label);
			return;
		}
		if (label.equals("Backspace") || label.equals("Delete")) {
			removeChar(label);
			return;
		}
		if (label.equals("Enter")) {
			for (String c : chars) {
				query += c;
			}
			completedInput();
			return;
		}
		addToString(label);
	}
	
	protected void movePointer(String label) {
		int queryLength = chars.size();
		if (label.equals("Right Arrow")) {
			if (pointer < (queryLength)) {
					pointer++;
			}
		}
		if (label.equals("Left Arrow")) {
			if (pointer != 0) {
					pointer--;
			}
		}
	}
	
	protected void removeChar(String label) {
		int queryLength = chars.size();
		if (label.equals("Delete")) {
			if (pointer < queryLength) {
					chars.remove(pointer);
			}
		}
		if (label.equals("Backspace")) {
			if (pointer != 0) {
					chars.remove(pointer-1);
					pointer --;
			}
		}
	}
	
	protected void addToString(String label) {
		if (label.length() == 1) {						// identifies letter key labels
			char c = label.charAt(0);
			chars.add(++pointer, getLetterCase(c));
		}
		if (label.equals("Spacebar")) {
			chars.add(pointer, " ");
		}
		char[] ch = label.toCharArray();
		int ascii = (int) ch[0];
		if (ascii >= 48 && ascii <= 57) {				// identifies number/symbol key labels
			chars.add(pointer, getNumberOrSymbol(ch));
			return;
		}
	}
	
	protected String getLetterCase(char c) {
		char[] ch = {c};
		String letter = ch.toString();
		if ((capsLockOn == true && shiftPressed == false) || 
				(capsLockOn == false && shiftPressed == true)) {
			return letter;
		}
		else {
			letter.toLowerCase();
			return letter;
		}
	}
	
	protected String getNumberOrSymbol(char[] ch) {
		char[] label;
		if (shiftPressed == false) {
			label = new char[] {ch[0]};				// gets number from label
		}
		else {
			label = new char[] {ch[2]};				// gets symbol from label
		}
		return label.toString();
	}
	
	public void keyPressed(String key) {
		// TODO: Fix event error
		//isCapsLockOn(key);
		//isShiftPressed(key);
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