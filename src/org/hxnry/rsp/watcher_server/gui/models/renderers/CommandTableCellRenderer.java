package org.hxnry.rsp.watcher_server.gui.models.renderers;

import org.hxnry.rsp.watcher_server.Launch;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CommandTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 6703872492730589499L;

    public Font font = new Font("Hellvetica", Font.BOLD, 10);

    public Component getTableCellRendererComponent(JTable whaleTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(whaleTable, value, isSelected, hasFocus, row, column);
        setFont(Launch.customFont);
        setHorizontalAlignment(getAlignment());
        return cellComponent;
    }

    public int getAlignment() {
        return JLabel.CENTER;
    }
}