package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.enums.NumpadUseArea;
import com.diy.software.listeners.NumpadControlListener;

public class NumpadControl implements ActionListener {
    private StationControl sc;
    private String numString = "";
    private NumpadUseArea useArea = null;

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

    public void setUseArea(NumpadUseArea useArea) {
        this.useArea = useArea;
    }

    public NumpadUseArea getUseArea() {
        return useArea;
    }

    public void exitNumPad() {
        numString = "";
        for (NumpadControlListener l: listeners)
            l.numpadCancelled(this, numString);
        sc.goBackOnUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String c = e.getActionCommand();
        if (c.startsWith("INPUT_BUTTON: ")) {
            numString += c.split(" ")[1];
            for (NumpadControlListener l: listeners)
                l.numberHasBeenUpdated(this, numString);
        } else {
            switch (c) {
                case "cancel":
                    exitNumPad();
                    break;
                case "correct":
                    if (numString.length() > 0) numString = numString.substring(0, numString.length() - 1);
                    for (NumpadControlListener l: listeners)
                        l.numberHasBeenUpdated(this, numString);
                    break;
                case "submit":
                    sc.getWalletControl().insertCard(numString);
                    numString = "";
                    break;
                default:
                    break;
            }
        }
    }
}
