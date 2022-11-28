package swing.styling;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class GUI_JButton extends JButton {

	public GUI_JButton() {
		this.setBackground(GUI_Color_Palette.DARK_BROWN);
		this.setFocusable(false);// Removes the border around button text
		this.setBounds(0, 0, 40, 500);

		this.setFont(GUI_Fonts.TITLE);
		this.setForeground(GUI_Color_Palette.WHITE);
	}

	public GUI_JButton(String text) {
		this();
		this.setText(text);
	}

}
