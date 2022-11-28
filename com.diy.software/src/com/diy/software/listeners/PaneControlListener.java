package com.diy.software.listeners;

import com.diy.software.controllers.StationControl;

public interface PaneControlListener {
  public void clientSidePaneChanged(StationControl sc, int index);
}
