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

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class PresentGiftCardOrCashScreen extends Screen implements CashControlListener {

	private GUI_JLabel prompt;
	private GUI_JLabel coinAvailability;
	private GUI_JLabel noteAvailability;
	private GUI_JLabel message;
	private GUI_JButton backButton;
	public double lastRecievedCash;
	private boolean isGiftCard;
	
	public PresentGiftCardOrCashScreen(final StationControl systemControl, boolean isGiftCard) {
		super(systemControl);
		
		lastRecievedCash = 0;
		
		systemControl.getCashControl().enablePayments();
		systemControl.getCashControl().addListener(this);

		this.isGiftCard = isGiftCard;

		cashInserted(null);
		
		prompt.setFont(GUI_Fonts.FRANKLIN_BOLD);
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(prompt, 0);

		this.coinAvailability = new GUI_JLabel();
		this.noteAvailability = new GUI_JLabel();
		this.message = new GUI_JLabel();
		coinAvailability.setFont(GUI_Fonts.FRANKLIN_BOLD);
		noteAvailability.setFont(GUI_Fonts.FRANKLIN_BOLD);
		message.setFont(GUI_Fonts.FRANKLIN_BOLD);
		coinAvailability.setHorizontalAlignment(SwingConstants.LEFT);
		noteAvailability.setHorizontalAlignment(SwingConstants.LEFT);
		coinAvailability.setVerticalAlignment(SwingConstants.TOP);
		noteAvailability.setVerticalAlignment(SwingConstants.TOP);
		message.setHorizontalAlignment(SwingConstants.CENTER);
		coinAvailability.setPreferredSize(new Dimension(this.width - 200, 100));
		noteAvailability.setPreferredSize(new Dimension(this.width - 200, 100));
		message.setPreferredSize(new Dimension(this.width - 200, 100));
		coinAvailability.setForeground(GUI_Color_Palette.BLACK);
		noteAvailability.setForeground(GUI_Color_Palette.BLACK);
		message.setForeground(GUI_Color_Palette.BLACK);
		this.addLayer(coinAvailability, 0);
		this.addLayer(noteAvailability, 0);
		this.addLayer(message, 0);
		
		this.backButton = makeCentralButton("BACK", this.width - 200, 100);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeThis();
				systemControl.getCashControl().disablePayments();
				systemControl.goBackOnUI();
			}
		});
		this.addLayer(backButton, 100);
	}
	
	public void removeThis() {
		systemControl.getCashControl().removeListener(this);
	}

	@Override
	public void cashInserted(CashControl cc) {
		if (isGiftCard) {
			this.prompt = new GUI_JLabel("Please swipe gift card. Total remaining: $" + fix(systemControl.getItemsControl().getCheckoutTotal()));
		} else {
			this.prompt = new GUI_JLabel("Please insert $" + fix(systemControl.getItemsControl().getCheckoutTotal()));
		}
	}
	
	public String fix(double d) { //fixes a bug where more than 2 decimals of the remaining cost will show up
		String n = String.valueOf(d);
		String ret = "";
		int l = -1;
		for(char i : n.toCharArray()) {
			ret += i;
			if(l == 0) {
				break;
			}
			if(i == '.') {
				l = 2;
			}
			if(l != -1) {
				l--;
			}
		}
		return ret;
	}

	@Override
	public void coinInsertionEnabled(CashControl cc) {
		if(!isGiftCard) {
			this.coinAvailability.setText("");
		}
		
	}

	@Override
	public void noteInsertionEnabled(CashControl cc) {
		if(!isGiftCard) {
			this.noteAvailability.setText("");
		}
		
	}

	@Override
	public void coinInsertionDisabled(CashControl cc) {
		if(!isGiftCard) {
			this.coinAvailability.setText("Coins are currently disabled.");
		}
		
	}

	@Override
	public void noteInsertionDisabled(CashControl cc) {
		if(!isGiftCard) {
			this.noteAvailability.setText("Banknotes are currently disabled.");
		}
		
	}

	@Override
	public void cashRejected(CashControl cc) {
		if(!isGiftCard) {
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
			message.setText("Input cash was rejected. You recieved $" + returnedCash + " back from the machine");
			lastRecievedCash = returnedCash;
			System.out.println("Input cash was rejected. You recieved $" + returnedCash + " back from the machine");
		}
	}

	@Override
	public void changeReturned(CashControl cc) {
		if(!isGiftCard) {
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
			message.setText("You recieved $" + returnedCash + " as change from the machine");
			lastRecievedCash = returnedCash;
			System.out.println("You recieved $" + returnedCash + " as change from the machine");
		}
	}
}
