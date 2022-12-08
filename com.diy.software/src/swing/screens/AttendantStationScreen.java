package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.ReceiptControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class AttendantStationScreen extends Screen implements AttendantControlListener, BagsControlListener, ItemsControlListener {

	private StationControl sc;
	private BagsControl bc;
	private AttendantControl ac;
	
	private static final int BUTTON_WIDTH = 310;
	private static final int BUTTON_HEIGHT = 50;
	private static final int LABEL_WIDTH = 200;
	private static final int LABEL_HEIGHT = 60;
	private boolean cusAddedBags = false;

	private GUI_JPanel removeItemPanel;
	private JTextField removeItemTextField;
	
	GUI_JButton approveAddedBagsButton, approveNoBagging, startUpButton, shutDownButton, 
				permitButton, preventButton, addItemButton, removeItemButton, logoutButton;
	GUI_JLabel 	weightDisplayLabel, weightDescrepancyMssg, inkLabel, paperLabel,
				adjustCoinLabel, adjustBanknoteLabel;
	GUI_JButton printReceiptButton;

	private static String HeaderText = "Attendant Screen";

	public AttendantStationScreen(final StationControl sc) {
		super(sc, HeaderText);
		this.sc = sc;
		bc = sc.getBagsControl();
		bc.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);
		
		sc.getItemsControl().addListener(this);
		
		// Initialize attendant buttons
		approveAddedBagsButton = initializeButton("Approve Added Bags", "approve added bags", cusAddedBags);
		approveNoBagging = initializeButton("Approve no bagging", "approve no bag", false);
		startUpButton = initializeButton("Start up station", "startUp", true);
		shutDownButton = initializeButton("Shut down station", "shutdown", true);
		permitButton = initializeButton("Permit station use", "permit_use", false);
		preventButton = initializeButton("Prevent station use", "prevent_use", true);
		addItemButton = initializeButton("Add item", "add", true);
		//removeItemButton = initializeButton("Remove item", "remove", true);
		logoutButton = initializeButton("Logout", "logout", true);
		
		removeItemPanel = new GUI_JPanel();
		removeItemPanel.setLayout(new GridLayout(2, 1));
		
		removeItemTextField = new JTextField();
		removeItemTextField.setEditable(false);
		removeItemTextField.setSize(new Dimension(width, height));
		
		removeItemButton = makeButton("Remove item");
		removeItemButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean success = false;
				try {
					success = ac.removeItem(Integer.parseInt(removeItemTextField.getText()));
					removeItemTextField.setText("");
				} catch (NumberFormatException e1) {
					removeItemTextField.setText("Please Enter Valid Number");
				}
				
				if(!success) {
					removeItemTextField.setText("Invalid Item Number");
				}
			}
		});
		
//		removeItemButton.addActionListener(ac);
		removeItemButton.setPreferredSize(new Dimension(width, height));
		removeItemButton.setEnabled(false);
		
		removeItemPanel.add(removeItemTextField);
		removeItemPanel.add(removeItemButton);

		// Initialize notifications labels
		weightDescrepancyMssg = initializeLabel("Weight Discrepancy");
		weightDisplayLabel = initializeLabel("Weight Display");
		inkLabel = initializeLabel("Ink status");
		paperLabel = initializeLabel("Paper status");
		adjustCoinLabel = initializeLabel("Adjust coin");
		adjustBanknoteLabel = initializeLabel("Adjust Banknote");
		
		// Notification Panel for JLabels that contain notification messages from system
		GUI_JPanel notificationPanel = new GUI_JPanel();
		notificationPanel.setLayout(new GridLayout(1, 6));
		
		notificationPanel.add(weightDescrepancyMssg);
		notificationPanel.add(weightDisplayLabel);
		notificationPanel.add(inkLabel);
		notificationPanel.add(paperLabel);
		notificationPanel.add(adjustCoinLabel);
		notificationPanel.add(adjustBanknoteLabel);
		
		this.addLayer(notificationPanel, 0);

		GridBagConstraints panelGrid = new GridBagConstraints();
		GUI_JPanel buttonPanel = new GUI_JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		
		panelGrid.gridx = 0;
		panelGrid.gridy = 0;
		panelGrid.fill = GridBagConstraints.BOTH;
		buttonPanel.add(approveAddedBagsButton, panelGrid);
		
		panelGrid.gridx = 1;
		buttonPanel.add(startUpButton, panelGrid);
		
		panelGrid.gridx = 2;
		buttonPanel.add(permitButton, panelGrid);
		
		panelGrid.gridx = 3;
		buttonPanel.add(addItemButton, panelGrid);

		panelGrid.gridy = 1;
		panelGrid.gridx = 0;
		buttonPanel.add(approveNoBagging, panelGrid);
		
		panelGrid.gridx = 1;
		buttonPanel.add(shutDownButton, panelGrid);
		
		panelGrid.gridx = 2;
		buttonPanel.add(preventButton, panelGrid);
		
		//panelGrid.gridx = 3;
		//buttonPanel.add(removeItemPanel, panelGrid);
		
		buttonPanel.setPanelBorder(10, 10, 10, 10); 
		buttonPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		this.addLayer(buttonPanel, 150);
		this.addLayer(removeItemPanel, 10);
		this.addLayer(logoutButton, 10);
		
