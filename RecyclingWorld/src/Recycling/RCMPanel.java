package Recycling;

import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.Statistics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Created by JHarder on 11/28/15.
 */
public class RCMPanel extends JPanel implements ActionListener {
    private RecyclingMachine RCM;

    /* Interface elements */
    private JPanel displayPanel;
    private JPanel dispensePanel;
    private JTextArea textArea;

    private JPanel buttonsPanel;
    private JButton getPaidButton;

    /* Public constants */
    public static final String depositButtonPressedString = "Deposit Button Pressed";

    /* Getters and Setters */
    public RecyclingMachine getRCM() {return RCM;}
    public void setRCM(RecyclingMachine RCM) {
        this.RCM = RCM;
        updateRCMDisplay();
    }

    public JPanel getDisplayPanel() {return displayPanel;}
    public JPanel getButtonsPanel() {return buttonsPanel;}
    public JPanel getDispensePanel() {return dispensePanel;}
    public JTextArea getTextArea() {return textArea;}

    /* Constructors */

    public RCMPanel () {
        this(Color.GRAY);
    }

    public RCMPanel(Color color) {
        setBackground(color);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalStrut(10));

        displayPanel = new JPanel();
        displayPanel.setBackground(Color.GRAY);
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

            displayPanel.add(Box.createVerticalStrut(75));

            textArea = new JTextArea();
            textArea.setEditable(false);
            displayPanel.add(textArea);

            displayPanel.add(Box.createVerticalStrut(75));

            dispensePanel = new JPanel();
            dispensePanel.setBackground(Color.BLACK);
            dispensePanel.setLayout(new BoxLayout(dispensePanel, BoxLayout.Y_AXIS));
            dispensePanel.add(Box.createVerticalStrut(40));
            displayPanel.add(dispensePanel);

            displayPanel.add(Box.createRigidArea(new Dimension(0,50)));

        add(displayPanel);

        add(Box.createHorizontalStrut(10));

        getPaidButton = new JButton("Get Paid");
        // Steup what button does here.

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.DARK_GRAY);
        // Add buttons.
        add(buttonsPanel);

        add(Box.createHorizontalStrut(10));
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

        validate();
        repaint();
    }

    public void updateTextArea() {
        if (RCM != null) {
            if (!RCM.isActive())
                textArea.setText("RCM "+RCM.getID()+" is INACTIVE");
            else {
                textArea.setText("RCM "+RCM.getID()+" is ACTIVE");
            }

        }
    }

    // To be called when the RCM's item list changes.
    public void updateButtons() {
        buttonsPanel.removeAll();
        for (Map.Entry<String,Double> entry:RCM.getPriceList().entrySet()) {
            String name = entry.getKey();
            double price = entry.getValue();
            JButton button = new JButton(name+": $"+price+"/lb");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String id = RCM.getID();
                    double price = RCM.getPriceList().get(name);
                    double weight = 2.5; // Eventually randomize this.
                    Statistics.logTransaction(id,name,weight,price);
//                    RCM.depositItem(name, weight);
                    JOptionPane.showMessageDialog( null,
                            "Depositing "+weight+" pounds of "+name+".");
                }
            });
            button.setActionCommand(depositButtonPressedString);
            buttonsPanel.add(button);
//            buttonsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        }

        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(getPaidButton);
    }

    /* Action Listener Methods */

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /* RCM manipulation */

}
