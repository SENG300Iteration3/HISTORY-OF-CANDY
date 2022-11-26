package swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.SystemControl;
import com.diy.software.listeners.CashControlListener;

import swing.GUI_Fonts;
import swing.GUI_JButton;
import swing.GUI_JLabel;
import swing.Screen;

public class PresentCashScreen extends Screen implements CashControlListener {

	private GUI_JLabel prompt;
	private GUI_JButton backButton;
	
	public PresentCashScreen(final SystemControl systemControl) {
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
	
	/* for testing purposes */
	public static void main(String[] args) {
		SystemControl sc = new SystemControl();
		PresentCashScreen gui = new PresentCashScreen(sc);
		gui.openInNewJFrame();
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
