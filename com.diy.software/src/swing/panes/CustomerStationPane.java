package swing.panes;

import com.diy.hardware.DoItYourselfStation;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.enums.PaymentType;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.listeners.PaymentControlListener;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.opeechee.Card.CardData;

import swing.screens.*;
import swing.panels.CatalogPanel;
import swing.screens.AddItemsScreen;
import swing.screens.BlockedPromptScreen;
import swing.screens.MembershipScreen;
import swing.screens.NotEnoughBagsScreen;
import swing.screens.OkayPromptScreen;
import swing.screens.PaymentScreen;
import swing.screens.PinPadScreen;
import swing.screens.PresentCardScreen;
import swing.screens.PresentMembershipCardScreen;
import swing.screens.PurchaseBagScreen;
import swing.screens.PresentGiftCardOrCashScreen;
import swing.styling.Screen;

public class CustomerStationPane implements StationControlListener, PaymentControlListener, ItemsControlListener {
	private StationControl sc;

	private JPanel rooPanel, currentPanel;
	private ArrayList<JPanel> panelStack;

	/******** Panels ********/
	private AddItemsScreen addItemsScreen;
	private PaymentScreen paymentScreen;
	private PinPadScreen pinPadScren;
	private PresentCardScreen presentCardScreen;
	private PresentGiftCardOrCashScreen presentCashScreen;
	private BlockedPromptScreen blockedPromptScreen;
	private OkayPromptScreen okayPromptScreen;
	private MembershipScreen membershipSceen;
	private PurchaseBagScreen purchaseBagScreen;
	private PresentMembershipCardScreen presentMembershipCardScreen;
	private PLUCodeScreen pluCodeScreen;
	private CatalogPanel catalogPanel;
	

	public CustomerStationPane(StationControl sc) {
		this.sc = sc;

		/******** Register StationGUI in all listeners ********/
		this.sc.register(this);
		this.sc.getPaymentControl().addListener(this);
		this.sc.getItemsControl().addListener(this);

		/******** Initialize all Panels ********/
		this.panelStack = new ArrayList<>();
		this.addItemsScreen = new AddItemsScreen(sc);
		this.pinPadScren = new PinPadScreen(sc);
		this.paymentScreen = new PaymentScreen(sc);
		this.membershipSceen = new MembershipScreen(sc);
		this.pluCodeScreen = new PLUCodeScreen(sc);
		this.purchaseBagScreen = new PurchaseBagScreen(sc);

		this.catalogPanel = new CatalogPanel(sc);
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
		DoItYourselfStation.configureBanknoteDenominations(new int[] { 100, 50, 20, 10, 5, 1 });
		DoItYourselfStation.configureCoinDenominations(new BigDecimal[] { new BigDecimal(2.00), new BigDecimal(1.00), 
								new BigDecimal(.25), new BigDecimal(.10), new BigDecimal(.05), new BigDecimal(.01) });
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
	public void systemControlLocked(StationControl systemControl, boolean isLocked, String reason) {
		if (isLocked) {
			if (reason == "use own bags") {
				AddOwnBagsPromptScreen screen = new AddOwnBagsPromptScreen(systemControl, 
						"Please Place Your Bags In the Bagging Area");
				addScreenToStack(screen);
			}else if (reason == "prevent") {
				OkayPromptScreen screen = new OkayPromptScreen(systemControl, "Station Closed For Maintainence", true, false);
				addScreenToStack(screen);
			}else if (reason == "Printer is out of ink or paper please wait for attendant") {
				OkayPromptScreen screen = new OkayPromptScreen(systemControl, reason, true, false);
				addScreenToStack(screen);
			}else if (reason == "shutdown") {
				OkayPromptScreen screen = new OkayPromptScreen(systemControl, "Station Shut-down", true, false);
				addScreenToStack(screen);
			} else {
				blockedPromptScreen = new BlockedPromptScreen(systemControl, reason);
				addScreenToStack(blockedPromptScreen);
			}
		} else {
			triggerPanelBack(systemControl);
		}
	}

	@Override
	public void paymentHasBeenMade(StationControl systemControl, CardData cardData) {
		okayPromptScreen = new OkayPromptScreen(systemControl, "Payment has been successfully made", true, true);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void paymentHasBeenCanceled(StationControl systemControl, CardData cardData, String reason) {
		okayPromptScreen = new OkayPromptScreen(systemControl, reason, false, true);
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
		okayPromptScreen = new OkayPromptScreen(systemControl, reason, false, true);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void initiatePinInput(StationControl systemControl, String kind) {
		addScreenToStack(pinPadScren);
	}

	@Override
	public void paymentMethodSelected(PaymentControl pc, PaymentType type) {
		switch (type) {
			case GiftCard:
				presentCashScreen = new PresentGiftCardOrCashScreen(sc, true);
				addScreenToStack(presentCashScreen);
				break;
			case Cash:
				presentCashScreen = new PresentGiftCardOrCashScreen(sc, false);
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
	public void triggerPurchaseBagsWorkflow(StationControl systemControl) {
		addScreenToStack(purchaseBagScreen);
	}

	@Override
	public void noBagsInStock(StationControl systemControl) {
		okayPromptScreen = new OkayPromptScreen(systemControl, "No Bags In Stock. Please Ask Attendant For Assistance.", false, true);
		addPanel(okayPromptScreen.getRootPanel());
		
	}

	@Override
	public void notEnoughBagsInStock(StationControl systemControl, int numBag) {
		NotEnoughBagsScreen screen = new NotEnoughBagsScreen(systemControl, numBag);
		addPanel(screen.getRootPanel());
		
	}

	
	public void triggerBrowsingCatalog(StationControl systemControl) {
		addPanelToStack(catalogPanel);
		
	}

	@Override
	public void triggerPLUCodeWorkflow(StationControl systemControl) {
		addScreenToStack(pluCodeScreen);
		
	}

	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemWasSelected(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingItemToBePlacedInScanningArea(StationControl sc) {
		sc.goBackOnUI();
		okayPromptScreen = new OkayPromptScreen(sc, "Please place item on the scale for weighing.", false, false);
		addPanel(okayPromptScreen.getRootPanel());
	}

	@Override
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {
		
		
	}

	@Override
	public void itemsHaveBeenUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void productSubtotalUpdated(ItemsControl ic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingAttendantToApproveItemRemoval(ItemsControl ic) {
		okayPromptScreen = new OkayPromptScreen(sc, "Please Ask Attendant For Assistance In Removing An Item", false, false);
		addScreenToStack(okayPromptScreen);
	}

	@Override
	public void itemRemoved(ItemsControl itemsControl) {
		triggerPanelBack(sc);
		
	}

	@Override
	public void triggerReceiptScreen(StationControl systemControl) {
		ReceiptScreen screen = new ReceiptScreen(systemControl);
		addScreenToStack(screen);
		
	}
}