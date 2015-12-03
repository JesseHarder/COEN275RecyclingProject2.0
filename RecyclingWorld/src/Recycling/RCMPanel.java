package Recycling;

import Recycling.RecyclingData.RecHelper;
import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.Statistics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by JHarder on 11/28/15.
 */
public class RCMPanel extends JPanel {
    private RecyclingMachine RCM;

    private boolean inMetric;
    private boolean screenNeedsUpdating; // Used to tell if timer updates should do things.

    /* Interface elements */

    CardLayout cards;
    JPanel cardPanel;
        JPanel preAuthenticationCard;
        JPanel noRCMCard;
        JPanel simCard;
            JPanel labelPanel;
                JLabel rcmlabel;
            JPanel contentPanel;
                JPanel displayPanel;
                JPanel dispensePanel;
                    JLabel dispenserErrorMessage;
                JTextArea textArea;

                JPanel buttonsPanel;
                    JButton getCashButton;
                    JButton getCouponsButton;
                    JButton metricButton;

    /* Public constants */
    public static final String preAuthenticationCardString = "Pre-Authentication Card";
    public static final String noRCMCardString = "No RCM Card";
    public static final String simulationCardString = "Simulation Card";
    public static final String depositButtonPressedString = "Deposit Button Pressed";
    public static final String getCashButtonPressedString = "Get Cash Button Pressed";
    public static final String getCouponsButtonPressedString = "Get Coupons Button Pressed";
    public static final String metricButtonPressedString = "Metric Button Pressed";

    public static final String lightBlueColorString = "#b3e6ff";

    /* Getters and Setters */
    public RecyclingMachine getRCM() {return RCM;}
    public void setRCM(RecyclingMachine RCM) {
        this.RCM = RCM;
        updateRCMDisplay();
    }

    /* Constructors */

    public RCMPanel () {
        this(Color.GRAY);
    }

