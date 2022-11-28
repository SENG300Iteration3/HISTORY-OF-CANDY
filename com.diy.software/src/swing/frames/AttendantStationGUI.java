package swing.frames;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PaneControlListener;

import swing.panes.AttendantStationPane;
import swing.styling.GUI_Constants;
import swing.styling.GUI_JFrame;

public class AttendantStationGUI implements PaneControlListener {
  private PaneControl pc;
  private GUI_JFrame frame = new GUI_JFrame("Attendant Screen", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
  private JTabbedPane tabbedPane = new JTabbedPane();

  public AttendantStationGUI(PaneControl pc) {
    this.pc = pc;
    this.pc.addListener(this);
    frame.setVisible(true);
    frame.setLocation(0, 0);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(tabbedPane , BorderLayout.CENTER);

    initializePanes();
  }

  private void initializePanes() {
    int i = 1;
    for (StationControl sc : pc.getStationControls()) {
      tabbedPane.addTab(
        "Station " + i++,
        (new AttendantStationPane(sc)).getRootPanel()
      );
    }
  }

  @Override
  public void clientSidePaneChanged(StationControl sc, int index) {
    // Do nothing
  } 
}