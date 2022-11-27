package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.CashControlListener;

import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.Screen;

public class PresentCashScreen extends Screen implements CashControlListener {

	private GUI_JLabel prompt;
	private GUI_JButton backButton;
	
	public PresentCashScreen(final StationControl systemControl) {
		super(systemControl);
		
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
	public void cashInsertionEnabled(CashControl cc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cashInsertionDisabled(CashControl cc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cashInserted(CashControl cc) {
		this.prompt.setText("Please insert $" + systemControl.getItemsControl().getCheckoutTotal());
	}
}
