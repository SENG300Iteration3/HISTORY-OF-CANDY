package swing.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.ReceiptControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

public class CustomerBagsPanel extends JPanel
		implements BagsControlListener, ItemsControlListener, AttendantControlListener {

	private static final long serialVersionUID = 1L;
	private BagsControl bc;
	private AttendantControl ac;
	private ItemsControl ic;
	JButton addBagsButton, doneAddingBagsButton, purchaseBagsButton;
	JLabel addBagsLabel;
	GridBagConstraints buttonGrid = new GridBagConstraints();

	public CustomerBagsPanel(StationControl sc) {
		super();
		bc = sc.getBagsControl();
		bc.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);

		ic = sc.getItemsControl();
		ic.addListener(this);

		addBagsButton = new JButton("addOwnBagsInBaggingArea()");
		addBagsButton.setActionCommand("add bags");
		addBagsButton.addActionListener(bc);

		addBagsLabel = new JLabel("");

		doneAddingBagsButton = new JButton("doneAddOwnBagsInBaggingArea()");
		doneAddingBagsButton.setActionCommand("done adding bags");
		doneAddingBagsButton.addActionListener(bc);

		purchaseBagsButton = new JButton("purchaseBags()");
		purchaseBagsButton.setActionCommand("purchase bags");
		purchaseBagsButton.addActionListener(bc);

		buttonGrid.gridy = 0;
		buttonGrid.gridx = 0;
		this.add(addBagsButton, buttonGrid);

		buttonGrid.gridx = 1;
		this.add(addBagsLabel, buttonGrid);

		buttonGrid.gridx = 2;
		this.add(doneAddingBagsButton, buttonGrid);

		buttonGrid.gridx = 3;
		this.add(purchaseBagsButton, buttonGrid);

		addBagsButton.setEnabled(true);
		purchaseBagsButton.setEnabled(true);
		doneAddingBagsButton.setEnabled(false);
	}

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		addBagsButton.setEnabled(true);
		addBagsLabel.setText("");
		doneAddingBagsButton.setEnabled(false);
		purchaseBagsButton.setEnabled(true);
	}

	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
		addBagsButton.setEnabled(true);
		purchaseBagsButton.setEnabled(true);
		doneAddingBagsButton.setEnabled(false);
	}

	@Override
	public void itemWasSelected(ItemsControl ic) {
		addBagsButton.setEnabled(false);
		purchaseBagsButton.setEnabled(false);
		doneAddingBagsButton.setEnabled(false);
	}

	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
		addBagsButton.setEnabled(false);
		purchaseBagsButton.setEnabled(false);
		doneAddingBagsButton.setEnabled(false);
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
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		addBagsButton.setEnabled(false);
		addBagsLabel.setText("Please place your bags in the bagging area");
		doneAddingBagsButton.setEnabled(true);
		purchaseBagsButton.setEnabled(false);
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		addBagsButton.setEnabled(false);
		addBagsLabel.setText("");
		doneAddingBagsButton.setEnabled(false);
		purchaseBagsButton.setEnabled(false);
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {}
	
	@Override
	public void itemsHaveBeenUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPaperState() {}
	public void productSubtotalUpdated(ItemsControl ic) {
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
	public void coinIsLowState(CoinStorageUnit unit, int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingItemToBePlacedInScanningArea(StationControl sc) {
	}

	@Override
	public void loggedIn(boolean isLoggedIn) {
		// TODO Auto-generated method stub
		
	}
}
