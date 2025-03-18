package org.hxnry.rsp.watcher_server.gui.models.renderers;

import javax.swing.*;
import java.awt.*;

public class BetsNameRenderer extends CommandTableCellRenderer {

        public Component getTableCellRendererComponent(JTable whaleTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(whaleTable, value, isSelected, hasFocus, row, column);
            if(value == null) {
                if(column == 7) {
                    setText("no stats");
                }
            }
            String isOddsValue = String.valueOf(whaleTable.getValueAt(row, 7));
            boolean isOdds = (isOddsValue != null) && Boolean.parseBoolean(isOddsValue);
            if(value == null || isOddsValue == null|| isOdds || whaleTable.getValueAt(row, 7) == null || isOddsValue.equalsIgnoreCase("no stats")) {
                cellComponent.setForeground(Color.LIGHT_GRAY);
            } else {
                cellComponent.setForeground(new Color(127, 173, 208));
            }
            return cellComponent;
        }
}






