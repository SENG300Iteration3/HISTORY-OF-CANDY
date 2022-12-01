package swing.panes;


import  com.diy.hardware.DoItYourselfStation;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.enums.PaymentType;
import com.diy.software.listeners.PaymentControlListener;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

import swing.screens.*;
import swing.styling.Screen;

public class CustomerStationPane implements StationControlListener, PaymentControlListener {
	private StationControl sc;

	private JPanel rooPanel, currentPanel;
	private ArrayList<JPanel> panelStack;

	/******** Panels ********/
	private AddItemsScreen addItemsScreen;
	private PaymentScreen paymentScreen;
	private PinPadScreen pinPadScren;
	private PresentCardScreen presentCardScreen;
	private PresentCashScreen presentCashScreen;
	private BlockedPromptScreen blockedPromptScreen;
	private OkayPromptScreen okayPromptScreen;
	private MembershipScreen membershipSceen;
	private PresentMembershipCardScreen presentMembershipCardScreen;
	private PLUCodeScreen pluCodeScreen;

	public CustomerStationPane(StationControl sc) {
		this.sc = sc;

		
		/******** Register StationGUI in all listeners ********/
		this.sc.register(this);
		this.sc.getPaymentControl().addListener(this);
		
		/******** Initialize all Panels ********/
		this.panelStack = new ArrayList<>();
		this.addItemsScreen = new AddItemsScreen(sc);
		this.pinPadScren = new PinPadScreen(sc);
		this.paymentScreen = new PaymentScreen(sc);
		this.membershipSceen = new MembershipScreen(sc);
		this.pluCodeScreen = new PLUCodeScreen(sc);
		this.currentPanel = new JPanel();
		this.rooPanel = new JPanel();
		this.rooPanel.add(currentPanel);

		/******** Add initial panel to screen ********/
		addScreenToStack(addItemsScreen);
	}

	public JPanel getRootPanel() {
		return rooPanel;
	}

	public static void configureDiItYourselfStationAR() {
		DoItYourselfStation.configureBanknoteDenominations(new int [] {100, 50, 20, 10, 5, 1});
    DoItYourselfStation.configureCoinDenominations(new long [] {200, 100, 25, 10, 5, 1});
	}

	private void addScreenToStack(Screen newScreen) {
		addPanel(newScreen.getRootPanel());
		panelStack.add(newScreen.getRootPanel());
	}

	private void addPanelToStack(JPanel newPanel) {
		addPanel(newPanel);
		panelStack.add(newPanel);
	}

	private void addPanel(JPanel newPanel) {
		JPanel parent = (JPanel) currentPanel.getParent();
		parent.remove(currentPanel);
		currentPanel = newPanel;
		parent.add(currentPanel);
		parent.invalidate();
		parent.validate();
		parent.repaint();
	}

	private void removePanelFromStack() {
		JPanel parent = (JPanel) currentPanel.getParent();
		parent.remove(currentPanel);
		currentPanel = panelStack.get(panelStack.size() - 2);
		parent.add(currentPanel);
		parent.invalidate();
		parent.validate();
		parent.repaint();
		panelStack.remove(panelStack.size() - 1);
	}

	@Override
	public void systemControlLocked(StationControl systemControl, boolean isLocked) {
		if (isLocked) {
			blockedPromptScreen = new BlockedPromptScreen(systemControl,
					"Item has been scanned. Please add it to the bagging area");
			addScreenToStack(blockedPromptScreen);
		} else {
			triggerPanelBack(systemControl);
		}
	}

	@Override
	public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {
		okayPromptScreen = new OkayPromptScreen(systemControl, "Payment has been successfully made", true);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {
		okayPromptScreen = new OkayPromptScreen(systemControl, reason, false);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void paymentsHaveBeenEnabled(StationControl systemControl) {
		// TODO Auto-generated method stub

	}
	
	public void startMembershipCardInput(StationControl systemControl) {
		presentMembershipCardScreen = new PresentMembershipCardScreen(sc);
		addScreenToStack(presentMembershipCardScreen);
	}
	
	
	public void membershipCardInputFinished(StationControl systemControl) {
		triggerPanelBack(systemControl);
	}
	
	@Override
	public void membershipCardInputCanceled(StationControl systemControl, String reason) {
		okayPromptScreen = new OkayPromptScreen(systemControl, reason, false);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void initiatePinInput(StationControl systemControl, String kind) {
		addScreenToStack(pinPadScren);
	}

	@Override
	public void paymentMethodSelected(PaymentControl pc, PaymentType type) {
		switch (type) {
			case Cash:
				presentCashScreen = new PresentCashScreen(sc);
				addScreenToStack(presentCashScreen);
				break;
			case Credit:
				presentCardScreen = new PresentCardScreen(sc, "credit");
				addScreenToStack(presentCardScreen);
				break;
			case Debit:
				presentCardScreen = new PresentCardScreen(sc, "debit");
				addScreenToStack(presentCardScreen);
				break;
		}
	}

	@Override
	public void triggerPanelBack(StationControl systemControl) {
		removePanelFromStack();
	}

	@Override
	public void triggerInitialScreen(StationControl systemControl) {
		JPanel firstPanel = panelStack.get(0);
		panelStack.clear();
		addPanelToStack(firstPanel);
	}

	@Override
	public void triggerPaymentWorkflow(StationControl systemControl) {
		addScreenToStack(paymentScreen);
	}

	@Override
	public void triggerMembershipWorkflow(StationControl systemControl) {
		addScreenToStack(membershipSceen);
	}

	@Override
	public void triggerPLUCodeWorkflow(StationControl systemControl) {
		addScreenToStack(pluCodeScreen);
	}
}