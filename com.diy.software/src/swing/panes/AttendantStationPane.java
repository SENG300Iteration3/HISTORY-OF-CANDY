package swing.panes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

import swing.screens.AttendantLoginScreen;
import swing.screens.AttendantStationScreen;
import swing.screens.RemoveItemScreen;
import swing.styling.GUI_JFrame;
import swing.styling.Screen;

public class AttendantStationPane implements AttendantControlListener {

// 	private StationControl sc;
// 	private BagsControl bc;
// 	private AttendantControl ac;
// 	private boolean cusAddedBags = false;
// 	GUI_JButton approveAddedBagsButton;
// //	GUI_JButton addInkToPrinterButton;
// //	GUI_JButton addPaperToPrinterButton;
// 	GUI_JButton approveNoBagging;
// 	GUI_JButton startUpButton;
// 	GUI_JButton shutDownButton;
// 	GUI_JButton removeItemButton;
// 	GUI_JLabel weightDisplayLabel, weightDescrepancyMssg, inkLabel, paperLabel, adjustCoinLabel, adjustBanknoteLabel;
// 	GUI_JButton printReceiptButton;

// 	private static String HeaderText = "Attendant Screen";

// 	public AttendantStationPane(StationControl sc) {
// 		super(sc, HeaderText);
// 		this.sc = sc;
// 		bc = sc.getBagsControl();
// 		bc.addListener(this);

// 		ac = sc.getAttendantControl();
// 		ac.addListener(this);

// 		int width = 400;
// 		int height = 50;

// 		approveAddedBagsButton = makeButton("approvedAddedBags()");
// 		approveAddedBagsButton.setActionCommand("approve added bags");
// 		approveAddedBagsButton.addActionListener(ac);
// 		approveAddedBagsButton.setPreferredSize(new Dimension(width, height));
// //
// //		addInkToPrinterButton = makeButton("Add ink");
// //		addInkToPrinterButton.setActionCommand("addInk");
// //		addInkToPrinterButton.addActionListener(ac);
// //		addInkToPrinterButton.setPreferredSize(new Dimension(width, height));
// //		
// //		addPaperToPrinterButton = makeButton("Add paper");
// //		addPaperToPrinterButton.setActionCommand("addPaper");
// //		addPaperToPrinterButton.addActionListener(ac);
// //		addPaperToPrinterButton.setPreferredSize(new Dimension(width, height));
// //		
// 		approveNoBagging = makeButton("Approve no bagging");
// 		approveNoBagging.setActionCommand("approve no bag");
// 		approveNoBagging.addActionListener(ac);
// 		approveNoBagging.setPreferredSize(new Dimension(width, height));


	//It doesn't matter if these components are public
	//because they can be accessed by getting the components inside the AttendantLoginScreen
	public Component currentComponent;
	public AttendantLoginScreen loginScreen;
	public JTabbedPane tabbedPane; 
	public JFrame frame;
	private PaneControl pc;
	
