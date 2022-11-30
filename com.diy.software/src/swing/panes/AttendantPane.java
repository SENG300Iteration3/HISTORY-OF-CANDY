package swing.panes;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;

import swing.frames.AttendantStationGUI;
import swing.styling.Screen;

public class AttendantPane implements AttendantControlListener {
	private StationControl sc;
	
	private JPanel rooPanel, currentPanel;
	private ArrayList<JPanel> panelStack;
	
	private AttendantLoginPane loginPane;
	private AttendantStationPane stationPane;
	AttendantStationGUI asGUI;
	
	public AttendantPane(StationControl sc, AttendantStationGUI asGUI) {
		
		this.sc = sc;
		this.asGUI = asGUI;

		this.sc.getAttendantControl().addListener(this);
		
		this.panelStack = new ArrayList<>();
		this.loginPane = new AttendantLoginPane(sc);
		this.stationPane = new AttendantStationPane(sc);
		
		this.currentPanel = new JPanel();
		this.rooPanel = new JPanel();
		this.rooPanel.add(currentPanel);
		
		addScreenToStack(loginPane);
		
	}
	
	public JPanel getRootPanel() {
		return rooPanel;
	}
	
	private void addPanel(JPanel newPanel) {
		JPanel parent = (JPanel) currentPanel.getParent();
		parent.remove(currentPanel);
		currentPanel = newPanel;
		parent.add(currentPanel);
		parent.invalidate();
		parent.validate();
		parent.repaint();
	
	}
	
	private void addScreenToStack(Screen newScreen) {
		addPanel(newScreen.getRootPanel());
		panelStack.add(newScreen.getRootPanel());
	}

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPaperState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addInkState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printerNotLowState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBaggingRequestState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loggedIn() {
		asGUI.loginTabs();
	}
	
	public void logInRequested() {
		addScreenToStack(stationPane);
	}

}
