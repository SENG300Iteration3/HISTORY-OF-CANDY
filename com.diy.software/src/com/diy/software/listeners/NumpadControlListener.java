package com.diy.software.listeners;

import com.diy.software.controllers.NumpadControl;

public interface NumpadControlListener {

    void numberHasBeenUpdated(NumpadControl npc, String number);
    void numpadCancelled(NumpadControl npc, String number);
    void numpadCorrected(NumpadControl npc, String number);
    void numpadSubmitted(NumpadControl npc, String number);
}
