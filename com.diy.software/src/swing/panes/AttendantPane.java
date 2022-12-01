package swing.panes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

import swing.screens.AttendantLoginScreen;
import swing.styling.GUI_JFrame;
import swing.styling.Screen;

public class AttendantPane implements AttendantControlListener {
	private PaneControl pc;
	
	//private GUI_JFrame rooPanel;
	private Component currentPanel;
	private ArrayList<JPanel> panelStack;
	
	private AttendantLoginScreen loginScreen;
	private AttendantStationPane stationScreen;

	private JTabbedPane tabbedPane;

	private GUI_JFrame frame;
	
	public AttendantPane(PaneControl pc, GUI_JFrame frame) {
		
		this.pc = pc;
		this.frame = frame;
		
		this.panelStack = new ArrayList<>();
		this.loginScreen = new AttendantLoginScreen(pc.getStationControls());
		this.tabbedPane = new JTabbedPane();
		int i = 1;
		for (StationControl sc : pc.getStationControls()) {
			sc.getAttendantControl().addListener(this);
			JPanel tempPanel = new JPanel();
			tempPanel.add(( new AttendantStationPane(sc)).getRootPanel());
			tabbedPane.addTab("Station " + i++, tempPanel);
		}
		 
		this.currentPanel = new JPanel();
		this.frame.getContentPane().add(currentPanel, BorderLayout.CENTER);
		
		addScreenToStack(loginScreen);
		
	}
	
	//public JPanel getRootPanel() {
		//return rooPanel;
	//}
	
	private void addPanel(Component newPanel) {
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
	public void initialState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loggedIn(boolean isLoggedIn) {
		if (isLoggedIn) {
			addPanel(tabbedPane);
			
			
		}else if (currentPanel.equals(loginScreen.getRootPanel())){
		
			loginScreen.loginFail();
		}else{
			//logout
		}
		
	}

	@Override
	public void attendantPreventUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowInk(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowPaper(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outOfInk(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outOfPaper(AttendantControl ac, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBagRequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinIsLowState(CoinStorageUnit unit, int amount) {
		// TODO Auto-generated method stub
		
	}
}
