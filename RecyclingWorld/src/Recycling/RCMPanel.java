package Recycling;

import Recycling.RecyclingData.RecyclingMachine;

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
        displayPanel.setBackground(Color.GRAY);
        displayPanel.setLayout(new BorderLayout());
            dispensePanel = new JPanel();
            dispensePanel.setBackground(Color.BLACK);
            displayPanel.add(dispensePanel, BorderLayout.SOUTH);
        add(displayPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.DARK_GRAY);
        // Add buttons.
        add(buttonsPanel, BorderLayout.EAST);


    }

    public RCMPanel(Color color, RecyclingMachine RCM) {
        this(color);
        this.RCM = RCM;
        updateRCMDisplay();
    }

    /* Display updating */
    public void updateRCMDisplay() {
        updateButtons();

        validate();
        repaint();
    }

    // To be called when the RCM's item list changes.
    public void updateButtons() {
        buttonsPanel.removeAll();
        for (String name:RCM.getPriceList().keySet()) {
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double weight = 2.5; // Eventually randomize this.
//                    RCM.depositItem(name, weight);
                    JOptionPane.showMessageDialog( null,
                            "Depositing "+weight+" pounds of "+name+".");
                }
            });
            button.setActionCommand(depositButtonPressedString);
            buttonsPanel.add(button);
//            buttonsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        }
    }

    /* Action Listener Methods */

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /* RCM manipulation */

}
