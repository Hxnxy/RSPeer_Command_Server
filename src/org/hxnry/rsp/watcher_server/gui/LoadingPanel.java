package org.hxnry.rsp.watcher_server.gui;


import org.hxnry.rsp.watcher_server.api.graphics.FlashingText;
import org.hxnry.rsp.watcher_server.api.util.Delta;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    Delta delta = new Delta();
    FlashingText flashingText = new FlashingText("Loading Data...");

    public LoadingPanel() {
        flashingText.setSize(22);
        flashingText.setSpeed(500);
    }

    @Override
    public void paintComponent(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
        double d = (double) delta.getDelta();
        flashingText.setBounds(this.getBounds());
        flashingText.draw(g, d);
        //sflashingText.setMessage(Double.toString(d));
    }
}