    public RCMPanel(Color color) {
        screenNeedsUpdating = false;
        inMetric = false;

        // Start by setting up cards at top level.
        setBackground(color);
        setLayout(new BorderLayout());
        cards = new CardLayout();
        cardPanel = new JPanel();
        cardPanel.setLayout(cards);

        /* Set up card for requesting authentication. */

            preAuthenticationCard = new JPanel();
            preAuthenticationCard.setBackground(Color.BLACK);
            preAuthenticationCard.setLayout(new BoxLayout(preAuthenticationCard,BoxLayout.X_AXIS));

                preAuthenticationCard.add(Box.createHorizontalGlue());
                JLabel pleaseAuthenticateLabel = new JLabel("Please Authenticate");
                pleaseAuthenticateLabel.setForeground(Color.WHITE);
                preAuthenticationCard.add(pleaseAuthenticateLabel, BorderLayout.CENTER);
                preAuthenticationCard.add(Box.createHorizontalGlue());

            cardPanel.add(preAuthenticationCard,preAuthenticationCardString);

        /* Set up No RCM Card. */
            noRCMCard = new JPanel();
            noRCMCard.setBackground(Color.BLACK);
            noRCMCard.setLayout(new BoxLayout(noRCMCard,BoxLayout.X_AXIS));

                noRCMCard.add(Box.createHorizontalGlue());
                JLabel noRCMLabel = new JLabel("No RCM to Simulate");
                pleaseAuthenticateLabel.setForeground(Color.RED);
                noRCMCard.add(pleaseAuthenticateLabel, BorderLayout.CENTER);
                noRCMCard.add(Box.createHorizontalGlue());

            cardPanel.add(noRCMCard,noRCMCardString);

        /* Set up card for primary functionality. */
            simCard = new JPanel();
            simCard.setBackground(color);
            simCard.setLayout(new BorderLayout());
                // Setup the label panel.
                labelPanel = new JPanel();
                labelPanel.setBackground(Color.WHITE);
                labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.X_AXIS));

                    labelPanel.add(Box.createHorizontalGlue());
                    rcmlabel = new JLabel("RCM");
                    Font f = rcmlabel.getFont();
                    rcmlabel.setFont(new Font(f.getName(), f.getStyle(), 24));
                    labelPanel.add(rcmlabel, BorderLayout.CENTER);
                    labelPanel.add(Box.createHorizontalGlue());

                simCard.add(labelPanel, BorderLayout.NORTH);

                // Setup main content panel.
                contentPanel = new JPanel();
                contentPanel.setBackground(simCard.getBackground());
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

                contentPanel.add(Box.createHorizontalStrut(10));

                    displayPanel = new JPanel();
                    displayPanel.setBackground(Color.GRAY);
                    displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

                        displayPanel.add(Box.createVerticalStrut(75));

                        textArea = new JTextArea();
                        textArea.setEditable(false);
                        textArea.setBackground(Color.decode("#b3e6ff"));
                        displayPanel.add(textArea);

                        displayPanel.add(Box.createVerticalStrut(75));

                        dispensePanel = new JPanel();
                        dispensePanel.setBackground(Color.BLACK);
                        dispensePanel.setLayout(new BoxLayout(dispensePanel, BoxLayout.Y_AXIS));
                        dispensePanel.add(Box.createVerticalStrut(20));
                            dispenserErrorMessage = new JLabel("Can't Deposit"); // Initially hidden.
                        dispensePanel.add(dispenserErrorMessage);
                        dispensePanel.add(Box.createVerticalStrut(20));
                        displayPanel.add(dispensePanel);

                        displayPanel.add(Box.createRigidArea(new Dimension(0,50)));


                contentPanel.add(displayPanel);

                contentPanel.add(Box.createHorizontalStrut(10));

                    /* Start code for buttons */

                    getCashButton = new JButton("Get Cash");
                    getCashButton.setActionCommand(getCashButtonPressedString);

                    getCouponsButton = new JButton("Get Coupons");
                    getCouponsButton.setActionCommand(getCouponsButtonPressedString);

                        ActionListener paymentActionListener = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String paymentType;
                                double amount;
                                // If not enough cash or user requested coupons.
                                if (!RCM.canWithdrawCashForSession()
                                        || e.getActionCommand().equals(getCouponsButtonPressedString)) {
                                    paymentType = " in coupons";
                                    amount = RCM.amountOwedForSession();
                                    RCM.startNewSession();
                                } else {
                                    paymentType = " as cash";
                                    amount = RCM.withdrawCashAndStartNewSession();
                                }
                                try {
                                    RCM.saveStatus();
                                } catch (FileNotFoundException e1) {
                                    System.out.println("Could not find file to save RCM with ID "+RCM.getID()+".");
                                }

                                String message = "Thank you for recycling. Here is $" + RCM.formatMoneyAmount(amount) + paymentType +".";
                                message += "\nPlease add another item to start a new session.";
                                textArea.setText(message);

    //                            scheduleScreenRefresh(5000);

                            }
                        };

                    getCashButton.addActionListener(paymentActionListener);
                    getCouponsButton.addActionListener(paymentActionListener);

                    metricButton = new JButton("Metric");
                    metricButton.setActionCommand(metricButtonPressedString);
                    metricButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            inMetric = !inMetric;
                            metricButton.setText((inMetric) ? "Imperial" : "Metric");
                            updateRCMDisplay();
                        }
                    });

                    buttonsPanel = new JPanel();
                    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
                    buttonsPanel.setBackground(Color.decode(lightBlueColorString));
                // Add buttons.
                contentPanel.add(buttonsPanel);

                contentPanel.add(Box.createHorizontalStrut(10));

            simCard.add(contentPanel, BorderLayout.CENTER);

        cardPanel.add(simCard, simulationCardString);

        if (RCM == null)
            cards.show(cardPanel, noRCMCardString);
        else
            cards.show(cardPanel, simulationCardString);

        add(cardPanel, BorderLayout.CENTER);
    }

    public RCMPanel(Color color, RecyclingMachine RCM) {
        this(color);
        this.RCM = RCM;
        updateRCMDisplay();
    }

    /* Display updating */
    public void updateRCMDisplay() {
        updateTextArea();
        updateButtons();
        updateDispensePanel();

        validate();
        repaint();
    }
    // Used if multi-threading is wanted.
    public void scheduleScreenRefresh(int miliseconds) {
        screenNeedsUpdating = true;
        Timer timer = new Timer(miliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (screenNeedsUpdating) {
                    updateRCMDisplay();
                }
            }
        });
        timer.start();
    }

    public void updateTextArea() {
        if (RCM != null) {
            if (!RCM.isActive())
                textArea.setText("RCM "+RCM.getID()+" is INACTIVE");
            else {
                String message = "RCM "+RCM.getID()+" is ACTIVE";

                String weightUnit = "lbs";

                for (String item:RCM.getPriceList().keySet())
                {
                    double weight = RCM.amountOfItemDepositedThisSession(item);
                    if (weight != 0.0)
                    {
                        double cash = RCM.priceForItemThisSession(item);
                        if (inMetric) {
                            weightUnit = "kgs";
                            weight = RCM.kilogramsForPounds(weight);
                        }
                        message += "\n"+ RecHelper.formatDoubleAmount(weight,2)+" "+weightUnit+"of " + item + ": $" + RCM.formatMoneyAmount(cash);
                    }
                }
                message += "\nTotal owed: $" + RCM.formatMoneyAmount(RCM.amountOwedForSession());

                textArea.setText(message);
            }
        }

        screenNeedsUpdating = false;
    }

    // To be called when the RCM's item list changes.
    public void updateButtons() {
        buttonsPanel.removeAll();
        if (RCM != null && RCM.isActive()) {
            for (Map.Entry<String,Double> entry:RCM.getPriceList().entrySet()) {
                final String name = entry.getKey();
                double price = entry.getValue();
                String weightUnit = "lb";

                if (inMetric) {
                    price = RCM.convertPriceToPricePerKilogram(price);
                    weightUnit = "kg";
                }

                JButton button = new JButton(name+": $"+RCM.formatMoneyAmount(price)+"/"+weightUnit);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String id = RCM.getID();
                        double price = RCM.getPriceList().get(name);
                        double weight = Math.random() * 10; // Eventually randomize this.
                        boolean hadRoomForDeposit = RCM.depositItem(name,weight);
                        try {
                            RCM.saveStatus();
                        } catch (FileNotFoundException e1) {
                            System.out.println("Could not find file to save RCM with ID "+RCM.getID()+".");
                        }
                        if (hadRoomForDeposit) {
                            Statistics.logTransaction(id,name,weight,price);
                            updateRCMDisplay();
                        } else {
                            dispensePanel.setBackground(Color.RED);
                            dispenserErrorMessage.setText("Not Enough Room");
//                            scheduleScreenRefresh(3000);
                        }
                    }
                });
                button.setActionCommand(depositButtonPressedString);
                buttonsPanel.add(button);
//            buttonsPanel.add(Box.createRigidArea(new Dimension(0,10)));
            }

            buttonsPanel.add(Box.createVerticalStrut(20));

            buttonsPanel.add(getCashButton);
            buttonsPanel.add(getCouponsButton);
            buttonsPanel.add(metricButton);
        }
    }

    public void updateDispensePanel() {
        dispensePanel.setBackground(Color.black);
        dispenserErrorMessage.setText("");
    }
}
