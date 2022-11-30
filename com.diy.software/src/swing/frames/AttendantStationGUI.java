package swing.frames;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PaneControlListener;

import swing.panes.AttendantLoginPane;
import swing.panes.AttendantPane;
import swing.panes.AttendantStationPane;
import swing.styling.GUI_Constants;
import swing.styling.GUI_JFrame;

public class AttendantStationGUI implements PaneControlListener {
  private PaneControl pc;
  private GUI_JFrame frame = new GUI_JFrame("Attendant Screen", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
  private JTabbedPane tabbedPane = new JTabbedPane();
  ArrayList<AttendantPane> attendantPanes;

  public AttendantStationGUI(PaneControl pc) {
    this.pc = pc;
    this.pc.addListener(this);
    frame.setVisible(true);
    frame.setLocation(0, 0);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(tabbedPane , BorderLayout.CENTER);
    attendantPanes = new ArrayList<AttendantPane>();

    initializePanes();
  }

  private void initializePanes() {
    int i = 1;
    for (StationControl sc : pc.getStationControls()) {
    	AttendantPane ap = new AttendantPane(sc, this);
    	attendantPanes.add(ap);
    	tabbedPane.addTab("Station " + i++, ap.getRootPanel());
    }
  }

  @Override
  public void clientSidePaneChanged(StationControl sc, int index) {
    // Do nothing
  }

  public void loginTabs() {
	  for (AttendantPane ap : attendantPanes) {
	    	ap.logInRequested();
	    }
	
  } 
}