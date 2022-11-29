package com.diy.software.listeners;

import com.diy.software.controllers.CashControl;

public interface CashControlListener {

  void coinInsertionEnabled(CashControl cc);
  
  void noteInsertionEnabled(CashControl cc);

  void coinInsertionDisabled(CashControl cc);
  
  void noteInsertionDisabled(CashControl cc);

  void cashInserted(CashControl cc);
  
  void cashRejected(CashControl cc);
  
  void changeReturned(CashControl cc);
}