package Recycling;

import Recycling.RecyclingData.RecyclingMachine;
import Recycling.RecyclingData.RecyclingMonitoringStation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by JHarder on 11/28/15.
 */
public class RecyclingDemo {
    // Data
    private RecyclingMonitoringStation RMOS;

    // Interface
    private JFrame mainFrame;
    public RCMPanel rcmPanel;       // Change this back to private eventually.
    private RMOSPanel rmosPanel;

    public RecyclingDemo() {
        mainFrame = new JFrame("Recycling Demo");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(900,600));

        rmosPanel= new RMOSPanel();
        // Set up the RMOSPanel.

        // For testing.
        RecyclingMachine testRCM = new RecyclingMachine();
        testRCM.testPrep();

        rcmPanel = new RCMPanel();
        rcmPanel.setRCM(testRCM);
        // Set up the RCMPanel.

        Container contentPane = mainFrame.getContentPane();

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        contentPane.add(rcmPanel);
        // Possibly call a setup function for rcmPanel here.
        contentPane.add(Box.createRigidArea(new Dimension(10,0)));
        contentPane.add(rmosPanel);
        // Possibly call a setup function for rmosPanel here.


        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