	public AttendantStationPane(PaneControl pc, JFrame frame) {
		this.frame = frame;
		this.pc = pc;
		
		this.loginScreen = new AttendantLoginScreen(pc.getStationControls());
		this.tabbedPane = new JTabbedPane();
		int i = 1;
		for (StationControl sc : pc.getStationControls()) {
			sc.getAttendantControl().addListener(this);
			JPanel tempPanel = new JPanel();
			tempPanel.add(( new AttendantStationScreen(sc)).getRootPanel(), BorderLayout.CENTER);
			tabbedPane.addTab("Station " + i++, tempPanel);
			tabbedPane.addChangeListener(pc);
		}
		 
		this.currentComponent = new JPanel();
		this.frame.getContentPane().add(currentComponent, BorderLayout.CENTER);
		
// 		// not sure if needed anymore
// 		// shutDownButton = makeButton("Shut Down station");
// 		// shutDownButton.setActionCommand("shutDown");
// 		// shutDownButton.addActionListener(ac);
// 		// shutDownButton.setPreferredSize(new Dimension(width, height));

// 		removeItemButton = makeButton("Remove item");
// 		removeItemButton.setActionCommand("remove");
// 		removeItemButton.addActionListener(ac);
// 		removeItemButton.setPreferredSize(new Dimension(width, height));
		
// 		weightDescrepancyMssg = initalizeLabel("weightDiscrepancyMsg");
// 		weightDisplayLabel = initalizeLabel("weightDisplayLabel");
// 		inkLabel = initalizeLabel("Ink status");
// 		paperLabel = initalizeLabel("Paper status");
// 		adjustCoinLabel = initalizeLabel("Adjust coin");
// 		adjustBanknoteLabel = initalizeLabel("Adjust Banknote");
		
		
// 		// Notification Panel for JLabels that contain notification messages from system
// 		GUI_JPanel notificationPanel = new GUI_JPanel();
// 		notificationPanel.setLayout(new GridLayout(2, 3));
		
// 		notificationPanel.add(weightDescrepancyMssg);
// 		notificationPanel.add(weightDisplayLabel);
// 		notificationPanel.add(inkLabel);
// 		notificationPanel.add(paperLabel);
// 		notificationPanel.add(adjustCoinLabel);
// 		notificationPanel.add(adjustBanknoteLabel);
		
// 		this.addLayer(notificationPanel, 0);
		
		
		
// 		JScrollPane buttonScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
// 				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
// 		buttonScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(24, 0));
// 		buttonScrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
// 		buttonScrollPane.setPreferredSize(new Dimension(450, 200));
// 		buttonScrollPane.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		
		
// 		GUI_JPanel buttonsPanel	= new GUI_JPanel();
// 		buttonsPanel.setLayout(new GridLayout(4, 1));
// 		buttonScrollPane.getViewport().add(buttonsPanel);
		
// 		buttonsPanel.add(approveAddedBagsButton);
// //		buttonsPanel.add(addInkToPrinterButton);
// //		buttonsPanel.add(addPaperToPrinterButton);
// 		buttonsPanel.add(approveNoBagging);
// 		buttonsPanel.add(startUpButton);
// 		buttonsPanel.add(removeItemButton);
		
// 		// buttonsPanel.add(shutDownButton);
		
// 		this.addLayer(buttonScrollPane, 50);

// 		this.printReceiptButton = makeCentralButton("PRINT RECEIPT", this.width - 200, 25);
		
// 		printReceiptButton.setActionCommand("printReceipt");
// 		printReceiptButton.addActionListener(systemControl.getReceiptControl());
		
// 		this.addLayer(printReceiptButton, 0);
		
// //		addInkToPrinterButton.setEnabled(false);
// //		addPaperToPrinterButton.setEnabled(false);
// 		approveNoBagging.setEnabled(false);
// 		approveAddedBagsButton.setEnabled(cusAddedBags);
// 		startUpButton.setEnabled(true);
// 		// shutDownButton.setEnabled(true);
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
	public void addTooMuchPaperState() {
		approveAddedBagsButton.setEnabled(false);
//		addInkToPrinterButton.setEnabled(false);
//		addPaperToPrinterButton.setEnabled(true);
	}

	@Override
	public void addPaperState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTooMuchInkState() {
		approveAddedBagsButton.setEnabled(false);
//		addInkToPrinterButton.setEnabled(true);
//		addPaperToPrinterButton.setEnabled(false);
	}
	
	@Override
	public void addInkState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printerNotLowInkState() {
		approveAddedBagsButton.setEnabled(false);
		//addInkToPrinterButton.setEnabled(false);
		//addPaperToPrinterButton.setEnabled(false);
		inkLabel.setText("Ink good");
		//paperLabel.setText("Paper status");
		printReceiptButton.setEnabled(true);
	}
	
	@Override
	public void printerNotLowPaperState() {
		paperLabel.setText("Paper good");
		printReceiptButton.setEnabled(true);
		
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
			if (!isLoggedIn) {
				loginScreen = new AttendantLoginScreen(pc.getStationControls());
				changeComponents(loginScreen.getRootPanel());
			}
		}
		
	}

	@Override
	public void attendantPreventUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBagRequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialState() {
		approveAddedBagsButton.setEnabled(false);
//		addInkToPrinterButton.setEnabled(false);
//		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(false);
		weightDescrepancyMssg.setText("");
		
	}
	
	public static void main(String args[]) {
		StationControl sc = new StationControl();
		AttendantStationPane ap = new AttendantStationPane(sc);
		ap.openInNewJFrame();
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
		inkLabel.setText(message);
		inkLabel.setBackground(GUI_Color_Palette.RED_BROWN);
		printReceiptButton.setEnabled(false);
	}

	@Override
	public void outOfPaper(AttendantControl ac, String message) {
		paperLabel.setText(message);
		paperLabel.setBackground(GUI_Color_Palette.RED_BROWN);
		printReceiptButton.setEnabled(false);
	}

	@Override
	public void coinIsLowState(CoinStorageUnit unit, int amount) {
		// TODO Auto-generated method stub
		
	}

	public void setTabIndex(int index) {
		tabbedPane.setSelectedIndex(index);
		
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendantApprovedItemRemoval(AttendantControl bc) {
		// TODO Auto-generated method stub
		
	}
	public void itemBagged() {
		// TODO Auto-generated method stub
		
	}
}
