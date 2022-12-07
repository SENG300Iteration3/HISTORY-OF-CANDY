package swing.screens;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class ReceiptScreen extends Screen {
	private JLabel promptLabel;
	private JScrollPane scrollPane;
	public JTextArea receiptTextArea;
	private JButton okayButton;

	public ReceiptScreen(final StationControl systemControl) {
		super(systemControl);

		this.promptLabel = new GUI_JLabel("Thank you for shopping with us!");
		promptLabel.setFont(GUI_Fonts.FRANKLIN_BOLD);
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		promptLabel.setPreferredSize(new Dimension(this.width - 200, 100));
		this.addLayer(promptLabel, 0);
		
		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(24, 0));
		scrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
		scrollPane.setPreferredSize(new Dimension(this.width - 400, 240));
		scrollPane.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		GUI_JPanel receiptPanel = new GUI_JPanel();
		scrollPane.getViewport().add(receiptPanel);
		
		receiptTextArea = new JTextArea();
		receiptTextArea.setPreferredSize(new Dimension(this.width - 200, 300));
		
		receiptPanel.add(receiptTextArea);
		
		addLayer(scrollPane, 0);

		this.okayButton = makeCentralButton("DONE", this.width - 200, 100);
		okayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				systemControl.goToInitialScreenOnUI();
				//TODO: Resetting the station
			}
		});
		this.addLayer(okayButton, 100);
	}
	
	public static void main(String[] args) {
		StationControl stationControl = new StationControl();
		AttendantControl attendantControl = new AttendantControl(stationControl);
		ReceiptScreen test = new ReceiptScreen(stationControl);
		test.receiptTextArea.setText("aaa\naaaa\naaa\n");
		test.openInNewJFrame();
	}
}
