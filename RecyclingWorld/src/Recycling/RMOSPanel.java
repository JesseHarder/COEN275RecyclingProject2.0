package Recycling;

import Recycling.RecyclingData.RecHelper;
import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.RecyclingMonitoringStation;
import Recycling.RecyclingData.Statistics;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by JHarder on 11/28/15.
 */
class labeledTextField extends JPanel {
    JLabel label;
    JTextField textField;

    public labeledTextField() {
        this ("Default", 10);
    }

    public labeledTextField(String labelText, int fieldWidth) {
        setLayout(new FlowLayout());
        label = new JLabel(labelText);
        add(label);
        textField = new JTextField(fieldWidth);
        add(textField);
    }

    public JLabel getLabel() {return label;}
    public JTextField getTextField() {return textField;}
}

public class RMOSPanel extends JPanel {
    /* Data */
    private boolean loggedIn;
    private boolean updatingRCMJList;
    private RecyclingMonitoringStation RMOS;
    private RCMPanel rcmPanel;

    /* Public constants */
    public static final String lightBlueColorString = "#b3e6ff";
    public static final Color lightBlueColor = Color.decode(lightBlueColorString);

    public static final String authenticationCardString = "Authentication Card";
    public static final String controlCardString = "Control Card";
    public static final String loginButtonPressedString = "Login Button Pressed";

    public static final String pricesCardString = "Prices Card";
    public static final String machineStatCardString = "Machine Stat Card";
    public static final String globalStatCardString = "Global Stat Card";

    public static final String addMachineButtonPressedString = "Add Machine Button Pressed";
    public static final String removeMachineButtonPressedString = "Remove Machine Button Pressed";
    public static final String activateButtonPressedString = "Activate Button Pressed";
        public static final String activateString = "Activate";
        public static final String deactivateString = "Deactivate";
    public static final String emptyButtonPressedString = "Empty Button Pressed";
    public static final String editPricesButtonPressedString = "Edit Prices Button Pressed";
    public static final String showMachineStatsButtonPressedString = "Show Machine Stats Button Pressed";
    public static final String showGlobalStatsButtonPressedString = "Show Global Stats Button Pressed";
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
        JPanel labelPanel;
            JLabel rmosLabel;
        JPanel contentPanel;
            JPanel centerPanel;
                CardLayout displayCards;
                JPanel displayPanel;
                    JPanel pricesCard;
                    JPanel machineStatsCard;
                        JLabel currentRCMLabel;
                        JLabel currentRCMLocationLabel;
                        JLabel moneyInRCMLabel;
                        JLabel capacityRCMLabel;
                        JLabel weightOfItemsLabel;
                        JLabel timesEmptiedLabel;
                        JLabel lastEmptiedLabel;
                        JLabel timesUsedLabel;
                    JPanel globalStatsCard;
                        JLabel mostUsedByWeight;
                        JLabel mostUsedByPayout;
                        JLabel leastUsedByWeight;
                        JLabel leastUsedByPayout;
                        JLabel mostRecycledItem;
                DefaultListModel<String> rcmListModel;
                JScrollPane rcmJListScroll;
                JList rcmJList;
            JPanel buttonPanel1;
                JButton addMachineButton;
                JButton removeMachineButton;
                JButton activateDeactivateButton;
                JButton emptyMachineButton;
            JPanel buttonPanel2;
                JButton editPricesButton;
                JButton showMachineStatsButton;
                JButton showGlobalStatsButton;
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

    public RMOSPanel () throws SQLException, ClassNotFoundException {
        this (Color.LIGHT_GRAY);
    }

