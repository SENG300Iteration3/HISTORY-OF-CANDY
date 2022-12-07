package swing.frames;


import javax.swing.JFrame;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PaneControlListener;

import swing.panes.AttendantStationPane;
import swing.styling.GUI_Constants;
import swing.styling.GUI_JFrame;

public class AttendantStationGUI  implements PaneControlListener {
  private PaneControl pc;
  private GUI_JFrame frame = new GUI_JFrame("Attendant Screen", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
  AttendantStationPane stationPane;


  public AttendantStationGUI(PaneControl pc) {
	  this.pc = pc;
	  this.pc.addListener(this);
	  frame.setVisible(true);
	  frame.setLocation(0, 0);
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  stationPane = new AttendantStationPane(pc, frame);
  }

  @Override
  public void clientSidePaneChanged(StationControl sc, int index) {
	  stationPane.setTabIndex(index);
  }
  
  public AttendantStationPane getStationPane() {
	  return stationPane;
  }
}