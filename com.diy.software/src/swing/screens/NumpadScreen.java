package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.diy.software.controllers.StationControl;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class NumpadScreen extends Screen {

    private ActionListener controller;

    private GridBagConstraints gridConstraint = new GridBagConstraints();

    private JButton[] numpadButtons = new JButton[10];
    private JButton cancelButton = createPinPadButton("X");
    private JButton correctButton = createPinPadButton("O");
    private JButton submitButton = createPinPadButton(">");

    GUI_JPanel numpadPanel;

    JLabel message;
    String messageLabelText;

    JButton numbers;

    public NumpadScreen(StationControl sc, String headerText, ActionListener controller, String messageLabelText, String numbersLabelText) {
        super(sc, headerText);

        //TODO: pass in listeners? -> did i break it by passing in an ActionListener? -> have interface that implements ActionListener and add a addListener() method?
        //pinPadController = sc.getPinPadControl();
        //pinPadController.addListener(this);

        this.controller = controller;
        this.messageLabelText = messageLabelText;
        //HAVE THIS LIKE THIS OR COPY messageLabelText?
        this.numbers.setText(numbersLabelText.toUpperCase());

        numpadPanel = new GUI_JPanel();
        numpadPanel.setLayout(new GridBagLayout());
        numpadPanel.setBackground(GUI_Color_Palette.DARK_BLUE);

        initalizeMessageLabel();
        initalizeTextField();

        gridConstraint.gridy = 1;
        gridConstraint.ipadx = 100;
        gridConstraint.ipady = 10;

        for (int i = 0; i < 10; i++) {
            JButton currButton = createPinPadButton("" + (i + 1) % 10);
            currButton.setActionCommand("INPUT_BUTTON: " + (i + 1) % 10);
            //TODO: change controller class to generic (this.?)
            currButton.addActionListener(this.controller);
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
        //TODO: change controller (this.?)
        cancelButton.addActionListener(this.controller);
        numpadPanel.add(cancelButton, gridConstraint);

        gridConstraint.gridx = 1;
        correctButton.setActionCommand("correct");
        //TODO: change controller (this.?)
        correctButton.addActionListener(this.controller);
        numpadPanel.add(correctButton, gridConstraint);

        gridConstraint.gridx = 2;
        submitButton.setActionCommand("submit");
        //TODO: change controller (this.?)
        submitButton.addActionListener(this.controller);
        numpadPanel.add(submitButton, gridConstraint);

        addLayer(numpadPanel, 0);
    }

    private void initalizeMessageLabel() {
        message = new GUI_JLabel(messageLabelText.toUpperCase());
        message.setFont(GUI_Fonts.FRANKLIN_BOLD);
        message.setHorizontalAlignment(JLabel.CENTER);

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
        //TODO: look at above comment in constructor
        numbers = new JButton();
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

    private GUI_JButton createPinPadButton(String text) {
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

    //TODO: give the interfaces default implementations of the methods to prevent needing funky shit with controllers, or just have it in controller (idk if good to have buttons n shit in the interface)
        //maybe make a more structured listener group? -> just one with one more method (some generic fieldHasBeenUpdated()) -> probably pointless

}
