package Recycling;

import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.RecyclingMonitoringStation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by JHarder on 11/28/15.
 */
public class RMOSPanel extends JPanel {
    /* Data */
    private boolean loggedIn;
    private RecyclingMonitoringStation RMOS;
    private RCMPanel rcmPanel;

    /* Public constants */
    public static final String authenticationCardString = "Authentication Card";
    public static final String controlCardString = "Control Card";
    public static final String loginButtonPressedString = "Login Button Pressed";
    public static final String logoutButtonPressedString = "Logout Button Pressed";

    /* Interface Elements */
    CardLayout cards;
    JPanel cardPanel;

    // For authentication.
    JPanel authenticationCard;
        JLabel usernameLabel, passwordLabel;
        JTextField usernameField, passwordField;
        JButton loginButton;
        JLabel passwordErrorMessageLabel;

    // For the actual RMOS controls.
    JPanel controlCard;
        JPanel centerPanel;
            JPanel displayArea;
            DefaultListModel<String> rcmListModel;
            JList rcmJList;
        JPanel buttonPanel;
            JButton addMachineButton;
            JButton removeMachineButton;
            JButton logoutButton;

    /* Getters and Setters */
    public RecyclingMonitoringStation getRMOS() {
        return RMOS;
    }
    public void setRMOS(RecyclingMonitoringStation RMOS) {
        this.RMOS = RMOS;
    }

    public RCMPanel getRcmPanel() {return rcmPanel;}
    public void setRCMPanel(RCMPanel rcmPanel) {
        this.rcmPanel = rcmPanel;
        updateRCMPanel();
    }

    public JPanel getCenterPanel() {return centerPanel;}

    /* Constructors */

    public RMOSPanel () {
        this (Color.LIGHT_GRAY);
    }

    public RMOSPanel(Color color) {
        // Data setup.
        loggedIn = false;

        RMOS = new RecyclingMonitoringStation();
        RMOS.testPrep();

        rcmPanel = null;

        // Interface Setup.

        // Start by setting up cards at top level.
        setBackground(color);
        setLayout(new BorderLayout());
        cards = new CardLayout();
        cardPanel = new JPanel();
        cardPanel.setLayout(cards);

        // Set up the authentication card.

        authenticationCard = new JPanel();
        authenticationCard.setBackground(color);
        authenticationCard.setLayout(new BoxLayout(authenticationCard, BoxLayout.Y_AXIS));

            authenticationCard.add(Box.createVerticalGlue());

            JPanel usernamePanel = new JPanel();
            usernamePanel.setBackground(color);
            usernamePanel.setLayout(new FlowLayout());
                usernameLabel = new JLabel("Username:");
                usernamePanel.add(usernameLabel);
                usernameField = new JTextField(10);
                usernamePanel.add(usernameField);
            authenticationCard.add(usernamePanel);

            JPanel passwordPanel = new JPanel();
            passwordPanel.setBackground(color);
            passwordPanel.setLayout(new FlowLayout());
                passwordLabel = new JLabel("Password:");
                passwordPanel.add(passwordLabel);
                passwordField = new JTextField(10);
                passwordPanel.add(passwordField);
            authenticationCard.add(passwordPanel);


            loginButton = new JButton("Login");
            loginButton.setActionCommand(loginButtonPressedString);
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    boolean verified = RMOS.verifyCredentials(username,password);

                    // Either switch view or display error message.
                    if (verified) {
                        loggedIn = true;
                        passwordErrorMessageLabel.setVisible(false);
                        cards.show(cardPanel, controlCardString);
                        updateRCMPanel();
                    } else {
                       passwordErrorMessageLabel.setVisible(true);
                    }
                }
            });
            authenticationCard.add(loginButton);

            passwordErrorMessageLabel = new JLabel("Incorrect username or password. Please try again.");
            passwordErrorMessageLabel.setForeground(Color.RED);
            passwordErrorMessageLabel.setVisible(false);
            authenticationCard.add(passwordErrorMessageLabel);

            authenticationCard.add(Box.createVerticalGlue());

        // Now Set up the control card.

        controlCard = new JPanel();

        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));

            centerPanel = new JPanel();
            centerPanel.setBackground(color);
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
                centerPanel.add(Box.createHorizontalStrut(20));

                displayArea = new JPanel();
                displayArea.setBackground(Color.WHITE);
                centerPanel.add(displayArea);

                centerPanel.add(Box.createHorizontalStrut(20));

                rcmListModel = new DefaultListModel<String>();
                rcmJList = new JList();
                updateRCMList();
                rcmJList.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        updateRCMPanel();
                    }
                });
                centerPanel.add(rcmJList);

            controlCard.add(centerPanel);

            buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.DARK_GRAY);
            buttonPanel.setLayout(new FlowLayout());
                logoutButton = new JButton("Logout");
                logoutButton.setActionCommand(logoutButtonPressedString);
                logoutButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loggedIn = false;
                        cards.show(cardPanel, authenticationCardString);
                        updateRCMPanel();
                    }
                });
                buttonPanel.add(logoutButton);
            controlCard.add(buttonPanel);

        cardPanel.add(authenticationCard, authenticationCardString);
        cardPanel.add(controlCard, controlCardString);

        cards.show(cardPanel, authenticationCardString); // Might need to move this up.
        add(cardPanel, BorderLayout.CENTER);
    }

    public void updateRCMList() {
        rcmListModel.clear();

        for (RecyclingMachine RCM:RMOS.getMachines()) {
            rcmListModel.addElement(RCM.getID());
        }

        rcmJList.setModel(rcmListModel);
        rcmJList.setSelectedIndex(0);
    }

    public void updateRCMPanel() {
        if (rcmPanel != null) {
            if (loggedIn) {
                if (RMOS.getMachines().isEmpty()) {
                    rcmPanel.setRCM(null);
                    rcmPanel.cards.show(rcmPanel.cardPanel,RCMPanel.simulationCardString);
                } else {
                    int index = rcmJList.getSelectedIndex();
                    rcmPanel.setRCM(RMOS.getMachines().get(index));
                    rcmPanel.cards.show(rcmPanel.cardPanel,RCMPanel.simulationCardString);
                }
            } else {
                rcmPanel.cards.show(rcmPanel.cardPanel,RCMPanel.preAuthenticationCardString);
            }
        }
    }
}
