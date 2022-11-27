package swing.station;


import  com.diy.hardware.DoItYourselfStation;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.SystemControl;
import com.diy.software.enums.PaymentType;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PaymentControlListener;
import com.diy.software.listeners.SystemControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

import swing.GUI_Constants;
import swing.GUI_JFrame;
import swing.Screen;
import swing.debug.DebugGUI;
import swing.screens.AddItemsScreen;
import swing.screens.BlockedPromptScreen;
import swing.screens.MembershipScreen;
import swing.screens.OkayPromptScreen;
import swing.screens.PaymentScreen;
import swing.screens.PinPadScreen;
import swing.screens.PresentCardScreen;
import swing.screens.PresentCashScreen;

public class StationGUI implements SystemControlListener, PaymentControlListener {
	private SystemControl sc;

	private GUI_JFrame frame = new GUI_JFrame("Station GUI", GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
	private JPanel currentPanel;
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

	public StationGUI(SystemControl sc) {
		this.sc = sc;

		/******** Register StationGUI in all listeners ********/
		this.sc.register(this);
		this.sc.getPaymentControl().addListener(this);

		/******** Initialize all Panels ********/
		panelStack = new ArrayList<>();
		addItemsScreen = new AddItemsScreen(sc);
		pinPadScren = new PinPadScreen(sc);
		paymentScreen = new PaymentScreen(sc);
		currentPanel = new JPanel();
		membershipSceen = new MembershipScreen(sc);

		/******** Add initial panel to screen ********/
		addScreenToStack(addItemsScreen);

		/******** Frame Setup ********/
		frame.setVisible(true);
		frame.setLocation(1280, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(currentPanel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		configureDiItYourselfStationAR();
		SystemControl sc = new SystemControl(new FakeDataInitializer());
		new StationGUI(sc);
		new DebugGUI(sc);
		new AttendantStationGUI(sc);
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
		frame.getContentPane().remove(currentPanel);
		currentPanel = newPanel;
		frame.getContentPane().add(currentPanel);
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

	private void removePanelFromStack() {
		frame.getContentPane().remove(currentPanel);
		currentPanel = panelStack.get(panelStack.size() - 2);
		frame.getContentPane().add(currentPanel);
		frame.invalidate();
		frame.validate();
		frame.repaint();
		panelStack.remove(panelStack.size() - 1);
	}

	@Override
	public void systemControlLocked(SystemControl systemControl, boolean isLocked) {
		if (isLocked) {
			blockedPromptScreen = new BlockedPromptScreen(systemControl,
					"Item has been scanned. Please add it to the bagging area");
			addScreenToStack(blockedPromptScreen);
		} else {
			triggerPanelBack(systemControl);
		}
	}

	@Override
	public void paymentHasBeenMade(SystemControl systemControl, CardData cardData) {
		okayPromptScreen = new OkayPromptScreen(systemControl, "Payment has been successfully made", true);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void paymentHasBeenCanceled(SystemControl systemControl, CardData cardData, String reason) {
		okayPromptScreen = new OkayPromptScreen(systemControl, reason, false);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void paymentsHaveBeenEnabled(SystemControl systemControl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initiatePinInput(SystemControl systemControl, String kind) {
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
	public void triggerPanelBack(SystemControl systemControl) {
		removePanelFromStack();
	}

	@Override
	public void triggerInitialScreen(SystemControl systemControl) {
		JPanel firstPanel = panelStack.get(0);
		panelStack.clear();
		addPanelToStack(firstPanel);
	}

	@Override
	public void triggerPaymentWorkflow(SystemControl systemControl) {
		addScreenToStack(paymentScreen);
	}

	@Override
	public void triggerMembershipWorkflow(SystemControl systemControl) {
		addScreenToStack(membershipSceen);
	}
}