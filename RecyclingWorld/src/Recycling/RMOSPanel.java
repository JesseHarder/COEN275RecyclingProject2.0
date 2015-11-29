package Recycling;

import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import java.awt.*;

/**
 * Created by JHarder on 11/28/15.
 */
public class RMOSPanel extends JPanel {

    public RMOSPanel () {
        this (Color.LIGHT_GRAY);
    }

    public RMOSPanel(Color color) {
        setBackground(color);
    }
}
