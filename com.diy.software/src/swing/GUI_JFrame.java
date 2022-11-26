package swing;

import java.awt.Color;

import javax.swing.JFrame;

/*This is an extension of JFrame in order to handle the setting up of a JFrame Window
 * */
public class GUI_JFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	// JFrame Setup
	public GUI_JFrame(String windowTitle, int windowSizeX, int windowSizeY) {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // close only the current window
		this.setTitle(windowTitle.toUpperCase());
		this.setSize(windowSizeX, windowSizeY);
		this.setLocationRelativeTo(null); // move JFrame to center of screen

		// JFrames background color in Hex
		changeBackgroundColor(GUI_Color_Palette.LIGHT_BLUE);
	}

	public void changeBackgroundColor(Color newBackgroundColor) {
		this.getContentPane().setBackground(newBackgroundColor);
	}
}
