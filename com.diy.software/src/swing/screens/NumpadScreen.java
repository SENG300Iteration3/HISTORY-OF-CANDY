package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.diy.software.controllers.NumpadControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.enums.NumpadUseArea;
import com.diy.software.listeners.NumpadControlListener;
import swing.styling.*;

public class NumpadScreen extends Screen implements NumpadControlListener {
    private NumpadControl nc;

    private GridBagConstraints gridConstraint = new GridBagConstraints();

    private JButton[] numpadButtons = new JButton[10];
    private JButton cancelButton = createNumpadButton("X");
    private JButton correctButton = createNumpadButton("O");
    private JButton submitButton = createNumpadButton(">");

    GUI_JPanel numpadPanel;

    JLabel message;
    String messageLabelText;

    JTextField numbers;

    public NumpadScreen(StationControl sc, String headerText, String messageLabelText, String numbersLabelText, NumpadUseArea nua) {
        super(sc, headerText);
        this.nc = sc.getNumpadControl();
        this.nc.addListener(this);
        this.nc.setUseArea(nua);

        this.messageLabelText = messageLabelText;
        
        numpadPanel = new GUI_JPanel();
        numpadPanel.setLayout(new GridBagLayout());
        numpadPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
        
        initalizeMessageLabel();
        initalizeTextField();
        
        this.numbers.setText(numbersLabelText.toUpperCase());

        gridConstraint.gridy = 1;
        gridConstraint.ipadx = 100;
        gridConstraint.ipady = 10;

        for (int i = 0; i < 10; i++) {
            JButton currButton = createNumpadButton("" + (i + 1) % 10);
            currButton.setActionCommand("INPUT_BUTTON: " + (i + 1) % 10);
            currButton.addActionListener(this.nc);
            numpadButtons[i] = currButton;
            gridConstraint.gridx = i % 3;
            gridConstraint.gridy = (i / 3) + 2;
            if (i == 9) {
                gridConstraint.gridx = 1;
            }
            numpadPanel.add(currButton, gridConstraint);
        }

        gridConstraint.gridy = 6;

        gridConstraint.gridx = 0;
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this.nc);
        numpadPanel.add(cancelButton, gridConstraint);

        gridConstraint.gridx = 1;
        correctButton.setActionCommand("correct");
        correctButton.addActionListener(this.nc);
        numpadPanel.add(correctButton, gridConstraint);

        gridConstraint.gridx = 2;
        submitButton.setActionCommand("submit");
        submitButton.addActionListener(this.nc);
        numpadPanel.add(submitButton, gridConstraint);

        addLayer(numpadPanel, 0);
    }

    private void initalizeMessageLabel() {
        message = new GUI_JLabel(messageLabelText.toUpperCase());
        message.setFont(GUI_Fonts.FRANKLIN_BOLD);
        message.setHorizontalAlignment(JLabel.CENTER);
        message.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

        int width = 405;
        int height = 50;

        GUI_JPanel centerPanel = new GUI_JPanel();
        centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
        centerPanel.setPreferredSize(new Dimension(width, height));
        centerPanel.setLayout(new GridLayout(1, 0));

        centerPanel.add(message);
        addLayer(centerPanel, 0);

    }

    private void initalizeTextField() {
        numbers = new JTextField();
        numbers.setFont(GUI_Fonts.FRANKLIN_BOLD);
        numbers.setHorizontalAlignment(JLabel.CENTER);
        numbers.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, 10));

        int width = 405;
        int height = 70;

        GUI_JPanel centerPanel = new GUI_JPanel();
        centerPanel.setBackground(GUI_Color_Palette.DARK_BLUE);
        centerPanel.setPreferredSize(new Dimension(width, height));
        centerPanel.setLayout(new GridLayout(1, 0));

        centerPanel.add(numbers);
        addLayer(centerPanel, 0);

    }

    @Override
    public void numberHasBeenUpdated(NumpadControl npc, String number) {
        numbers.setText(number);
    }

    private GUI_JButton createNumpadButton(String text) {
        int overallMargin = 10;

        /* Setup of the title's panel */
        GUI_JButton pinPadButton = new GUI_JButton();
        pinPadButton.setText(text);
        pinPadButton.setBackground(GUI_Color_Palette.DARK_BROWN);
        pinPadButton.setForeground(GUI_Color_Palette.WHITE);

        pinPadButton.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

        pinPadButton.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
        pinPadButton.setLayout(new BorderLayout());

        /* Adding the panel to the window */
        return pinPadButton;
    }

    @Override
    public void numpadCancelled(NumpadControl npc, String number) {
        
    }

    @Override
    public void numpadCorrected(NumpadControl npc, String number) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void numpadSubmitted(NumpadControl npc, String number) {
        // TODO Auto-generated method stub
        
    }
}
