package swing.panels;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.opeechee.Card;

import swing.styling.GUI_Color_Palette;

public class CustomerWalletPanel extends JPanel implements WalletControlListener {

	private static final long serialVersionUID = 1L;
	private WalletControl wc;
	JRadioButton cc1, cc2, cc3, m, gc;
	JButton insertOrEjectButton, tapButton, swipeButton, scanMemButton;

	private boolean aCardIsSelected = false;
	private boolean paymentsEnabled = false;
	private boolean membershipCardInputEnabled = false;
	private boolean membershipCardSelected = false;

	public CustomerWalletPanel(StationControl sc) {
		super();
		wc = sc.getWalletControl();
		wc.addListener(this);

		List<Card> cards = sc.customer.wallet.cards;
		cc1 = new JRadioButton(cards.get(0).kind);
		cc1.setActionCommand("cc0");
		cc2 = new JRadioButton(cards.get(1).kind);
		cc2.setActionCommand("cc1");
		cc3 = new JRadioButton(cards.get(2).kind);
		cc3.setActionCommand("cc2");
		
		m = new JRadioButton(cards.get(3).kind);
		m.setActionCommand("m");
		
		gc = new JRadioButton(cards.get(4).kind);
		gc.setActionCommand("giftcard");

		insertOrEjectButton = new JButton("insert");
		insertOrEjectButton.setActionCommand("insert");
		insertOrEjectButton.addActionListener(wc);
		insertOrEjectButton.setEnabled(false);

		tapButton = new JButton("tap");
		tapButton.setActionCommand("tap");
		tapButton.addActionListener(wc);
		tapButton.setEnabled(false);

		swipeButton = new JButton("swipe");
		swipeButton.setActionCommand("swipe");
		swipeButton.addActionListener(wc);
		swipeButton.setEnabled(false);
		
		scanMemButton = new JButton("scan membership card");
		scanMemButton.setActionCommand("scan");
		scanMemButton.addActionListener(wc);
		scanMemButton.setEnabled(false);

		cc1.setSelected(true);

		cc1.addActionListener(wc);
		cc2.addActionListener(wc);
		cc3.addActionListener(wc);
		m.addActionListener(wc); //CORRECT CONTROLLER
		gc.addActionListener(wc);

		ButtonGroup ccButtonGroup = new ButtonGroup();
		ccButtonGroup.add(cc1);
		ccButtonGroup.add(cc2);
		ccButtonGroup.add(cc3);
		ccButtonGroup.add(m);
		ccButtonGroup.add(gc);
		ccButtonGroup.clearSelection();

		this.setBackground(GUI_Color_Palette.DARK_BLUE);
		this.add(cc1);
		this.add(cc2);
		this.add(cc3);
		this.add(m);
		this.add(gc);
		this.add(insertOrEjectButton);
		this.add(tapButton);
		this.add(swipeButton);
		this.add(scanMemButton);
	}

	private void updateButtonStates() {
		if (membershipCardSelected) {
			if (membershipCardInputEnabled) {
				swipeButton.setEnabled(true);
				scanMemButton.setEnabled(true);
				insertOrEjectButton.setEnabled(false);
				tapButton.setEnabled(false);
			} else {
				swipeButton.setEnabled(false);
				scanMemButton.setEnabled(false);
				insertOrEjectButton.setEnabled(false);
				tapButton.setEnabled(false);
			}
		} else {
			insertOrEjectButton.setEnabled(aCardIsSelected && paymentsEnabled);
			tapButton.setEnabled(aCardIsSelected && paymentsEnabled);
			swipeButton.setEnabled(aCardIsSelected && paymentsEnabled);
			scanMemButton.setEnabled(false);
		}
	}

	@Override
	public void cardHasBeenSelected(WalletControl wc) {
		aCardIsSelected = true;
		membershipCardSelected = false;
		updateButtonStates();
	}
	
	@Override
	public void membershipCardInputEnabled(WalletControl wc) {
		membershipCardInputEnabled = true;
		updateButtonStates();
	}
	
	@Override
	public void membershipCardInputCanceled(WalletControl walletControl) {
		membershipCardInputEnabled = false;
		updateButtonStates();
	}
	
	@Override
	public void membershipCardHasBeenSelected(WalletControl wc) {
		membershipCardSelected = true;
		updateButtonStates();
	}

	@Override
	public void cardPaymentsEnabled(WalletControl wc) {
		paymentsEnabled = true;
		updateButtonStates();
	}

	@Override
	public void cardPaymentsDisabled(WalletControl wc) {
		paymentsEnabled = false;
		updateButtonStates();
	}

	@Override
	public void cardHasBeenInserted(WalletControl wc) {
		// TODO Auto-generated method stub
		paymentsEnabled = false;
		updateButtonStates();
	}

	@Override
	public void cardWithPinInserted(WalletControl wc) {
		insertOrEjectButton.setText("remove");
		insertOrEjectButton.setActionCommand("remove");
		insertOrEjectButton.setEnabled(true);
	}

	@Override
	public void cardWithPinRemoved(WalletControl wc) {
		insertOrEjectButton.setText("insert");
		insertOrEjectButton.setActionCommand("insert");
		updateButtonStates();
	}

	public JRadioButton getCc1() {
		return cc1;
	}

	public JRadioButton getCc2() {
		return cc2;
	}

	public JRadioButton getCc3() {
		return cc3;
	}

	public JRadioButton getM() {
		return m;
	}

	public JButton getTapButton() {
		return tapButton;
	}

	public JButton getSwipeButton() {
		return swipeButton;
	}

	public JButton getScanMemButton() {
		return scanMemButton;
	}

	public JRadioButton getGc() {
		return gc;
	}

	public JButton getInsertOrEjectButton() {
		return insertOrEjectButton;
	}
	
}
