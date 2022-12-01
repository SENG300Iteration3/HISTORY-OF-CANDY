package swing.frames;


import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.PaneControlListener;

import swing.panes.AttendantPane;
import swing.styling.GUI_Constants;
import swing.styling.GUI_JFrame;

public class AttendantStationGUI  implements PaneControlListener {
  private PaneControl pc;
  private GUI_JFrame frame = new GUI_JFrame("Attendant Screen", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
  AttendantPane loginPane;

  public AttendantStationGUI(PaneControl pc) {
    this.pc = pc;
    this.pc.addListener(this);
    frame.setVisible(true);
    frame.setLocation(0, 0);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    loginPane = new AttendantPane(pc);

    initializePanes();
  }

  private void initializePanes() {
	  
	  frame.getContentPane().add(loginPane.getRootPanel(), BorderLayout.CENTER);
	  frame.getContentPane().revalidate();
	  frame.getContentPane().repaint();
	  frame.revalidate();
	  frame.repaint();
  }

@Override
public void clientSidePaneChanged(StationControl sc, int index) {
	// TODO Auto-generated method stub
	
}
}