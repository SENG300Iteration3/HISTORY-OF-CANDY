package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class TextSearchScreen extends Screen 
{
	
	private static String headerTitle = "Search Screen";
	
	private GUI_JPanel backgroundPanel;
	private JTextField searchbar;
	
	
	public TextSearchScreen(StationControl systemControl) 
	{
		super(systemControl, headerTitle);
		
		initalizeSearchbarBackground();
		initailizeSearchBar();
	}
	
	private void initalizeSearchbarBackground()
	{
		//Set up variables 
		int panelWidth = 100;
		int panelHeight  = 550;
		int borderSize = 20;
		int gridLayoutRow = 3;
		int gridLayoutCol = 0;
		
		backgroundPanel = new GUI_JPanel();
		backgroundPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		backgroundPanel.setPreferredSize(new Dimension(this.width - panelWidth, panelHeight));
		backgroundPanel.setBorder(BorderFactory.createMatteBorder(borderSize, borderSize, borderSize, borderSize, GUI_Color_Palette.DARK_BLUE));
		backgroundPanel.setLayout(new GridLayout(gridLayoutRow,gridLayoutCol));
		this.addLayer(backgroundPanel,0);
	}
	
	private void initailizeSearchBar()
	{
		int seacrhBarHeight = 100;
		int seacrhBarWidth = 500;
		
		searchbar = new JTextField("pin".toUpperCase());
		searchbar.setFont(GUI_Fonts.FRANKLIN_BOLD);
		searchbar.setHorizontalAlignment(JLabel.CENTER);
		
		searchbar.setPreferredSize(new Dimension(seacrhBarHeight,seacrhBarWidth));
		searchbar.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));
		
		backgroundPanel.add(searchbar);
	}
	
	
	//Creates  Item detail/button
	private GUI_JButton makeItemComponent(String itemName, double cost) 
	{
		//Setting up the button 
		int buttonSize  = 200;
		GUI_JButton itemButton = new GUI_JButton();
		itemButton.setPreferredSize(new Dimension(this.width - buttonSize, 50));
		itemButton.setLayout(new BorderLayout());
		//Removing the border button
		Border emptyBorder = BorderFactory.createEmptyBorder();
		itemButton.setBorder(emptyBorder);
		
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
	
	private String formatDollars(double dollarAmount) 
	{
		return "$" + String.format("%.2f", dollarAmount);
	}
	
	/*Just for testing */
	public static void main(String[] args) 
	{
		StationControl stationControl = new StationControl();
		TextSearchScreen testGui = new TextSearchScreen(stationControl);
		testGui.openInNewJFrame();
	}

}
