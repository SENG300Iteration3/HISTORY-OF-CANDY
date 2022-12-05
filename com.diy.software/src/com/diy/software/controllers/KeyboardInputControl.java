package com.diy.software.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import com.jimmyselectronics.nightingale.Keyboard;
import com.unitedbankingservices.DisabledException;

public class KeyboardInputControl  implements KeyListener{
	private Keyboard keyboard;
	private static String[] digitLabels = {"0 )", "1 !", "2 @", "3 #", "4 $", "5 %", "6 ^", "7 &", "8 *", "9 ("};
	
	private static HashMap<String, String> specialLabels  = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

	{
	    put("Shift", "Shift (Left)");
	    put("Caps Lock", "CapsLock");
	    put("`", "` ~"); 
	    put("-", "- _");
	    put("=", "= +");
	    put("[", "[ {");
	    put("]", "] }");
	    put("\\", "\\ |");
	    put(";", "; :");
	    put("'", "' \"");
	    put(",", ", <");
	    put(".", ". >"); 
	    put("/", "/ ?"); 
	    put("Space", "Spacebar"); 
	    put("Left", "Left Arrow");
	    put("Right", "Right Arrow"); 
	}};
	
	public KeyboardInputControl(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
				
		String k = getKey(e);
			
		if (k != null) {
			try {
				keyboard.keys().get(k).press();
			} catch (DisabledException e1) {
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		String k = getKey(e);
		
		if (k != null) {
			try {
				keyboard.keys().get(k).release();
			} catch (DisabledException e1) {
			}
		}
		
	}
		
	private String getKey(KeyEvent e) {
		String k;
			
		String keyText = KeyEvent.getKeyText(e.getKeyCode());
		if (keyboard.keys().containsKey(keyText)){
			k = keyText;
		} else if (Character.isDigit(keyText.charAt(0))) {
			k = digitLabels[Integer.parseInt(keyText)];
		}else {
			k = specialLabels.get(keyText);
		}
			
		return k;	
	}

}
