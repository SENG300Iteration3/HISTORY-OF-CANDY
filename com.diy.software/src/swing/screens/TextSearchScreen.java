package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.PhysicalKeyboardControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.TextLookupControl;
import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.listeners.TextLookupControlListener;
import com.diy.software.util.CodedProduct;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class TextSearchScreen extends Screen implements KeyboardControlListener, TextLookupControlListener
{

	private static String headerTitle = "Search Screen";

	private GUI_JPanel backgroundPanel;
	private JTextField searchbar;
	private GUI_JPanel searchResultPanel;
	private GUI_JButton searchButton;
	private GUI_JButton backButton;
	private ArrayList<GUI_JButton> itemButtonList;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	Border emptyBorder = BorderFactory.createEmptyBorder();

	private KeyboardControl kc;
	private TextLookupControl tlc;
	private AttendantControl ac;


	public TextSearchScreen(StationControl sc, AttendantControl ac)
	{
		super(sc, headerTitle);

		this.ac = ac;
		
		itemButtonList = new ArrayList<GUI_JButton>();
		
		// Initializing all GUI Components
		initalizeSearchAreaBackground();
		initailizeSearchBar();
		initailizeSearchResultHolder();
		initailizeSearchButton();
		initailizeBackButton();

		kc = ac.getKeyboardControl();

		kc.addListener(this);

		tlc = ac.getTextLookupControl();
		tlc.addListener(this);
	}

	private void initalizeSearchAreaBackground()
	{
		//Set up variables 
		int panelWidth = 100;
		int panelHeight  = 560;

		//Setting up the background panel
		backgroundPanel = new GUI_JPanel();
		backgroundPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		backgroundPanel.setPreferredSize(new Dimension(this.width - panelWidth, panelHeight));
		backgroundPanel.setLayout(new GridBagLayout());

		//Adding the component to the Screen
		this.addLayer(backgroundPanel,-60);
	}

	private void initailizeSearchBar()
	{
		//Search bar set up variable
		int seacrhBarHeight = 50;
		int seacrhBarWidth = 1100;
		int insetSpace = 10;

		//Setting up Search bar
		searchbar = new JTextField("Search Bar".toUpperCase());
		searchbar.setFont(GUI_Fonts.FRANKLIN_BOLD);
		searchbar.setHorizontalAlignment(JLabel.CENTER);
		searchbar.setBorder(emptyBorder);
		searchbar.setPreferredSize(new Dimension(seacrhBarWidth,seacrhBarHeight));
		searchbar.setEditable(false);

		//Setting up grid Bag Constraints on the search bar
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(-70, 0, insetSpace, 0);

		//Adding the component to the main Background
		backgroundPanel.add(searchbar,gridBagConstraints);
	}

	private void initailizeSearchResultHolder()
	{
		//Search bar set up variable
		int searchResultHolderHeight = 300;
		int searchResultHolderWidth = 1100;
		int searchResultPanelHeight = 850;
		int searchResultPanelWidth = 0;
		int insetSpace = 0;

		//Setting up Search results holder
		searchResultPanel = new GUI_JPanel();
		searchResultPanel.setBorder(emptyBorder);
		searchResultPanel.setBackground(GUI_Color_Palette.DARK_BROWN);
		searchResultPanel.setPreferredSize(new Dimension(searchResultPanelWidth,searchResultPanelHeight));
		searchResultPanel.setLayout(new GridLayout(20, 0));

		//Setting up grid Bag Constraints on the result holder
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(insetSpace, 0,0 , 0);

		//Setting up the scrollPane
		JScrollPane resultScrollPane = new JScrollPane
				(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		resultScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(20, 0));
		resultScrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
		resultScrollPane.setBorder(emptyBorder);

		resultScrollPane.getVerticalScrollBar().setBorder(emptyBorder);
		resultScrollPane.getViewport().setBackground(GUI_Color_Palette.DARK_BROWN);
		resultScrollPane.getVerticalScrollBar().setBackground(GUI_Color_Palette.DARK_BROWN);
		resultScrollPane.setPreferredSize(new Dimension(searchResultHolderWidth, searchResultHolderHeight));

		//Adding the results holder to the scroll pane
		resultScrollPane.getViewport().add(searchResultPanel);

		//Adding the component to the main Background
		backgroundPanel.add(resultScrollPane,gridBagConstraints);
	}


	private void initailizeSearchButton()
	{
		//Setup variables
		int searchButtonHeight = 45;
		int searchButtonWidth = 350;

		//Setting up  the search button
		searchButton = new GUI_JButton("Search".toUpperCase());
		searchButton.setFont(GUI_Fonts.FRANKLIN_BOLD);
		searchButton.setPreferredSize(new Dimension(searchButtonWidth, searchButtonHeight));
		searchButton.setLayout(new BorderLayout());
		searchButton.setBorder(emptyBorder);

		//Setting up grid Bag Constraints on the search button
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(10,0,0,0);

		//Adding the component to the main Background
		backgroundPanel.add(searchButton, gridBagConstraints);

		searchButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				clearSearchResults();
				tlc.findProduct(searchbar.getText());
				searchResultPanel.repaint();
				searchResultPanel.revalidate();
				kc.clearText();
			}
		});
	}

	private void initailizeBackButton()
	{
		//Setup variables
		int searchButtonHeight = 35;
		int searchButtonWidth = 130;

		//Setting up  the back button
		backButton = new GUI_JButton("Back".toUpperCase());
		backButton.setFont(GUI_Fonts.FRANKLIN_BOLD);
		backButton.setPreferredSize(new Dimension(searchButtonWidth, searchButtonHeight));
		backButton.setLayout(new BorderLayout());
		backButton.setBorder(emptyBorder);
		backButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				kc.clearText();
				ac.exitTextSearch();
			}
		});

		//Setting up grid Bag Constraints on the back button
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(10 ,0,0,0);

		//Adding the component to the main Background
		backgroundPanel.add(backButton, gridBagConstraints);

	}


	//Creates  Item detail/button
	private GUI_JButton makeItemComponent(String itemName, double cost, final int index)
	{
		//Setting up the button 
		int buttonSize  = 200;
		GUI_JButton itemButton = new GUI_JButton();
		itemButton.setPreferredSize(new Dimension(this.width - buttonSize, 100));
		itemButton.setLayout(new BorderLayout());
		itemButton.setBorder(emptyBorder);
		itemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				tlc.addProduct(index);
			}
		});
		itemButtonList.add(itemButton);
		
		//Setting up and adding the total label
		GUI_JLabel totalLabel = new GUI_JLabel(itemName.toUpperCase());
		totalLabel.setFont(GUI_Fonts.SUB_HEADER);
		totalLabel.setBorder(new EmptyBorder(0, 30, 0, 0));
		itemButton.add(totalLabel, BorderLayout.WEST);

		//Setting up and adding the cost label
		GUI_JLabel costLabel = new GUI_JLabel(formatDollars(cost));
		costLabel.setFont(GUI_Fonts.SUB_HEADER);
		costLabel.setBorder(new EmptyBorder(0, 0, 0, 100));
		itemButton.add(costLabel, BorderLayout.EAST);

		return itemButton;
	}

	//Clears the search results panel
	public void clearSearchResults()
	{
		searchResultPanel.removeAll();
	}

	//Turns a double in a formated dollar string
	private String formatDollars(double dollarAmount)
	{
		return "$" + String.format("%.2f", dollarAmount);
	}

	@Override
	public void searchQueryWasEntered(TextLookupControl tlc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resultWasChosen(TextLookupControl tlc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemHasBeenBagged(TextLookupControl tlc) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void checkoutHasBeenUpdated(TextLookupControl tlc) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
		// Very important check to only update the text field when the attendant is viewing this screen
		if (this.rootPanel.getParent() != null) {
			searchbar.setText(text);
			searchbar.requestFocus();
			searchbar.setCaretPosition(pointerPosition);
		}
	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String text) {
		clearSearchResults();
		tlc.findProduct(searchbar.getText());
		searchResultPanel.repaint();
		searchResultPanel.revalidate();
		kc.clearText();
	}

	@Override
	public void searchHasBeenCleared(TextLookupControl tlc) {
		clearSearchResults();
	}

	@Override
	public void resultsWereFound(TextLookupControl tlc) {
		ArrayList<CodedProduct> results = tlc.getResults();
		for (int i = 0; i < results.size(); i++) {
			CodedProduct product = results.get(i);
			searchResultPanel.add(makeItemComponent(product.toString(), product.getPrice(), i));

		}

	}

	@Override
	public void noResultsWereFound(TextLookupControl tlc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemHasBeenAddedToCheckout(TextLookupControl tlc) {
	}

	public JTextField getSearchbar() {
		return searchbar;
	}

	public GUI_JButton getSearchButton() {
		return searchButton;
	}

	public GUI_JButton getBackButton() {
		return backButton;
	}

	public ArrayList<GUI_JButton> getItemButtonList() {
		return itemButtonList;
	}
	
	
	
	
}
