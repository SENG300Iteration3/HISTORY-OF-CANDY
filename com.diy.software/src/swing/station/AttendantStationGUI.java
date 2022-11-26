package swing.station;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.diy.software.controllers.SystemControl;

import swing.GUI_Constants;
import swing.GUI_JFrame;
import swing.screens.AttendantScreen;

public class AttendantStationGUI {
  SystemControl sc;
  GUI_JFrame frame = new GUI_JFrame("Attendant Actions", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
  AttendantScreen attendantScreen;

  public AttendantStationGUI(SystemControl sc) {
    this.sc = sc;

    attendantScreen = new AttendantScreen(sc);
    frame.setVisible(true);
    frame.setLocation(0, 0);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(attendantScreen.getRootPanel(), BorderLayout.CENTER);

  }

}