package swing.panes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class AttendantStationPane extends Screen implements AttendantControlListener, BagsControlListener {

	private StationControl sc;
	private BagsControl bc;
	private AttendantControl ac;
	private boolean cusAddedBags = false;
	GUI_JButton approveAddedBagsButton;
	GUI_JButton addInkToPrinterButton;
	GUI_JButton addPaperToPrinterButton;
	GUI_JButton approveNoBagging;
	GUI_JButton logoutButton;
	GUI_JLabel weightDisplayLabel, weightDescrepancyMssg;

	private static String HeaderText = "Attendant Screen";

	public AttendantStationPane(StationControl sc) {
		super(sc, HeaderText);
		this.sc = sc;
		bc = sc.getBagsControl();
		bc.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);

		int width = 450;
		int height = 50;

		approveAddedBagsButton = createPinPadButton("approvedAddedBags()");
		approveAddedBagsButton.setActionCommand("approve added bags");
		approveAddedBagsButton.addActionListener(ac);
		approveAddedBagsButton.setPreferredSize(new Dimension(width, height));

		addInkToPrinterButton = createPinPadButton("Add ink");
		addInkToPrinterButton.setActionCommand("addInk");
		addInkToPrinterButton.addActionListener(ac);
		addInkToPrinterButton.setPreferredSize(new Dimension(width, height));
		
		addPaperToPrinterButton = createPinPadButton("Add paper");
		addPaperToPrinterButton.setActionCommand("addPaper");
		addPaperToPrinterButton.addActionListener(ac);
		addPaperToPrinterButton.setPreferredSize(new Dimension(width, height));
		
		approveNoBagging = createPinPadButton("Approve no bagging");
		approveNoBagging.setActionCommand("approve no bag");
		approveNoBagging.addActionListener(ac);
		approveNoBagging.setPreferredSize(new Dimension(width, height));
		
		logoutButton = createPinPadButton("Logout");
		logoutButton.setActionCommand("logout");
		logoutButton.addActionListener(ac);
		logoutButton.setPreferredSize(new Dimension(width, height));
		
		weightDescrepancyMssg = initalizeLabel();
		weightDisplayLabel = initalizeLabel();
		
		addLayer(approveAddedBagsButton, 0);
		addLayer(addInkToPrinterButton,0);
		addLayer(addPaperToPrinterButton,0);
		addLayer(approveNoBagging,0);
		addLayer(logoutButton, 0);
		addLayer(weightDescrepancyMssg,0);
		
		
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);
		approveNoBagging.setEnabled(false);
		approveAddedBagsButton.setEnabled(cusAddedBags);
	}

	private GUI_JLabel initalizeLabel() {

		GUI_JLabel label = new GUI_JLabel();
		label.setForeground(GUI_Color_Palette.WHITE);
		label.setPreferredSize(new Dimension(this.width - 200, 100));
		label.setFont(GUI_Fonts.FRANKLIN_BOLD);
		label.setHorizontalAlignment(JLabel.CENTER);

		addLayer(label, 0);

		return label;
	}

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
		// TODO: implement method
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

	private GUI_JButton createPinPadButton(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton pinPadButton = new GUI_JButton();
		pinPadButton.setText(text);
		pinPadButton.setBackground(GUI_Color_Palette.DARK_BROWN);
		pinPadButton.setForeground(GUI_Color_Palette.WHITE);

		pinPadButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		pinPadButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		pinPadButton.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return pinPadButton;
	}

	@Override
	public void addPaperState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(true);
	}

	@Override
	public void addInkState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(true);
		addPaperToPrinterButton.setEnabled(false);	
	}

	@Override
	public void printerNotLowState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		weightDescrepancyMssg.setText(updateMessage);
		
	}

	@Override
	public void noBagRequest() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(true);
	}

	@Override
	public void initialState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(false);
		weightDescrepancyMssg.setText("");
	}

	@Override
	public void loggedIn(boolean isLoggedIn) {
		// TODO Auto-generated method stub
		
	}
}
