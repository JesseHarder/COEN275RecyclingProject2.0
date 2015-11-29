package Recycling;

import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.Statistics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by JHarder on 11/28/15.
 */
public class RCMPanel extends JPanel implements ActionListener {
    private RecyclingMachine RCM;

    /* Interface elements */
    private JPanel displayPanel;
    private JPanel buttonsPanel;
    private JPanel dispensePanel;

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

    /* Constructors */

    public RCMPanel () {
        this(Color.GRAY);
    }

    public RCMPanel(Color color) {
        setBackground(color);
        setLayout(new BorderLayout());

        displayPanel = new JPanel();
        displayPanel.setBackground(Color.RED);
        add(displayPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.green);
        // Add buttons.
        add(buttonsPanel, BorderLayout.EAST);

        dispensePanel = new JPanel();
        dispensePanel.setBackground(Color.yellow);
        add(dispensePanel, BorderLayout.SOUTH);
    }

    public RCMPanel(Color color, RecyclingMachine RCM) {
        this(color);
        this.RCM = RCM;
        updateRCMDisplay();
    }

    /* Display updating */
    public void updateRCMDisplay() {
        updateButtons();
    }

    // To be called when the RCM's item list changes.
    public void updateButtons() {
        buttonsPanel.removeAll();
        for (String name:RCM.getPriceList().keySet()) {
            JButton button = new JButton(name);
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
            buttonsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        }
    }

    /* Action Listener Methods */

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /* RCM manipulation */

}
