package org.hxnry.rsp.watcher_server.gui;

import org.hxnry.rsp.watcher_server.api.util.ImageDecoder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatEntry extends JPanel {

    JPanel content = new JPanel();

    public StatEntry(String iconPath, Object value) {
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        BufferedImage image = ImageDecoder.decodeToImage(iconPath);
        content.add(new JLabel(new ImageIcon(image)));
        content.add(Box.createRigidArea(new Dimension(5, 0)));
        content.add(new JLabel(String.valueOf(value)));
        add(content);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

}
