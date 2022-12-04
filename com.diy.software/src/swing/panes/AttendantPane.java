package swing.panes;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

import swing.screens.AttendantLoginScreen;

public class AttendantPane implements AttendantControlListener {

	//It doesn't matter if these components are public
	//because they can be accessed by getting the components inside the AttendantLoginScreen
	public Component currentComponent;
	public AttendantLoginScreen loginScreen;
	public JTabbedPane tabbedPane;
	public JFrame frame;
	
	public AttendantPane(ArrayList<StationControl> stationControls, JFrame frame) {
		this.frame = frame;
		
		this.loginScreen = new AttendantLoginScreen(stationControls);
		this.tabbedPane = new JTabbedPane();
		int i = 1;
		for (StationControl sc : stationControls) {
			sc.getAttendantControl().addListener(this);
			JPanel tempPanel = new JPanel();
			tempPanel.add(( new AttendantStationPane(sc)).getRootPanel());
			tabbedPane.addTab("Station " + i++, tempPanel);
		}
		 
		this.currentComponent = new JPanel();
		this.frame.getContentPane().add(currentComponent, BorderLayout.CENTER);
		
		changeComponents(loginScreen.getRootPanel());
		
	}
	
	private void changeComponents(Component newComponent) {
		frame.remove(currentComponent);
		currentComponent = newComponent;
		frame.add(currentComponent);
		frame.invalidate();
		frame.validate();
		frame.repaint();
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
			changeComponents(tabbedPane);
			
			
		}else if (currentComponent.equals(loginScreen.getRootPanel())){
		
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
