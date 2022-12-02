package swing.frames;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PaneControlListener;

import swing.panes.AttendantActionsPane;
import swing.panes.CustomerActionsPane;
import swing.styling.GUI_Constants;
import swing.styling.GUI_JFrame;

public class AttendantActionsGUI implements PaneControlListener {
  private PaneControl pc;
  private GUI_JFrame frame = new GUI_JFrame("Attendant Actions", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT/3);
  private JTabbedPane tabbedPane = new JTabbedPane();

  public AttendantActionsGUI(PaneControl pc) {
    this.pc = pc;
    this.pc.addListener(this);
    frame.setVisible(true);
    frame.setLocation(1024, 1024);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(tabbedPane , BorderLayout.CENTER);

		initializePanes();
  }

  public void initializePanes() {
    int i = 1;
    for (StationControl sc : pc.getStationControls()) {
      tabbedPane.addTab(
        "Station " + i++,
        (new AttendantActionsPane(sc)).getRootPanel()
      );
    }
    tabbedPane.addChangeListener(pc);
  }

  @Override
  public void clientSidePaneChanged(StationControl sc, int index) {
		tabbedPane.setSelectedIndex(index);
  }
}