    public RMOSPanel(Color color) throws SQLException, ClassNotFoundException {
        // Data setup.
        loggedIn = false;
        updatingRCMJList = false;

        RMOS = new RecyclingMonitoringStation();
        RMOS.loadStatus();
//        RMOS.testPrep();

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
                passwordField = new JPasswordField(10);
                ((JPasswordField)passwordField).setEchoChar('*');
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
        controlCard.setLayout(new BorderLayout());

            labelPanel = new JPanel();
            labelPanel.setBackground(Color.WHITE);
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));

                labelPanel.add(Box.createHorizontalGlue());
                rmosLabel = new JLabel("RMOS");
                Font f = rmosLabel.getFont();
                rmosLabel.setFont(new Font(f.getName(), f.getStyle(), 24));
                labelPanel.add(rmosLabel);
                labelPanel.add(Box.createHorizontalGlue());

            controlCard.add(labelPanel, BorderLayout.NORTH);

            contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

                centerPanel = new JPanel();
                centerPanel.setBackground(color);
                centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
                    centerPanel.add(Box.createHorizontalStrut(20));

                    displayCards = new CardLayout();
                    displayPanel = new JPanel();
                    displayPanel.setBackground(lightBlueColor);
                    displayPanel.setLayout(displayCards);

                        pricesCard = new PriceEditingPanel(displayPanel.getBackground());
                        displayPanel.add(pricesCard, pricesCardString);

                        machineStatsCard = new JPanel();
                        machineStatsCard.setLayout(new BoxLayout(machineStatsCard,BoxLayout.Y_AXIS));
                        machineStatsCard.setBackground(displayPanel.getBackground());
                        // Add machine card stuff here.
                            currentRCMLabel = new JLabel();
                            machineStatsCard.add(currentRCMLabel);
                            currentRCMLocationLabel = new JLabel();
                            machineStatsCard.add(currentRCMLocationLabel);
                            moneyInRCMLabel = new JLabel();
                            machineStatsCard.add(moneyInRCMLabel);
                            capacityRCMLabel = new JLabel();
                            machineStatsCard.add(capacityRCMLabel);
                            weightOfItemsLabel = new JLabel();
                            machineStatsCard.add(weightOfItemsLabel);
                            timesEmptiedLabel = new JLabel();
                            machineStatsCard.add(timesEmptiedLabel);
                            lastEmptiedLabel = new JLabel();
                            machineStatsCard.add(lastEmptiedLabel);
                            timesUsedLabel = new JLabel();
                            machineStatsCard.add(timesUsedLabel);
                        displayPanel.add(machineStatsCard, machineStatCardString);

                        globalStatsCard = new JPanel();
                        globalStatsCard.setLayout(new BoxLayout(globalStatsCard,BoxLayout.Y_AXIS));
                        globalStatsCard.setBackground(displayPanel.getBackground());
                        // Add global stats stuff here.
                            mostUsedByWeight = new JLabel();
                            globalStatsCard.add(mostUsedByWeight);
                            leastUsedByWeight = new JLabel();
                            globalStatsCard.add(leastUsedByWeight);
                            mostUsedByPayout = new JLabel();
                            globalStatsCard.add(mostUsedByPayout);
                            leastUsedByPayout = new JLabel();
                            globalStatsCard.add(leastUsedByPayout);
                            mostRecycledItem = new JLabel();
                            globalStatsCard.add(mostRecycledItem);
                            updateGlobalStats();


                        displayPanel.add(globalStatsCard, globalStatCardString);

                    displayCards.show(displayPanel,globalStatCardString);
                    centerPanel.add(displayPanel);

                    centerPanel.add(Box.createHorizontalStrut(20));

                    rcmListModel = new DefaultListModel<String>();
                    rcmJListScroll = new JScrollPane();
                    rcmJList = new JList();
                    updateRCMList();
                    rcmJList.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            if (!updatingRCMJList) {
                                try {
                                    updateMachineStats();
                                    updateGlobalStats();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                } catch (ClassNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                updateButtonPanel();
                                updateRCMPanel();
                            }
                        }
                    });
                    rcmJListScroll.setViewportView(rcmJList);
                    centerPanel.add(rcmJListScroll);

                contentPanel.add(centerPanel);

                buttonPanel1 = new JPanel();
                buttonPanel1.setBackground(Color.DARK_GRAY);
                buttonPanel1.setLayout(new FlowLayout());

                    // Setup add machine button
                    addMachineButton = new JButton("Add Machine");
                    addMachineButton.setActionCommand(addMachineButtonPressedString);
                    addMachineButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Add machine button logic here.
                            RMOS.addMachine();
                            RMOS.saveStatus();
                            try {
                                updateGlobalStats();
                                updateMachineStats();
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            }
                            updateButtonPanel();
                            updateRCMList();
                        }
                    });

                    // Setup remove machine button
                    removeMachineButton = new JButton("Remove Machine");
                    removeMachineButton.setActionCommand(removeMachineButtonPressedString);
                    removeMachineButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Remove machine button logic here.
                            if (!RMOS.getMachines().isEmpty()) {
                                RecyclingMachine RCM = selectedRCM();
                                RMOS.removeMachineWithID(RCM.getID());
                                RMOS.saveStatus();

                                updateRCMList();
                                try {
                                    updateMachineStats();
                                    updateGlobalStats();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                } catch (ClassNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                updateButtonPanel();
                                updateRCMPanel();
                        }
                    }});

                    // Setup activate button
                    activateDeactivateButton = new JButton(activateString);
                    activateDeactivateButton.setActionCommand(activateButtonPressedString);
                    activateDeactivateButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (!RMOS.getMachines().isEmpty()) {
                                RecyclingMachine RCM = selectedRCM();
                                RCM.setActive(!RCM.isActive());
                                try {
                                    RCM.saveStatus();
                                } catch (FileNotFoundException except) {
                                    System.out.println("Could not find file to save RCM with ID "+RCM.getID()+".");
                                }
                                try {
                                    updateMachineStats();
                                    updateGlobalStats();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                } catch (ClassNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                updateButtonPanel();
                                updateRCMPanel();
                            }
                        }
                    });

                    // Setup empty button
                    emptyMachineButton = new JButton("Empty Machine");
                    emptyMachineButton.setActionCommand(emptyButtonPressedString);
                    emptyMachineButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Empty button logic here.
                            if(!RMOS.getMachines().isEmpty()){
                                RecyclingMachine RCM = selectedRCM();
                                RCM.empty();
                                try {
                                    RCM.saveStatus();
                                } catch (FileNotFoundException except) {
                                    System.out.println("Could not find file to save RCM with ID "+RCM.getID()+".");
                                }
                                try {
                                    updateGlobalStats();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                } catch (ClassNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                try {
                                    updateMachineStats();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                } catch (ClassNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                updateButtonPanel();
                                updateRCMPanel();
                            }
                        }
                    });

                buttonPanel2 = new JPanel();
                buttonPanel2.setBackground(Color.DARK_GRAY);
                buttonPanel2.setLayout(new FlowLayout());

                    // Setup show stats button.
                    editPricesButton = new JButton("Edit Prices");
                    editPricesButton.setActionCommand(editPricesButtonPressedString);
                    editPricesButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            displayCards.show(displayPanel, pricesCardString);
                        }
                    });

                    // Setup show stats button.
                    showMachineStatsButton = new JButton("Show Machine Stats");
                    showMachineStatsButton.setActionCommand(showMachineStatsButtonPressedString);
                    showMachineStatsButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                updateMachineStats();
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            }
                            displayCards.show(displayPanel, machineStatCardString);
                        }
                    });

                    // Setup show stats button.
                    showGlobalStatsButton = new JButton("Show Global Stats");
                    showGlobalStatsButton.setActionCommand(showGlobalStatsButtonPressedString);
                    showGlobalStatsButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                updateGlobalStats();
                                updateMachineStats();
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            }
                            displayCards.show(displayPanel, globalStatCardString);
                        }
                    });

                    // Setup logout button.
                    logoutButton = new JButton("Logout");
                    logoutButton.setActionCommand(logoutButtonPressedString);
                    logoutButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            RMOS.saveStatus();
                            loggedIn = false;
                            cards.show(cardPanel, authenticationCardString);
                            updateRCMPanel();
                        }
                    });

                    updateButtonPanel();
                contentPanel.add(buttonPanel1);
                contentPanel.add(buttonPanel2);

            controlCard.add(contentPanel, BorderLayout.CENTER);

        cardPanel.add(authenticationCard, authenticationCardString);
        cardPanel.add(controlCard, controlCardString);

        cards.show(cardPanel, authenticationCardString); // Might need to move this up.
        add(cardPanel, BorderLayout.CENTER);
    }

    public void updateRCMList() {
        updatingRCMJList = true;
        int indexTemp = rcmJList.getSelectedIndex();
        rcmListModel.clear();

        for (RecyclingMachine RCM:RMOS.getMachines()) {
            rcmListModel.addElement(RCM.getID() + "-" + RCM.getLocation());
        }

        rcmJList.setModel(rcmListModel);
        updatingRCMJList = false;
        if (indexTemp < 0) {
            rcmJList.setSelectedIndex(0);
        } else if (indexTemp >= rcmJList.getModel().getSize()) {
            rcmJList.setSelectedIndex(rcmJList.getModel().getSize() - 1);
        } else {
            rcmJList.setSelectedIndex(indexTemp);
        }
    }

    //Returns the index for the currently select RCM.
    //Returns -1 if the list is empty.
    public int selectedRCMIndex() {
        if (RMOS.getMachines().isEmpty() || rcmJList.getModel().getSize() == 0)
        {
            return -1;
        }
        else
        {
            return rcmJList.getSelectedIndex();
        }
    }

    //Returns the currently select RCM.
    //Returns null if the list is empty.
    public RecyclingMachine selectedRCM() {
        if (RMOS.getMachines().isEmpty() || rcmJList.getModel().getSize() == 0)
            return null;
        else
            System.out.println(RMOS);
            return RMOS.getMachines().get(rcmJList.getSelectedIndex());
    }

    // Updates any buttons on the button panel that are dynamic.
    public void updateButtonPanel() {
        boolean RMOSisEmpty = RMOS.getMachines().isEmpty();
        boolean jListIsEmpty = rcmJList.getModel().getSize() == 0;

        if (RMOSisEmpty || jListIsEmpty) {
            activateDeactivateButton.setText(activateString);
        } else {
            int index = selectedRCMIndex();
            RecyclingMachine RCM = RMOS.getMachines().get(index);
            // Choosing whether activate button reads "activate" or "deactivate".
            if (RCM.isActive())
                activateDeactivateButton.setText(deactivateString);
            else
                activateDeactivateButton.setText(activateString);
        }

        // Button Panel 1
        buttonPanel1.removeAll();
        buttonPanel1.add(addMachineButton);
        if (!RMOSisEmpty) buttonPanel1.add(removeMachineButton);
        if (!RMOSisEmpty) buttonPanel1.add(activateDeactivateButton);
        if (!RMOSisEmpty) buttonPanel1.add(emptyMachineButton);

        // Button Panel 2
        buttonPanel2.add(editPricesButton);
        if (!RMOSisEmpty) buttonPanel2.add(showMachineStatsButton);
        buttonPanel2.add(showGlobalStatsButton);
        buttonPanel2.add(logoutButton);
    }

    public void updateRCMPanel() {
        if (rcmPanel != null) {
            if (loggedIn) {
                if (RMOS.getMachines().isEmpty() || rcmJList.getModel().getSize() == 0) {
                    rcmPanel.setRCM(null);
                    rcmPanel.cards.show(rcmPanel.cardPanel,RCMPanel.simulationCardString);
                } else {
                    int index = selectedRCMIndex();
                    rcmPanel.setRCM(RMOS.getMachines().get(index));
                    rcmPanel.cards.show(rcmPanel.cardPanel,RCMPanel.simulationCardString);
                    rcmPanel.updateRCMDisplay();
                }
            } else {
                rcmPanel.cards.show(rcmPanel.cardPanel,RCMPanel.preAuthenticationCardString);
            }
        }
    }

    public void updateMachineStats() throws SQLException, ClassNotFoundException {
        if (!RMOS.getMachines().isEmpty()) {
            RecyclingMachine sRCM = selectedRCM();
            String selectedRCMID = sRCM.getID();
            String selectedRCMLocation = sRCM.getLocation();

            currentRCMLabel.setText("Viewing Data for Recycling Machine ID: "+selectedRCMID);
            currentRCMLocationLabel.setText("Machine location: "+selectedRCMLocation);
//        moneyInRCMLabel.setText("Cash in Recycling Machine: $"+RecHelper.formatDoubleAmount(sRCM.getMoneyManager().getCashReserves(), 2));
            moneyInRCMLabel.setText("Cash in Recycling Machine: $"+sRCM.getCashReserves());
            capacityRCMLabel.setText("Remaining Capacity of RCM: "+RecHelper.formatDoubleAmount(sRCM.getItemContainer().getRemainingSpace(),2)+" ("+String.valueOf(sRCM.getItemContainer().getContentsWeight())+" used of "+String.valueOf(sRCM.getItemContainer().getWeightCapacity())+")");
            weightOfItemsLabel.setText("Total Weight of All Items Recycled: "+String.valueOf(Statistics.getTotalItemWeightByID(selectedRCMID)));
            timesEmptiedLabel.setText("Number of Times Emptied: "+String.valueOf(Statistics.timesEmptied(selectedRCMID)));
            lastEmptiedLabel.setText("Last Emptied: "+sRCM.getItemContainer().lastEmptied());
            timesUsedLabel.setText("Number of Times Used: "+String.valueOf(Statistics.timesUsed(selectedRCMID)));
        } else {
            currentRCMLabel.setText("No machine.");
            currentRCMLocationLabel.setText("No machine.");
            moneyInRCMLabel.setText("No machine.");
            capacityRCMLabel.setText("No machine.");
            weightOfItemsLabel.setText("No machine.");
            timesEmptiedLabel.setText("No machine.");
            lastEmptiedLabel.setText("No machine.");
            timesUsedLabel.setText("No machine.");
        }
    }

    public void updateGlobalStats() throws SQLException, ClassNotFoundException {
        mostUsedByWeight.setText("Most Used Machine by Weight: "+ Statistics.mostUsedByWeight()+"("+Statistics.highestWeight()+")");
        mostUsedByPayout.setText("Most Used Machine by Payout: "+ Statistics.mostUsedByPayout()+"($"+Statistics.highestPayout()+")");
        leastUsedByWeight.setText("Least Used Machine by Weight: "+ Statistics.leastUsedByWeight()+"("+Statistics.lowestWeight()+")");
        leastUsedByPayout.setText("Least Used Machine by Payout: "+ Statistics.leastUsedByPayout()+"($"+Statistics.lowestPayout()+")");
        mostRecycledItem.setText("Most Recycled Item: "+ Statistics.mostRecycledItem());

    }

    /* Auto-saving thread functions */

    /* Price Editing Panel Class */
    class PriceEditingPanel extends JPanel {
        public static final String setPriceErrorString = "Price must be real number.";

        boolean updatingPricesJList;

        JPanel upperPricePanel;
        labeledTextField nameField;
        labeledTextField priceField;
        JLabel setPriceErrorLabel;
        JButton setPriceButton;
        JPanel lowerPricePanel;
        JScrollPane pricesJListScroll;
        DefaultListModel<String> pricesListModel;
        JList pricesJList;
        JButton removePriceButton;


        public PriceEditingPanel () {
            this (Color.GRAY);
        }

        public PriceEditingPanel (Color color) {
            updatingPricesJList = false;

            setBackground(color);
            setLayout(new BorderLayout());

            upperPricePanel = new JPanel();
            upperPricePanel.setBackground(getBackground());
            upperPricePanel.setLayout(new FlowLayout());

            nameField = new labeledTextField("Item Name:",10);
            nameField.setBackground(upperPricePanel.getBackground());
            upperPricePanel.add(nameField);

            priceField = new labeledTextField("Price:",10);
            priceField.setBackground(upperPricePanel.getBackground());
            upperPricePanel.add(priceField);

            setPriceButton = new JButton("Set Price");
            setPriceButton.setBackground(upperPricePanel.getBackground());
            setPriceButton.setActionCommand("Set Price Button Pressed");
            setPriceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getTextField().getText();
                    String priceString = priceField.getTextField().getText();

                    if (RecHelper.isDouble(priceString)) {
                        setPriceErrorLabel.setText("");
                        double price = Double.parseDouble(priceString);
                        RMOS.setPrice(name,price);
                        RMOS.saveStatus();
                        updatePricesJList();
                    } else {
                        setPriceErrorLabel.setText(setPriceErrorString);
                    }
                }
            });
            upperPricePanel.add(setPriceButton);

            setPriceErrorLabel = new JLabel("");
            setPriceErrorLabel.setForeground(Color.RED);
            setPriceErrorLabel.setBackground(upperPricePanel.getBackground());
            upperPricePanel.add(setPriceErrorLabel);

            add(upperPricePanel, BorderLayout.NORTH);

            add(Box.createHorizontalStrut(5));

            lowerPricePanel = new JPanel();
            lowerPricePanel.setBackground(color);
            lowerPricePanel.setLayout(new FlowLayout());

            pricesListModel = new DefaultListModel<String>();
            pricesJListScroll = new JScrollPane();
            pricesJList = new JList();
            updatePricesJList();
            pricesJList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!updatingPricesJList) {
                        updateRCMPanel();
                    }
                }
            });
            pricesJListScroll.setViewportView(pricesJList);
            lowerPricePanel.add(pricesJListScroll);

            removePriceButton = new JButton("Remove Price");
            removePriceButton.setActionCommand("Remove Price Button Pressed");
            removePriceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String val = (String)pricesJList.getSelectedValue();
                    String name = val.split(":")[0];
                    RMOS.removePrice(name);
                    RMOS.saveStatus();
                    updatePricesJList();
                    updateRCMPanel();
                }
            });
            lowerPricePanel.add(removePriceButton);

            add(lowerPricePanel, BorderLayout.CENTER);
        }

        public void updatePricesJList() {
            updatingPricesJList = true;
            int indexTemp = pricesJList.getSelectedIndex();
            pricesListModel.clear();

            for (Map.Entry<String, Double> entry:RMOS.getPriceList().entrySet()) {
                String itemName = entry.getKey();
                String itemPrice = entry.getValue().toString();
                String listEntry = itemName + ": $" + itemPrice;
                pricesListModel.addElement(listEntry);
            }

            pricesJList.setModel(pricesListModel);
            updatingPricesJList = false;
            if (indexTemp < 0) {
                pricesJList.setSelectedIndex(0);
            } else if (indexTemp >= pricesJList.getModel().getSize()) {
                pricesJList.setSelectedIndex(pricesJList.getModel().getSize() - 1);
            } else {
                pricesJList.setSelectedIndex(indexTemp);
            }
        }
    }
}
