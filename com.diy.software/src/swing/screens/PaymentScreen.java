package swing.screens;

import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;

import swing.styling.GUI_JButton;
import swing.styling.Screen;

public class PaymentScreen extends Screen {
	private PaymentControl pc;

	private GUI_JButton giftCardButton;
	private GUI_JButton cashButton;
	private GUI_JButton creditButton;
	private GUI_JButton debitButton;
	private GUI_JButton cancelButton;

	public PaymentScreen(StationControl sc) {
		super(sc, "Select a payment method");

		this.giftCardButton = makeCentralButton("Gift Card", this.width - 200, 100);
		this.addLayer(giftCardButton, -100);

		this.cashButton = makeCentralButton("Cash", this.width - 200, 100);
		this.addLayer(cashButton, 0);

		this.creditButton = makeCentralButton("Credit", this.width - 200, 100);
		this.addLayer(creditButton, 0);

		this.debitButton = makeCentralButton("Debit", this.width - 200, 100);
		this.addLayer(debitButton, 0);

		this.cancelButton = makeCentralButton("Cancel", this.width / 2, 100);
		this.addLayer(cancelButton, 20);

		pc = sc.getPaymentControl();

		giftCardButton.setActionCommand("giftcard");
		cashButton.setActionCommand("cash");
		creditButton.setActionCommand("credit");
		debitButton.setActionCommand("debit");

		giftCardButton.addActionListener(pc);
		cashButton.addActionListener(pc);
		creditButton.addActionListener(pc);
		debitButton.addActionListener(pc);
		cancelButton.addActionListener(pc);
	}

	public GUI_JButton getGiftCardButton() {
		return giftCardButton;
	}

	public GUI_JButton getCashButton() {
		return cashButton;
	}

	public GUI_JButton getCreditButton() {
		return creditButton;
	}

	public GUI_JButton getDebitButton() {
		return debitButton;
	}

	public GUI_JButton getCancelButton() {
		return cancelButton;
	}

}
