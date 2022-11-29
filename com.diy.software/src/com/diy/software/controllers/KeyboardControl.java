package com.diy.software.controllers;

import java.util.ArrayList;

import com.diy.software.listeners.KeyboardControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.nightingale.Keyboard;
import com.jimmyselectronics.nightingale.KeyboardListener;

public class KeyboardControl implements KeyboardListener{
	private ArrayList<KeyboardControlListener> listeners;
	private Keyboard kb;
	private ArrayList<String> chars;
	private String query;
	private boolean isCapsLockOn;
	private boolean isShiftPressed;
	private int pointer;
	
	public KeyboardControl(Keyboard keyboard) {
		this.kb = keyboard;
		keyboard.register(this);
		chars = new ArrayList<String>();
		isCapsLockOn = false; 			// assumes caps lock is initially off
		isShiftPressed = false;
		pointer = -1;
	}
	
	private void awaitingInput() {
	    for (KeyboardControlListener listener : listeners) 
	      listener.awaitingKeyboardInput(this);
	  }
	
	private void recievedInput() {
		for (KeyboardControlListener listener : listeners)
			listener.keyboardInputRecieved(this);
	}
	
	private void completedInput() {
		for(KeyboardControlListener listener : listeners)
			listener.keyboardInputCompleted(this, this.query);
	}
	
	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R arrows, and enter
	private void keyAction(String label) {
		if (label.equals("Right Arrow") || label.equals("Left Arrow")) {
			movePointer(label);
			return;
		}
		if (label.equals("Backspace") || label.equals("Delete")) {
			removeChar(label);
		}
		if (label.equals("Enter")) {
			query = String.join("", chars);			// why doesn't this work??				
			completedInput();
			return;
		}
		addToString(label);
	}
	
	private void movePointer(String label) {
		int queryLength = String.join("", chars).length();
		if (label.equals("Right Arrow")) {
			if (pointer < (queryLength)) {
					pointer++;
			}
		}
		if (label.equals("Left Arrow")) {
			if (pointer != 0) {
					pointer --;
			}
		}
	}
	
	private void removeChar(String label) {
		int queryLength = String.join("", chars).length();
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
	
	private void addToString(String label) {
		if (label.length() == 1) {						// identifies letter key labels
			char c = label.charAt(0);
			chars.add(pointer, getLetterCase(c));
		}
		if (label.equals("Spacebar"));{
			chars.add(pointer, " ");
		}
		char[] ch = label.toCharArray();
		int ascii = (int) ch[0];
		if (ascii >= 48 && ascii <= 57) {				// identifies number/symbol key labels
			chars.add(pointer, getNumberOrSymbol(ch));
			return;
		}
	}
	
	private String getLetterCase(char c) {
		char[] ch = {c};
		String letter = ch.toString();
		if ((isCapsLockOn == true && isShiftPressed == false) || 
				(isCapsLockOn == false && isShiftPressed == true)) {
			return letter;
		}
		else {
			letter.toLowerCase();
			return letter;
		}
	}
	
	private String getNumberOrSymbol(char[] ch) {
		char[] label;
		if (isShiftPressed == false) {
			label = new char[] {ch[0]};				// gets number from label
		}
		else {
			label = new char[] {ch[2]};				// gets symbol from label
		}
		return label.toString();
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(Keyboard keyboard, String label) {
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this);
	}

	@Override
	public void keyReleased(Keyboard keyboard, String label) {
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}

}
