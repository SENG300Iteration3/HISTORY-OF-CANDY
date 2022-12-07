package swing.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ReceiptControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

/**
 * This class isn't linked to anything. Should it still be kept?
 *
 */
public class AttendantPanel extends JPanel implements AttendantControlListener, BagsControlListener {

	private static final long serialVersionUID = 1L;
	private StationControl sc;
	private BagsControl bc;
	private AttendantControl ac;
	private boolean cusAddedBags = false;
	JButton approveAddedBagsButton;
	JButton addInkToPrinterButton;
	JButton addPaperToPrinterButton;
	JButton approveNoBagButton;
	JLabel weightDisplayLabel, weightDescrepancyMssg;
	GridBagConstraints grid = new GridBagConstraints();

	public AttendantPanel(StationControl sc) {
		super();
		this.sc = sc;
		bc = sc.getBagsControl();
		bc.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);

		approveAddedBagsButton = new JButton("approvedAddedBags(()");
		approveAddedBagsButton.setActionCommand("approve added bags");
		approveAddedBagsButton.addActionListener(ac);

		addInkToPrinterButton = new JButton("Add ink");
		addInkToPrinterButton.setActionCommand("addInk");
		addInkToPrinterButton.addActionListener(ac);

		addPaperToPrinterButton = new JButton("Add paper");
		addPaperToPrinterButton.setActionCommand("addPaper");
		addPaperToPrinterButton.addActionListener(ac);

		approveNoBagButton = new JButton("Approve no bagging");
		approveNoBagButton.setActionCommand("approve");
		approveNoBagButton.addActionListener(ac);

		weightDisplayLabel = new JLabel("");
		weightDescrepancyMssg = new JLabel();

		grid.gridx = 0;
		grid.gridy = 0;
		this.add(weightDisplayLabel);
		grid.gridy = 1;
		this.add(approveAddedBagsButton);
		grid.gridy = 2;
		this.add(addInkToPrinterButton);
		grid.gridy = 3;
		this.add(addPaperToPrinterButton);
		grid.gridy = 4;
		this.add(approveNoBagButton);

		this.add(weightDescrepancyMssg);

		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);
		approveNoBagButton.setEnabled(false);

		approveAddedBagsButton.setEnabled(cusAddedBags);
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
		approveNoBagButton.setEnabled(true);
	}

	@Override
	public void initialState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);
		approveNoBagButton.setEnabled(false);
		weightDescrepancyMssg.setText("");
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
	public void coinIsLowState(int amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loggedIn(boolean isLoggedIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemBagged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknotesInStorageLowState() {
		// TODO Auto-generated method stub
		
	}
}
