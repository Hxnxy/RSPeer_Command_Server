package org.hxnry.rsp.watcher_server.gui.models.renderers;

import javax.swing.*;
import java.awt.*;

public class DuelTableCellRenderer extends CommandTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Object playerOneHp = table.getValueAt(row, 3);
        Object playerTwoHp = table.getValueAt(row, 11);

        int oneHp = Integer.parseInt(String.valueOf(playerOneHp).replaceAll("[^0-9]", ""));
        int twoHp = Integer.parseInt(String.valueOf(playerTwoHp).replaceAll("[^0-9]", ""));

        if(column >= 1 && column <= 8) {
            if(oneHp > 0 && twoHp > 0) {
                setForeground(new Color(248, 245, 137));
            } else if(twoHp == 0) {
                setForeground(new Color(62, 164, 60));
            } else {
                if(column == 3) setText("DEAD");
                setForeground(new Color(221, 117, 109));
            }
        } else {
            if(twoHp > 0 && oneHp > 0) {
                setForeground(new Color(248, 245, 137));
            } else if(oneHp == 0) {
                setForeground(new Color(62, 164, 60));
            } else {
                if(column == 11) setText("DEAD");
                setForeground(new Color(221, 117, 109));
            }
        }
        return this;
    }
}
