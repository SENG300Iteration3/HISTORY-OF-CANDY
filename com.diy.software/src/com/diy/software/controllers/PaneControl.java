package com.diy.software.controllers;

import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.diy.software.listeners.PaneControlListener;

public class PaneControl implements ChangeListener {
  private ArrayList<StationControl> stationControls;
  private ArrayList<PaneControlListener> listeners = new ArrayList<PaneControlListener>();

  public PaneControl(ArrayList<StationControl> stationControls) {
    this.stationControls = stationControls;
  }

  public void addListener(PaneControlListener pl) {
    listeners.add(pl);
  }

  public void removeListener(PaneControlListener pl) {
    listeners.remove(pl);
  }

  public void removeStation(StationControl sc) {
    stationControls.remove(sc);

    // TODO: On station remove we should remove the ui for the station
  }

  public ArrayList<StationControl> getStationControls() {
    return stationControls;
  }

  public void setCurrentStationControl(StationControl sc) {
    int index = stationControls.indexOf(sc);
    for (PaneControlListener listener : listeners) {
      listener.clientSidePaneChanged(sc, index);
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    setCurrentStationControl(stationControls.get(((JTabbedPane)e.getSource()).getSelectedIndex()));
  }
}
