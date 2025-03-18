package org.hxnry.rsp.watcher_server.gui.models.renderers;

import org.hxnry.rsp.watcher_server.api.util.Clock;

import javax.swing.*;
import java.awt.*;

public class TimestampRenderer extends CommandTableCellRenderer {

    private static final long serialVersionUID = 6703872492730589499L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if(value == null) {
            return cellComponent;
        }

        setText(Clock.formatTime(Long.parseLong(String.valueOf(value))));
        setToolTipText(Clock.formatTime(Clock.DATE_FORMAT, Long.parseLong(String.valueOf(value))));
        return cellComponent;
    }
}