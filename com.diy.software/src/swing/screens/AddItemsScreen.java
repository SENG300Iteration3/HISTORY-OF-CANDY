package swing.screens;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.diy.software.util.Tuple;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.listeners.StationControlListener;

public class AddItemsScreen extends Screen implements ItemsControlListener, BagsControlListener {
	private StationControl systemControl;
	private ItemsControl itemsControl;
	private BagsControl bc;

	protected GUI_JLabel subtotalLabel;
	protected GUI_JPanel scannedPanel;
	protected GUI_JButton payBtn;
	protected GUI_JButton memberBtn;

	protected GUI_JButton requestNoBaggingBtn;
	protected GUI_JButton addOwnBagsBtn;
	protected GUI_JButton removeItemBtn;

	protected GUI_JButton purchaseOwnBagsBtn;
	protected GUI_JButton addItemByPLUBtn;
	protected GUI_JButton searchCatalogueBtn;
	
	private AddOwnBagsPromptScreen ownBagsPromptScreen;


	public AddItemsScreen(StationControl systemControl) {
		super(systemControl, "Self Checkout");
		
		this.systemControl = systemControl;
		
		this.itemsControl = systemControl.getItemsControl();
		this.itemsControl.addListener(this);
		
		bc = systemControl.getBagsControl();
		bc.addListener(this);


		//Create a main panel that will be split into 2
		//The left side will have the already made scanning screen
		//The right side will have the 5 new buttons
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.setPreferredSize(new Dimension(this.width-100, 400));
		//Right side panel (BoxLayout)

		JPanel leftSidePanel = new JPanel();

		BoxLayout boxlayout = new BoxLayout(leftSidePanel, BoxLayout.Y_AXIS);
		leftSidePanel.setLayout(boxlayout);


		GUI_JLabel itemCheckoutHeader = new GUI_JLabel("ITEM CHECKOUT");
		itemCheckoutHeader.setOpaque(true);
		itemCheckoutHeader.setBackground(GUI_Color_Palette.DARK_BROWN);
		itemCheckoutHeader.setFont(GUI_Fonts.TITLE);
		itemCheckoutHeader.setHorizontalAlignment(JLabel.CENTER);
		//itemCheckoutHeader.setPreferredSize(new Dimension(this.width - 400, 100));
		itemCheckoutHeader.setMaximumSize(new Dimension(this.width - 400, itemCheckoutHeader.getMinimumSize().height));
		itemCheckoutHeader.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));

		itemCheckoutHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		itemCheckoutHeader.setAlignmentY(Component.CENTER_ALIGNMENT);

		//this.addLayer(itemCheckoutHeader, 0);

		JScrollPane itemScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		itemScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(24, 0));
		itemScrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
		itemScrollPane.setPreferredSize(new Dimension(this.width - 400, 240));
		itemScrollPane.setBorder(BorderFactory.createMatteBorder(0, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		itemScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		//this.addLayer(itemScrollPane, 0);

		this.scannedPanel = new GUI_JPanel();
		scannedPanel.setLayout(new GridLayout(20, 1));
		itemScrollPane.getViewport().add(scannedPanel);


		GUI_JPanel totalPanelBg = makeItemLabel("subtotal", 0);
		((GUI_JLabel) totalPanelBg.getComponent(0)).setFont(GUI_Fonts.TITLE);
		this.subtotalLabel = (GUI_JLabel) totalPanelBg.getComponent(1);
		subtotalLabel.setBorder(new EmptyBorder(0, 0, 0, 34)); // adjust position of text
		subtotalLabel.setFont(GUI_Fonts.TITLE);
		totalPanelBg.setPreferredSize(new Dimension(this.width - 400, 80));
		totalPanelBg.setBorder(BorderFactory.createMatteBorder(0, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		totalPanelBg.setAlignmentX(Component.CENTER_ALIGNMENT);
		//this.addLayer(totalPanelBg, 0);

		leftSidePanel.add(itemCheckoutHeader);
		leftSidePanel.add(itemScrollPane);
		leftSidePanel.add(totalPanelBg);

		mainPanel.add(leftSidePanel, BorderLayout.CENTER);

		JPanel rightSidePanel = new JPanel();

		//Set a name so that in makeButton() we can call the proper border dimensions
		rightSidePanel.setName("Right Panel");

		rightSidePanel.setPreferredSize(new Dimension(400, 400));

		BoxLayout boxlayout2 = new BoxLayout(rightSidePanel, BoxLayout.Y_AXIS);

		rightSidePanel.setLayout(boxlayout2);

		mainPanel.add(rightSidePanel, BorderLayout.EAST);

		//Adding buttons to the right side of the frame

		rightSidePanel.setBorder(new EmptyBorder(0, 40, 0, 0));


		JPanel rightSidebuttonPanel = new JPanel(new GridLayout(4, 1));
		rightSidebuttonPanel.setPreferredSize(new Dimension(370, 400));

		rightSidebuttonPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		rightSidePanel.add(rightSidebuttonPanel);

		JPanel firstButtonPanel = new JPanel(new GridLayout());
		this.addOwnBagsBtn = makeButton("Add Own Bags", firstButtonPanel);
		addOwnBagsBtn.setActionCommand("add bags");
		addOwnBagsBtn.addActionListener(bc);
		rightSidebuttonPanel.add(firstButtonPanel);

		JPanel secondButtonPanel = new JPanel(new GridLayout());
		this.purchaseOwnBagsBtn = makeButton("Purchase Bag", secondButtonPanel);
		purchaseOwnBagsBtn.setActionCommand("purchase bags");
		purchaseOwnBagsBtn.addActionListener(bc);
		rightSidebuttonPanel.add(secondButtonPanel);

		JPanel thirdButtonPanel = new JPanel(new GridLayout());
		this.addItemByPLUBtn = makeButton("Add Item by PLU", thirdButtonPanel);
		rightSidebuttonPanel.add(thirdButtonPanel);

		JPanel fourthButtonPanel = new JPanel(new GridLayout());
		this.removeItemBtn = makeButton("Remove Item", fourthButtonPanel);
		rightSidebuttonPanel.add(fourthButtonPanel);
		this.removeItemBtn.setActionCommand("remove item");
		this.removeItemBtn.addActionListener(itemsControl);

		this.addLayer(mainPanel, 0);

		//Bottom 3 buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(this.width - 125, 80));
		this.addLayer(buttonPanel, 30);

		this.payBtn = makeButton("pay", buttonPanel);
		this.payBtn.setActionCommand("pay");
		this.payBtn.addActionListener(itemsControl);

		this.memberBtn = makeButton("enter member id", buttonPanel);
		this.memberBtn.setActionCommand("member");
		this.memberBtn.addActionListener(itemsControl);

		this.searchCatalogueBtn = makeButton("Browse Items", buttonPanel);
		


	}

	public void invalidateAllScannedItems() {
		this.scannedPanel.removeAll();
	}

	public void addScannedItem(String itemName, double cost) {
		this.scannedPanel.add(makeItemLabel(itemName, cost));
		this.scannedPanel.repaint();
		this.scannedPanel.revalidate();
	}

	public void updateSubtotal(double subtotal) {
		this.subtotalLabel.setText(formatDollars(subtotal));
	}

	private GUI_JPanel makeItemLabel(String itemName, double cost) {
		GUI_JPanel itemPanel = new GUI_JPanel();
		itemPanel.setPreferredSize(new Dimension(this.width - 200, 50));
		itemPanel.setLayout(new BorderLayout());

		GUI_JLabel totalLabel = new GUI_JLabel(itemName.toUpperCase());
		totalLabel.setFont(GUI_Fonts.SUB_HEADER);
		totalLabel.setBorder(new EmptyBorder(0, 30, 0, 0));
		itemPanel.add(totalLabel, BorderLayout.WEST);

		GUI_JLabel costLabel = new GUI_JLabel(formatDollars(cost));
		costLabel.setFont(GUI_Fonts.SUB_HEADER);
		costLabel.setBorder(new EmptyBorder(0, 0, 0, 100));
		itemPanel.add(costLabel, BorderLayout.EAST);

		return itemPanel;
	}

	private String formatDollars(double dollarAmount) {
		return "$" + String.format("%.2f", dollarAmount);
	}

	private GUI_JButton makeButton(String text, JPanel parent) {
		final int left_padding = parent.getComponentCount() == 0 ? 20 : 0;

		GUI_JButton btn = new GUI_JButton(text.toUpperCase());
		btn.setFont(GUI_Fonts.TITLE);

		//Fix border formatting for the left side of the buttons
		if (!Objects.equals(parent.getName(), "Right Panel")) {

			btn.setBorder(BorderFactory.createMatteBorder(10, left_padding, 10, 20, GUI_Color_Palette.DARK_BLUE));
		} else {
			btn.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, GUI_Color_Palette.DARK_BLUE));
		}
		btn.setBackground(GUI_Color_Palette.DARK_BROWN);
		btn.setOpaque(true);
		parent.add(btn);

		return btn;
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
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsHaveBeenUpdated(ItemsControl itemsControl) {
		Map<String, Double> checkoutList = itemsControl.getCheckoutList();
		int i = 0;
		this.invalidateAllScannedItems();
		for (String productName: checkoutList.keySet()) {
			this.addScannedItem(i+1 + ". " + productName, checkoutList.get(productName));
			i++;
		}
	}

	@Override
	public void productSubtotalUpdated(ItemsControl itemsControl) {
		subtotalLabel.setText("Subtotal: $" + itemsControl.getCheckoutTotal());
	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {

	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub
		
	}
}

