package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.listeners.NumpadControlListener;

public class NumpadControl implements ActionListener {
    private StationControl sc;
    private String pin = "";
    private ArrayList<NumpadControlListener> listeners;

    public NumpadControl(StationControl sc) {
        this.sc = sc;
        this.listeners = new ArrayList<>();
    }

    public void addListener(NumpadControlListener l) {
        listeners.add(l);
    }

    public void removeListener(NumpadControlListener l) {
        listeners.remove(l);
    }

    //what replace with?
    public void exitPinPad() {
        pin = "";
        sc.getWalletControl().enablePayments();
        sc.goBackOnUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String c = e.getActionCommand();
        if (c.startsWith("INPUT_BUTTON: ")) {
            pin += c.split(" ")[1];
            for (NumpadControlListener l: listeners)
                l.numberHasBeenUpdated(this, pin);
        } else {
            switch (c) {
                case "cancel":
                    exitPinPad();
                    break;
                case "correct":
                    if (pin.length() > 0) pin = pin.substring(0, pin.length() - 1);
                    for (NumpadControlListener l: listeners)
                        l.numberHasBeenUpdated(this, pin);
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
