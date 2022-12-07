package swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.KeyboardControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class AttendantLoginScreen extends Screen implements ActionListener, KeyboardControlListener {
	
	//It doesn't matter if these components are public
	//because they can be accessed by getting the components inside the AttendantLoginScreen
	public AttendantControl ac;
	public GUI_JButton loginButton;
	public JTextField loginInfo;
	public JLabel loginTitle;
	public JLabel loginFail;
	public GUI_JPanel centerPanel;
	
	private int width = 450;
	private int height = 50;
	private int overallMargin = 10;
	private KeyboardControl kc;
	
	private static String HeaderText = "Attendant Login Screen".toUpperCase();
	
	public AttendantLoginScreen(ArrayList<StationControl> stationControls) {
		super(null, HeaderText);
		
		super.rootPanel.setOpaque(true);
		super.rootPanel.setBackground(GUI_Color_Palette.WHITE);
		ac = stationControls.get(0).getAttendantControl();
		kc = ac.getKeyboardControl();
		kc.addListener(this);
		
		centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width,2 * height));
		centerPanel.setLayout(new GridLayout(2, 0));
		
		initalizeLoginLabel();
		
		initalizeTextField();
		
		addLayer(centerPanel, 0);
		
		initalizeLoginButton();
		
		loginFail = new GUI_JLabel();
		loginFail.setFont(GUI_Fonts.SUB_HEADER);
		loginFail.setHorizontalAlignment(JLabel.CENTER);
		loginFail.setForeground(Color.RED);
		
		addLayer(loginFail, 0);
	}
	
	public void loginFail() {
		loginFail.setText("Incorrect login Information".toUpperCase());
		
	}
	
	private void initalizeLoginButton() {
		
		loginButton = new GUI_JButton();
		loginButton.setText("LOGIN");
		loginButton.setBackground(GUI_Color_Palette.DARK_BROWN);
		loginButton.setForeground(GUI_Color_Palette.WHITE);

		loginButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		loginButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		loginButton.setLayout(new BorderLayout());
		
		loginButton.setActionCommand("LOGIN");
		loginButton.addActionListener(this);
		loginButton.setPreferredSize(new Dimension(width, height));
		addLayer(loginButton, 0);
		
	}
	private void initalizeLoginLabel() {
		
		loginTitle = new GUI_JLabel(HeaderText);
		loginTitle.setFont(GUI_Fonts.FRANKLIN_BOLD);
		loginTitle.setHorizontalAlignment(JLabel.CENTER);
		

		centerPanel.add(loginTitle, 0);
		
	}
	
	private void initalizeTextField() {
		
		loginInfo = new JTextField();
		loginInfo.setFont(GUI_Fonts.FRANKLIN_BOLD);
		loginInfo.setHorizontalAlignment(JLabel.CENTER);
		loginInfo.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));
		
		loginInfo.setEditable(false);
		loginInfo.getCaret().setVisible(true);
		loginInfo.setFocusable(true);
		//loginInfo.addKeyListener(kc);

		centerPanel.add(loginInfo, 1);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ac.login(loginInfo.getText());
	}

	@Override
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
		loginInfo.setText(text);
		loginInfo.requestFocus();
		loginInfo.setCaretPosition(pointerPosition);
		
	}

	@Override
	public void keyboardInputCompleted(KeyboardControl kc, String text) {
		ac.login(text);
	}
}
