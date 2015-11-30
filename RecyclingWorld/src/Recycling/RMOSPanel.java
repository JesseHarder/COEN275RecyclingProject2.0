package Recycling;

import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.RecyclingMonitoringStation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * Created by JHarder on 11/28/15.
 */
public class RMOSPanel extends JPanel {
    private RecyclingMonitoringStation RMOS;
    private RCMPanel rcmPanel;

    private JPanel centerPanel;
        private JPanel displayArea;
        private DefaultListModel<String> rcmListModel;
        private JList rcmList;
    private JPanel buttonPanel;
        private JButton b1;

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
        RMOS = new RecyclingMonitoringStation();
        RMOS.testPrep();

        rcmPanel = null;

        setBackground(color);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(50));

        centerPanel = new JPanel();
        centerPanel.setBackground(color);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
            centerPanel.add(Box.createHorizontalStrut(20));

            displayArea = new JPanel();
            displayArea.setBackground(Color.WHITE);
            centerPanel.add(displayArea);

            centerPanel.add(Box.createHorizontalStrut(20));

            rcmListModel = new DefaultListModel<String>();
            rcmList = new JList();
            updateRCMList();
            rcmList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    updateRCMPanel();
                }
            });
            centerPanel.add(rcmList);

            centerPanel.add(Box.createHorizontalStrut(20));

        add(centerPanel);

        add(Box.createVerticalStrut(50));

        buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setLayout(new FlowLayout());
            b1 = new JButton("Button 1");
            buttonPanel.add(b1);
        add(buttonPanel);
    }

    public void updateRCMList() {
        rcmListModel.clear();

        for (RecyclingMachine RCM:RMOS.getMachines()) {
            rcmListModel.addElement(RCM.getID());
        }

        rcmList.setModel(rcmListModel);
        rcmList.setSelectedIndex(0);
    }

    public void updateRCMPanel() {
        if (rcmPanel != null) {
            int index = rcmList.getSelectedIndex();
            rcmPanel.setRCM(RMOS.getMachines().get(index));
        }
    }
}