//		// FIXME: Used for testing. Remove before submission.
//		this.printReceiptButton = makeCentralButton("PRINT RECEIPT", this.width - 200, 25);
//		printReceiptButton.setActionCommand("printReceipt");
//		printReceiptButton.addActionListener(systemControl.getReceiptControl());
//		this.addLayer(printReceiptButton, 0);
	}
	
	
	private GUI_JLabel initializeLabel(String labelText) {

		GUI_JLabel label = new GUI_JLabel();
		label.setText(labelText);
		label.setForeground(GUI_Color_Palette.WHITE);
		label.setBackground(GUI_Color_Palette.LIGHT_BROWN);
		label.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
		label.setFont(GUI_Fonts.ATTENDANT_LABELS);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));

		return label;
	}
	
	private GUI_JButton initializeButton(String buttonText, String buttonCommand, boolean enabled) {
		
		GUI_JButton button = new GUI_JButton();
		
		button = makeButton(buttonText.toUpperCase());
		button.setActionCommand(buttonCommand);
		button.setFont(GUI_Fonts.ATTENDANT_BUTTONS);
		button.addActionListener(ac);
		button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		button.setEnabled(enabled);
		
		return button;
	}
	
//	private JScrollPane initializeScrollPane(GUI_JButton b1, GUI_JButton b2) {
//		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
//		scrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
//		scrollPane.setPreferredSize(new Dimension(200, 100));
//		scrollPane.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
//		
//		
//		GUI_JPanel buttonsPanel	= new GUI_JPanel();
//		buttonsPanel.setLayout(new GridLayout(2, 1));
//		scrollPane.getViewport().add(buttonsPanel);
//		
//		buttonsPanel.add(b1);
//		buttonsPanel.add(b2);
//		
//		return scrollPane;
//	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		approveAddedBagsButton.setEnabled(true);
		weightDisplayLabel.setText("Weight on station scale: " + sc.getWeightOfLastItemAddedToBaggingArea() + "g");
	}

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		approveAddedBagsButton.setEnabled(false);
		weightDisplayLabel.setText("");
	}

	@Override
	public void attendantPreventUse(AttendantControl ac) {
		preventButton.setEnabled(false);
		permitButton.setEnabled(true);
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		approveAddedBagsButton.setEnabled(false);
		weightDisplayLabel.setText("");
	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub

	}

	private GUI_JButton makeButton(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton button = new GUI_JButton();
		button.setText(text);
		button.setBackground(GUI_Color_Palette.DARK_BROWN);
		button.setForeground(GUI_Color_Palette.WHITE);

		button.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		button.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		button.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return button;
	}

//	@Override
//	public void addPaperState() {
//		approveAddedBagsButton.setEnabled(false);
//		
//	}
//
//	@Override
//	public void addInkState() {
//		approveAddedBagsButton.setEnabled(false);
//		
//	}
//
//	@Override
//	public void printerNotLowState() {
//		approveAddedBagsButton.setEnabled(false);
//		inkLabel.setText("Ink status");
//		paperLabel.setText("Paper status");
//	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		weightDescrepancyMssg.setText(updateMessage);
		
	}

	@Override
	public void noBagRequest() {
		approveAddedBagsButton.setEnabled(false);
		approveNoBagging.setEnabled(true);
	}

	@Override
	public void initialState() {
		approveAddedBagsButton.setEnabled(false);
		approveNoBagging.setEnabled(false);
		weightDescrepancyMssg.setText("");
	}
	
	public static void main(String args[]) {
		StationControl sc = new StationControl();
		AttendantStationScreen ap = new AttendantStationScreen(sc);
		ap.openInNewJFrame();
	}


	@Override
	public void lowInk(AttendantControl ac, String message) {
		inkLabel.setText(message);
		inkLabel.setBackground(GUI_Color_Palette.RED_BROWN);
	}


	@Override
	public void lowPaper(AttendantControl ac, String message) {
		paperLabel.setText(message);
		paperLabel.setBackground(GUI_Color_Palette.RED_BROWN);
	}


	@Override
	public void outOfInk(AttendantControl ac, String message) {
		inkLabel.setText(message);
		inkLabel.setBackground(GUI_Color_Palette.RED_BROWN);	
	}


	@Override
	public void outOfPaper(AttendantControl ac, String message) {
		paperLabel.setText(message);
		paperLabel.setBackground(GUI_Color_Palette.RED_BROWN);
	}


	@Override
	public void coinIsLowState(CoinStorageUnit unit, int amount) {
		// TODO Auto-generated method stub
	}


	@Override
	public void loggedIn(boolean isLoggedIn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		preventButton.setEnabled(true);
		permitButton.setEnabled(false);
		
	}


	@Override
	public void attendantApprovedItemRemoval(AttendantControl bc) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void itemWasSelected(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void awaitingItemToBePlacedInScanningArea(StationControl sc) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {
		removeItemButton.setEnabled(false);
		removeItemTextField.setEditable(false);
	}


	@Override
	public void itemsHaveBeenUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void productSubtotalUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void awaitingAttendantToApproveItemRemoval(ItemsControl ic) {
		removeItemButton.setEnabled(true);
		removeItemTextField.setEditable(true);
		
	}


	@Override
	public void itemRemoved(ItemsControl itemsControl) {
		removeItemButton.setEnabled(false);
		removeItemTextField.setEditable(false);
	}
		
	public void itemBagged() {
		approveNoBagging.setEnabled(false);
	}


	@Override
	public void printerNotLowInkState() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void printerNotLowPaperState() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addTooMuchInkState() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addTooMuchPaperState() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void banknotesInStorageLowState() {
		// TODO Auto-generated method stub
		
	}
}
