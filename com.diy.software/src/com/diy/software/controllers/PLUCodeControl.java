package com.diy.software.controllers;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import com.diy.hardware.PriceLookUpCode;
import com.diy.software.listeners.PLUCodeControlListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PLUCodeControl implements ActionListener {
	private StationControl sc;
	private ItemsControl ic;
	private String pluCode = "";
	private ArrayList<PLUCodeControlListener> listeners;

	public PLUCodeControl(StationControl sc) {
		this.sc = sc;
		this.ic = sc.getItemsControl();
		this.listeners = new ArrayList<>();
		this.addListener(ic);
	}
	
	public void addListener(PLUCodeControlListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PLUCodeControlListener l) {
		listeners.remove(l);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if (c.startsWith("PLU_INPUT_BUTTON: ")) {
			pluCode += c.split(" ")[1];
			
			// Updates input on GUI
			for (PLUCodeControlListener l: listeners)
				l.pluHasBeenUpdated(this, pluCode);
		} else {
			switch (c) {
			case "cancel":
				pluCode = "";		
				sc.goBackOnUI();
				break;
			case "correct":
				pluCode = "";
				for (PLUCodeControlListener l: listeners)
					l.pluHasBeenUpdated(this, pluCode);
				break;
			case "submit":
				try {
					for (PLUCodeControlListener l: listeners)
						l.pluCodeEntered(this, pluCode);
					pluCode = "";
					break;
				} catch(InvalidArgumentSimulationException exc) {
					System.err.println(exc.getMessage());
					for (PLUCodeControlListener l: listeners)
						l.pluErrorMessageUpdated(this, exc.getMessage());
					break;
				} catch (NullPointerException exc) {
					System.err.println(exc.getMessage());
					for (PLUCodeControlListener l: listeners)
						l.pluErrorMessageUpdated(this, exc.getMessage());
				}
			default:
				break;
			}
		}
	}
}
