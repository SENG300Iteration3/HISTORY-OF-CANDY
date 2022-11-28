package swing.styling;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class GUI_JPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public GUI_JPanel() {
		this.setBounds(0, 0, 250, 250);
		this.setBackground(GUI_Color_Palette.DARK_BROWN);
	}

	public void setPanelBorder(int topBorderSize, int BottomBorderSize, int LeftBorderSize, int RightBorderSize) {
		this.setBorder(BorderFactory.createEmptyBorder(topBorderSize, BottomBorderSize, LeftBorderSize, RightBorderSize));
	}

}
