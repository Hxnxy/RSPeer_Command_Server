package org.hxnry.rsp.watcher_server.gui.models.renderers;

import org.hxnry.rsp.watcher_server.api.util.Clock;

import javax.swing.*;
import java.awt.*;

public class WhaleTimeRenderer extends TimestampRenderer {

    private final long OFFLINE_TRESHOLD = 60000;
    private final long UNSEEN_THRESHOLD = 12000;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(Clock.hasExpired(Long.valueOf(value.toString()), OFFLINE_TRESHOLD)) {
            setForeground(new Color(196, 196, 196));
        } else if(Clock.hasExpired(Long.valueOf(value.toString()), UNSEEN_THRESHOLD)) {
            setForeground(new Color(224, 185, 51));
        } else {
            setForeground(new Color(61, 188, 51));
        }

        return cellComponent;
    }
}
