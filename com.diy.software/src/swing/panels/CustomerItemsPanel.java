package swing.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;

public class CustomerItemsPanel extends JPanel
		implements ItemsControlListener, AttendantControlListener, BagsControlListener {

	private static final long serialVersionUID = 1L;
	private ItemsControl ic;
	private AttendantControl ac;
	private BagsControl bc;
	private boolean itemsAvailable;
	JButton selectNextItemButton, mainScannerButton, handheldScannerButton, deselectCurrentItemButton, placeItemInBaggingAreaButton;
	GridBagConstraints buttonGrid = new GridBagConstraints();
	JLabel weightDescrepancyMessage;

	public CustomerItemsPanel(StationControl sc) {
		super();
		ic = sc.getItemsControl();
		ic.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);

		bc = sc.getBagsControl();
		bc.addListener(this);
		
		weightDescrepancyMessage = new JLabel();
		
		selectNextItemButton = new JButton("selectNextItem()");
		selectNextItemButton.setActionCommand("pick up");
		selectNextItemButton.addActionListener(ic);

		mainScannerButton = new JButton("mainScanner()");
		mainScannerButton.setActionCommand("main scan");
		mainScannerButton.addActionListener(ic);
		
		handheldScannerButton = new JButton("handheldscanner()");
		handheldScannerButton.setActionCommand("handheld scan");
		handheldScannerButton.addActionListener(ic);

		deselectCurrentItemButton = new JButton("deselectCurrentItem()");
		deselectCurrentItemButton.setActionCommand("put back");
		deselectCurrentItemButton.addActionListener(ic);

		placeItemInBaggingAreaButton = new JButton("placeItemInBagginArea()");
		placeItemInBaggingAreaButton.setActionCommand("bag");
		placeItemInBaggingAreaButton.addActionListener(ic);

		this.setLayout(new GridBagLayout());

		buttonGrid.gridx = 0;
		buttonGrid.gridy = 0;
		this.add(selectNextItemButton, buttonGrid);

		buttonGrid.gridx = 1;
		this.add(mainScannerButton, buttonGrid);
		
		buttonGrid.gridx = 2;
		this.add(handheldScannerButton, buttonGrid);

		buttonGrid.gridx = 3;
		this.add(deselectCurrentItemButton, buttonGrid);

		buttonGrid.gridx = 4;
		this.add(placeItemInBaggingAreaButton, buttonGrid);
		
		buttonGrid.gridy = 1;
		buttonGrid.gridx = 5;
		this.add(weightDescrepancyMessage);
		

		// FIXME: should instead check customer cart if the shopping car is not zero
		this.itemsAvailable = true;

		selectNextItemButton.setEnabled(itemsAvailable);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(false);
	}

	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
		selectNextItemButton.setEnabled(itemsAvailable);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(false);
		weightDescrepancyMessage.setText("");
	}

	@Override
	public void itemWasSelected(ItemsControl ic) {
		selectNextItemButton.setEnabled(false);
		mainScannerButton.setEnabled(true);
		handheldScannerButton.setEnabled(true);
		deselectCurrentItemButton.setEnabled(true);
		placeItemInBaggingAreaButton.setEnabled(false);
	}

	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
		selectNextItemButton.setEnabled(false);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(true);
	}

	@Override
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
		itemsAvailable = false;
		selectNextItemButton.setEnabled(itemsAvailable);
	}

	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
		itemsAvailable = true;
		selectNextItemButton.setEnabled(itemsAvailable);
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		selectNextItemButton.setEnabled(false);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(false);
	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		selectNextItemButton.setEnabled(false);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(false);
	}

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		selectNextItemButton.setEnabled(itemsAvailable);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(false);
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
		selectNextItemButton.setEnabled(false);
		mainScannerButton.setEnabled(false);
		handheldScannerButton.setEnabled(false);
		deselectCurrentItemButton.setEnabled(false);
		placeItemInBaggingAreaButton.setEnabled(false);
		weightDescrepancyMessage.setText(updateMessage);
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
	public void printerNotLowState() {}
	public void productSubtotalUpdated(ItemsControl ic) {
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
}
