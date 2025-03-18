package org.hxnry.rsp.watcher_server.gui.models.renderers;

import org.hxnry.rsp.watcher_server.Launch;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 6703872492730589499L;
    public Font font = new Font("Hellvetica", Font.BOLD, 11);
    private BufferedImage icon;

    public StatRenderer(BufferedImage icon) {
        this.icon = icon;
        setText("");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(icon != null) {
            if(value != null) {
                setIcon(new ImageIcon(icon));
            }
        }
        setText(value == null ? "" : String.valueOf(value));
        setFont(value == null ? Launch.customFont.deriveFont(13f) : Launch.customFont);
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }
}