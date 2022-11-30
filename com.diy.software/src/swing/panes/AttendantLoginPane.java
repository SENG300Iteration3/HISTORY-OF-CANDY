package swing.panes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class AttendantLoginPane extends Screen {
	
	private StationControl sc;
	private AttendantControl ac;
	GUI_JButton loginButton;
	private JTextField loginInfo;
	private JLabel loginTitle;
	private int width = 450;
	private int height = 50;
	private int overallMargin = 10;
	testListener tl;
	GUI_JPanel centerPanel;
	
	private static String HeaderText = "Attendant Login Screen".toUpperCase();
	
	public AttendantLoginPane(StationControl sc) {
		super(sc, HeaderText);
		this.sc = sc;
		ac = sc.getAttendantControl();
		tl = new testListener();
		
		centerPanel = new GUI_JPanel();
		centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
		centerPanel.setPreferredSize(new Dimension(width,2 * height));
		centerPanel.setLayout(new GridLayout(2, 0));
		
		initalizeLoginLabel();
		
		initalizeTextField();
		
		addLayer(centerPanel, 0);
		
		initalizeLoginButton();
		
		
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
		loginButton.addActionListener(tl);
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


		centerPanel.add(loginInfo, 1);
		
	}
	
	public class testListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			ac.login(loginInfo.getText(), "password");
		}
		
	}
}
