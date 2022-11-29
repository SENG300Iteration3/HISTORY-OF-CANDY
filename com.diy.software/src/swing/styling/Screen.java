package swing.styling;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.diy.software.controllers.StationControl;

public abstract class Screen {
	protected StationControl systemControl;
	private static int screenCount = 0;

	private String headerTitle;
	private String serial;
	protected int width;
	protected int height;
	private int layerCount = 0;

	protected GUI_JPanel rootPanel;
	protected GUI_JPanel centralPanel;

	public Screen(StationControl systemControl, String headerTitle, int width, int height) {
		this.systemControl = systemControl;

		this.headerTitle = headerTitle.toUpperCase();
		this.serial = Integer.toString(screenCount++);
		this.width = width;
		this.height = height;

		this.rootPanel = new GUI_JPanel();
		this.rootPanel.setOpaque(false);

		BorderLayout layout = new BorderLayout();
		this.rootPanel.setLayout(layout);

		if (headerTitle != null && !headerTitle.isEmpty()) {
			initTitleBar();
		}

		this.centralPanel = new GUI_JPanel();
		this.centralPanel.setOpaque(false);
		this.centralPanel.setPreferredSize(new Dimension(this.width - 200, this.height));
		this.rootPanel.add(this.centralPanel, BorderLayout.CENTER);

		this.centralPanel.setLayout(new GridBagLayout());
	}

	public Screen(StationControl systemControl, String headerTitle) {
		this(systemControl, headerTitle, GUI_Constants.SCREEN_WIDTH, GUI_Constants.SCREEN_HEIGHT);
	}

	public Screen(StationControl systemControl) {
		this(systemControl, "");
	}

	private void initTitleBar() {
		GUI_JLabel titleLabel = new GUI_JLabel();
		titleLabel.setOpaque(true);
		titleLabel.setBackground(GUI_Color_Palette.DARK_BLUE);
		titleLabel.setPreferredSize(new Dimension(this.width, 70));
		titleLabel.setText(this.headerTitle);
		titleLabel.setFont(GUI_Fonts.TITLE);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		this.rootPanel.add(titleLabel, BorderLayout.NORTH);
	}

	public GUI_JButton makeCentralButton(String text, int width, int height) {
		GUI_JButton btn = new GUI_JButton(text);
		btn.setPreferredSize(new Dimension(width, height));
		btn.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, GUI_Color_Palette.DARK_BLUE));

		return btn;
	}

	/* Add a component below the lowest component on the screen */
	public void addLayer(JComponent component, int gap) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = this.layerCount++;
		gc.insets = new Insets(gap, 0, 0, 0);

		this.centralPanel.add(component, gc);
	}

	/* Useful for testing purposes */
	public void openInNewJFrame() {
		GUI_JFrame frame = new GUI_JFrame(
				this.headerTitle,
				GUI_Constants.SCREEN_WIDTH,
				GUI_Constants.SCREEN_HEIGHT);
		frame.add(this.rootPanel);
		frame.setVisible(true);
	}

	public GUI_JPanel getRootPanel() {
		return this.rootPanel;
	}

	public String getSerial() {
		return this.serial;
	}
}
