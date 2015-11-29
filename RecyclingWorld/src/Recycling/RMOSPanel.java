package Recycling;

import javax.swing.*;
import java.awt.*;

/**
 * Created by JHarder on 11/28/15.
 */
public class RMOSPanel extends JPanel {
    private JPanel centerPanel;
        private JPanel displayArea;
        private JList rcmList;
    private JPanel buttonPanel;
        JButton b1;

    public RMOSPanel () {
        this (Color.LIGHT_GRAY);
    }

    public RMOSPanel(Color color) {
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

            rcmList = new JList();
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
}
