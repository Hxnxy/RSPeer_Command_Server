package org.hxnry.rsp.watcher_server.gui.models.renderers;

import javax.swing.*;
import java.awt.*;

public class BeepRenderer extends CommandTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(getText() == null || getText().isEmpty()) {
            setText("true");
        }
        return cellComponent;
    }
}
