package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.listeners.PinPadControlListener;

public class PinPadControl implements ActionListener {
	private StationControl sc;
	private String pin = "";
	private ArrayList<PinPadControlListener> listeners;

	public PinPadControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}
	
	public void addListener(PinPadControlListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PinPadControlListener l) {
		listeners.remove(l);
	}
	
	public void exitPinPad() {
		pin = "";
		sc.getWalletControl().enablePayments();
		sc.goBackOnUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("PIN_INPUT_BUTTON: ")) {
			pin += c.split(" ")[1];
			for (PinPadControlListener l: listeners)
				l.pinHasBeenUpdated(this, pin);
		} else {
			switch (c) {
			case "cancel":
				exitPinPad();
				break;
			case "correct":
				if (pin.length() > 0) pin = pin.substring(0, pin.length() - 1);
				for (PinPadControlListener l: listeners)
					l.pinHasBeenUpdated(this, pin);
				break;
			case "submit":
				sc.getWalletControl().insertCard(pin);
				pin = "";
				break;
			default:
				break;
			}
		}
	}
}
