package com.diy.software.listeners;

import com.diy.software.controllers.NumpadControl;

public interface NumpadControlListener {

    void numberHasBeenUpdated(NumpadControl npc, String number);

}
