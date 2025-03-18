package org.hxnry.rsp.watcher_server;

import org.hxnry.rsp.watcher_server.gui.ScoutViewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Launch {

    public static ScoutViewer scoutViewer;
    public static Font customFont;

    public static void main(String[] args) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader.getResourceAsStream("runescape_uf.ttf")) {
            customFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(15f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                scoutViewer = new ScoutViewer(6973);
                JFrame.setDefaultLookAndFeelDecorated(false);
                ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
                PopupFactory.setSharedInstance(new PopupFactory());
                scoutViewer.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

