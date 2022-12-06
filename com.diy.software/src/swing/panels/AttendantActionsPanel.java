package swing.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
import com.diy.software.listeners.ReceiptControlListener;
import com.unitedbankingservices.coin.CoinStorageUnit;

public class AttendantActionsPanel extends JPanel
		implements ItemsControlListener, AttendantControlListener, BagsControlListener, ReceiptControlListener {

	private static final long serialVersionUID = 1L;
	private ItemsControl ic;
	private AttendantControl ac; 
	private BagsControl bc;
	private ReceiptControl rc;
	private boolean stationBlocked = true; // FIXME: Testing now. Should be false to start
	
	JButton inkButton, paperButton, bagDispenserButton, coinButton, banknoteButton, outOfOrderButton;
	GridBagConstraints buttonGrid = new GridBagConstraints();

	public AttendantActionsPanel(StationControl sc) {
		super();
		ic = sc.getItemsControl();
		ic.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);

		bc = sc.getBagsControl();
		bc.addListener(this);
		
		rc = sc.getReceiptControl();
		rc.addListenerReceipt(this);

		inkButton = initializeButton("Refill Ink Dispenser", "addInk");
		paperButton = initializeButton("Refill Paper Dispenser", "addPaper");
		coinButton = initializeButton("Refill Coin Dispenser", "addCoin");
		banknoteButton = initializeButton("Refill Banknote Dispenser", "addBanknote");
		bagDispenserButton = initializeButton("Refill Bag Dispenser", "addBag");
		outOfOrderButton = initializeButton("Out Of Order", "outOfOrder");
		
		this.setLayout(new GridBagLayout());
 
		buttonGrid.gridx = 0;
		buttonGrid.gridy = 0;
		this.add(inkButton, buttonGrid);

		buttonGrid.gridx = 1;
		this.add(paperButton, buttonGrid);
		
		buttonGrid.gridx = 2;
		this.add(coinButton, buttonGrid);

		buttonGrid.gridx = 3;
		this.add(banknoteButton, buttonGrid);
		
		buttonGrid.gridx = 4;
		this.add(bagDispenserButton, buttonGrid);
		
		buttonGrid.gridx = 5;
		this.add(outOfOrderButton, buttonGrid);
		
		bagDispenserButton.setEnabled(true);		// attendant should be able to load bags anytime they want to
		
//		Can add messages here
//		buttonGrid.gridy = 1;
//		buttonGrid.gridx = 3;
//		this.add(Label);
//		
	}
	private JButton initializeButton(String msg, String command) {
		JButton button =  new JButton(msg);
		button.setActionCommand(command);
		button.addActionListener(ac);
		button.setEnabled(false);
		
		return button;
	}
	
	
	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
	}

	@Override
	public void itemWasSelected(ItemsControl ic) {
	}

	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
	}

	@Override
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
	}

	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		
	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
	}

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
	
	}

	@Override
	public void attendantPreventUse(AttendantControl ac) {
		// TODO Auto-generated method stub
		stationBlocked = true;
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {

	}

	@Override
	public void addPaperState() {
		if(stationBlocked) paperButton.setEnabled(true);
	}
	
	public void itemsHaveBeenUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInkState() {
		if (stationBlocked) inkButton.setEnabled(true);
	}

	@Override
	public void printerNotLowState() {
		// FIXME: This is called when ink is refilled and paper is refilled, may need to be split up.
		// In future, buttons should be disabled when the system is not blocked (permit station use) + this.
		// Also thinking that this should be called when the paper and ink is completely full.
		// Technically if an ink dispenser goes from 10% to 70% full it would be out of a low state, but you should still have option to add more ink to station.
		inkButton.setEnabled(false);
		paperButton.setEnabled(false);
	}
	
	@Override
	public void productSubtotalUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noBagRequest() {
	}

	@Override
	public void initialState() {
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
	public void outOfInkOrPaper(ReceiptControl rc, String message) {
	}

	@Override
	public void setCheckedoutItems(ReceiptControl rc, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTotalCost(ReceiptControl rc, String totalCost) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDateandTime(ReceiptControl rc, String dateTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setThankyouMessage(ReceiptControl rc, String dateTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTakeReceiptState(ReceiptControl rc) {
	}

	@Override
	public void setNoReceiptState(ReceiptControl rc) {
	}

	@Override
	public void attendantPermitStationUse(AttendantControl ac) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void coinIsLowState(CoinStorageUnit unit, int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingItemToBePlacedInScanningArea(StationControl sc) {
	}

	@Override
	public void loggedIn(boolean isLoggedIn) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void itemBagged() {
		// TODO Auto-generated method stub
		
	}
}
