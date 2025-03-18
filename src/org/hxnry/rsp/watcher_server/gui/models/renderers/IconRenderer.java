package org.hxnry.rsp.watcher_server.gui.models.renderers;

import org.hxnry.rsp.watcher_server.Launch;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconRenderer  extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 6703872492730589499L;

    private BufferedImage icon;

    public IconRenderer(BufferedImage icon) {
        this.icon = icon;
        this.setText("unknown");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(icon != null)
            setIcon(new ImageIcon(icon));
        setText(value == null ? "unknown" : String.valueOf(value));
        setFont(getFont());
        setHorizontalAlignment(getAlignment());
        return this;
    }

    public Font getFont() {
        return Launch.customFont;
    }

    public int getAlignment() {
        return JLabel.LEFT;
    }
}