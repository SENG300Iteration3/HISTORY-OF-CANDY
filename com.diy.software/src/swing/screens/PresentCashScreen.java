package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingConstants;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.CashControlListener;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.coin.Coin;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class PresentCashScreen extends Screen implements CashControlListener {

	private GUI_JLabel prompt;
	private GUI_JLabel coinAvailability;
	private GUI_JLabel noteAvailability;
	private GUI_JLabel message;
	private GUI_JButton backButton;
	public double lastRecievedCash;
	
	public PresentCashScreen(final StationControl systemControl) {
		super(systemControl);
		
		lastRecievedCash = 0;
		
		systemControl.getCashControl().enablePayments();
		systemControl.getCashControl().addListener(this);

		this.prompt = new GUI_JLabel("Please insert $" + systemControl.getItemsControl().getCheckoutTotal());
		prompt.setFont(GUI_Fonts.FRANKLIN_BOLD);
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(prompt, 0);

		this.backButton = makeCentralButton("BACK", this.width - 200, 100);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				systemControl.getCashControl().disablePayments();
				systemControl.goBackOnUI();
			}
		});
		this.addLayer(backButton, 100);
	}

	@Override
	public void cashInserted(CashControl cc) {
		this.prompt.setText("Please insert $" + systemControl.getItemsControl().getCheckoutTotal());
	}

	@Override
	public void coinInsertionEnabled(CashControl cc) {
		this.coinAvailability.setText("");
		
	}

	@Override
	public void noteInsertionEnabled(CashControl cc) {
		this.noteAvailability.setText("");
		
	}

	@Override
	public void coinInsertionDisabled(CashControl cc) {
		this.coinAvailability.setText("Coins are currently disabled.");
		
	}

	@Override
	public void noteInsertionDisabled(CashControl cc) {
		this.coinAvailability.setText("Banknotes are currently disabled.");
		
	}

	@Override
	public void cashRejected(CashControl cc) {
		List<Coin> c = systemControl.station.coinTray.collectCoins();
		Banknote b = null; 
		if(systemControl.station.banknoteInput.hasDanglingBanknotes()) {
			b = systemControl.station.banknoteInput.removeDanglingBanknote();
		}
		double returnedCash = 0;
		for(Coin i : c) {
			returnedCash += ((double)i.getValue())/100.0;
		}
		if(b != null) {
			returnedCash += b.getValue();
		}
		message.setText("Input cash was rejected. You recieved " + returnedCash + " back from the machine");
		lastRecievedCash = returnedCash;
	}

	@Override
	public void changeReturned(CashControl cc) {
		List<Coin> c = systemControl.station.coinTray.collectCoins();
		List<Banknote> b = null;
		if(systemControl.station.banknoteOutput.hasDanglingBanknotes()) {
			b = systemControl.station.banknoteOutput.removeDanglingBanknotes();
		}
		double returnedCash = 0;
		for(Coin i : c) {
			returnedCash += ((double)i.getValue())/100.0;
		}
		if(b != null) {
			for(Banknote i : b) {
				returnedCash += i.getValue();
			}
		}
		message.setText("You recieved " + returnedCash + " change from the machine");
		lastRecievedCash = returnedCash;
	}
}
