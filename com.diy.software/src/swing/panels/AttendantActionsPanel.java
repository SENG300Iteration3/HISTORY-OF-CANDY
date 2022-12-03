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
	JButton inkButton, paperButton, bagDispenserButton;
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
		
		inkButton =  new JButton("Refill Ink Dispenser");
//		inkButton.setActionCommand("refill ink");
//		inkButton.addActionListener(ac);
		
		paperButton = new JButton("Refill Paper Dispenser");
//		paperButton.setActionCommand("refill paper");
//		paperButton.addActionListener(ac);
		
		bagDispenserButton = new JButton("Refill Bag Dispenser");
//		bagDispenserButton.setActionCommand("refill bag dispenser");
//		bagDispenserButton.addActionListener(ac);
		
		this.setLayout(new GridBagLayout());

		buttonGrid.gridx = 0;
		buttonGrid.gridy = 0;
		this.add(inkButton, buttonGrid);

		buttonGrid.gridx = 1;
		this.add(paperButton, buttonGrid);
		
		buttonGrid.gridx = 2;
		this.add(bagDispenserButton, buttonGrid);

//		Can add messages here
//		buttonGrid.gridy = 1;
//		buttonGrid.gridx = 3;
//		this.add(Label);
//		

		inkButton.setEnabled(false);
		paperButton.setEnabled(false);
		bagDispenserButton.setEnabled(false);
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
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {

	}

	@Override
	public void addPaperState() {}
	public void itemsHaveBeenUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInkState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printerNotLowState() {
		
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
}
