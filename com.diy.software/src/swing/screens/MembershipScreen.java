package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.diy.software.controllers.MembershipControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.MembershipControlListener;
import com.diy.software.listeners.StationControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class MembershipScreen extends Screen implements MembershipControlListener{
	private MembershipControl mc;

	private GridBagConstraints gridConstraint = new GridBagConstraints();

	private JButton[] numberPadButtons = new JButton[10];
	private JButton cancelButton = createNumberPadButton("X");
	private JButton correctButton = createNumberPadButton("O");
	private JButton submitButton = createNumberPadButton(">");
	
	private GUI_JPanel scanSwipePanel;
	private GUI_JButton scanSwipeButton;

	private JTextField numberEntry;
	private JLabel memberMssg = new JLabel("");
	GUI_JPanel numberInputPanel;
	
	private PresentMembershipCardScreen scanSwipeScreen;

	private static String HeaderText = "Membership";
	
	public MembershipScreen(StationControl sc) {
		super(sc, HeaderText);
		mc = sc.getMembershipControl();
		mc.addListener(this);
		
		scanSwipePanel = new GUI_JPanel();
		scanSwipePanel.setLayout(new GridBagLayout());
		scanSwipePanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		
		scanSwipeButton = new GUI_JButton("Scan/Swipe Card");
		scanSwipeButton.setBackground(GUI_Color_Palette.DARK_BLUE);
		scanSwipeButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));
		scanSwipeButton.setForeground(GUI_Color_Palette.WHITE);
		
		gridConstraint.gridy = 1;
		gridConstraint.gridx = 1;
		scanSwipeButton.setActionCommand("scan swipe membership");
		scanSwipeButton.addActionListener(mc);
		scanSwipePanel.add(scanSwipeButton, gridConstraint);
		
		addLayer(scanSwipePanel, 0);

		numberInputPanel = new GUI_JPanel();
		numberInputPanel.setLayout(new GridBagLayout());
		numberInputPanel.setBackground(GUI_Color_Palette.DARK_BLUE);

		initalizeMessageLabel();
		initalizeTextField();

		gridConstraint.gridy = 1;
		gridConstraint.ipadx = 100;
		gridConstraint.ipady = 10;

		for (int i = 0; i < 10; i++) {
			GUI_JButton currButton = createNumberPadButton("" + (i + 1) % 10);
			currButton.setActionCommand("MEMBER_INPUT_BUTTON: " + (i + 1) % 10);
			currButton.addActionListener(mc);
			numberPadButtons[i] = currButton;
			gridConstraint.gridx = i % 3;
			gridConstraint.gridy = (i / 3) + 2;
			if (i == 9) {
				gridConstraint.gridx = 1;
			}
			numberInputPanel.add(currButton, gridConstraint);
		}

		gridConstraint.gridy = 6;

		gridConstraint.gridx = 0;
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(mc);
		numberInputPanel.add(cancelButton, gridConstraint);

		gridConstraint.gridx = 1;
		correctButton.setActionCommand("correct");
		correctButton.addActionListener(mc);
		numberInputPanel.add(correctButton, gridConstraint);

		gridConstraint.gridx = 2;
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(mc);
		numberInputPanel.add(submitButton, gridConstraint);

		addLayer(numberInputPanel, 0);
	}
	
	private void initalizeMessageLabel() {
		memberMssg = new GUI_JLabel("Enter your member ID".toUpperCase());
		memberMssg.setFont(GUI_Fonts.FRANKLIN_BOLD);
		memberMssg.setHorizontalAlignment(JLabel.CENTER);

		int width = 405;
		int height = 50;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(memberMssg);
		addLayer(centerPanel, 10);

	}

	private void initalizeTextField() {
		numberEntry = new JTextField("MemberID".toUpperCase());
		numberEntry.setFont(GUI_Fonts.FRANKLIN_BOLD);
		numberEntry.setHorizontalAlignment(JLabel.CENTER);
		numberEntry.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

		int width = 405;
		int height = 70;

		GUI_JPanel centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width, height));
		centerPanel.setLayout(new GridLayout(1, 0));

		centerPanel.add(numberEntry);
		addLayer(centerPanel, 0);

	}

	private GUI_JButton createNumberPadButton(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton numberPadButton = new GUI_JButton();
		numberPadButton.setText(text);
		numberPadButton.setBackground(GUI_Color_Palette.DARK_BROWN);
		numberPadButton.setForeground(GUI_Color_Palette.WHITE);

		numberPadButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		numberPadButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		numberPadButton.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return numberPadButton;
	}
	
	@Override
	public void welcomeMember(MembershipControl mc, String memberName) {
		memberMssg.setText(memberName);
	}


	@Override
	public void memberFieldHasBeenUpdated(MembershipControl mc, String memberNumber) {
		numberEntry.setText(memberNumber);
	}
	
	@Override
	public void scanSwipeSelected(MembershipControl mc) {
		super.systemControl.startMembershipCardInput();
		
	}
}
