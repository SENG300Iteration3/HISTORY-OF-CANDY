package swing.panels;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.diy.software.controllers.SystemControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.listeners.WalletControlListener;
import com.jimmyselectronics.opeechee.Card;

import swing.GUI_Color_Palette;

public class DebugWalletPanel extends JPanel implements WalletControlListener {

	private static final long serialVersionUID = 1L;
	private WalletControl wc;
	JRadioButton cc1, cc2, cc3;
	JButton insertOrEjectButton, tapButton, swipeButton;

	private boolean aCardIsSelected = false;
	private boolean paymentsEnabled = false;

	public DebugWalletPanel(SystemControl sc) {
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

		cc1.setSelected(true);

		cc1.addActionListener(wc);
		cc2.addActionListener(wc);
		cc3.addActionListener(wc);

		ButtonGroup ccButtonGroup = new ButtonGroup();
		ccButtonGroup.add(cc1);
		ccButtonGroup.add(cc2);
		ccButtonGroup.add(cc3);
		ccButtonGroup.clearSelection();

		this.setBackground(GUI_Color_Palette.DARK_BLUE);
		this.add(cc1);
		this.add(cc2);
		this.add(cc3);
		this.add(insertOrEjectButton);
		this.add(tapButton);
		this.add(swipeButton);
	}

	private void updateButtonStates() {
		insertOrEjectButton.setEnabled(aCardIsSelected && paymentsEnabled);
		tapButton.setEnabled(aCardIsSelected && paymentsEnabled);
		swipeButton.setEnabled(aCardIsSelected && paymentsEnabled);
	}

	@Override
	public void cardHasBeenSelected(WalletControl wc) {
		aCardIsSelected = true;
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
}
