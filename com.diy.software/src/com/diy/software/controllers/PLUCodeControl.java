package com.diy.software.controllers;

import com.diy.software.listeners.PLUCodeControlListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PLUCodeControl implements ActionListener {
	private StationControl sc;
	private String pluCode = "";
	private ArrayList<PLUCodeControlListener> listeners;

	public PLUCodeControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}
	
	public void addListener(PLUCodeControlListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PLUCodeControlListener l) {
		listeners.remove(l);
	}
	
	public void exitPLUCode() {
		pluCode = "";
		//TODO: insert line of code here. something like pin pad controls "sc.getWalletControl().enablePayments();"
		sc.goBackOnUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("PLU_INPUT_BUTTON: ")) {
			pluCode += c.split(" ")[1];
			for (PLUCodeControlListener l: listeners)
				l.pluHasBeenUpdated(this, pluCode);
		} else {
			switch (c) {
			case "cancel":
				exitPLUCode();
				break;
			case "correct":
				if (pluCode.length() > 0) pluCode = pluCode.substring(0, pluCode.length() - 1);
				for (PLUCodeControlListener l: listeners)
					l.pluHasBeenUpdated(this, pluCode);
				break;
			case "submit":
				//TODO: insert the line of code here. something like pin pad controls "sc.getWalletControl().insertCard(pin);"
				pluCode = "";
				break;
			default:
				break;
			}
		}
	}
}