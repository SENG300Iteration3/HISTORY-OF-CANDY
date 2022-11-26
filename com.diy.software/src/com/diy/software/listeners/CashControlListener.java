package com.diy.software.listeners;

import com.diy.software.controllers.CashControl;

public interface CashControlListener {

  void cashInsertionEnabled(CashControl cc);

  void cashInsertionDisabled(CashControl cc);

  void cashInserted(CashControl cc);

}