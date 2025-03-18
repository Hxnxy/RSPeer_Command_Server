package org.hxnry.rsp.watcher_server.gui;

import org.hxnry.rsp.watcher_server.Launch;
import org.hxnry.rsp.watcher_server.gui.utensils.Lowercaser;

import javax.swing.*;
import java.awt.*;

public class UtensilsPanel extends JPanel {

    JButton lowercaseUtensilButton;
    JPanel contentPane;

    public UtensilsPanel() {

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        setupLowercaseButton();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(contentPane);
    }

    private void setupLowercaseButton() {
        lowercaseUtensilButton = new JButton();
        lowercaseUtensilButton.setFont(Launch.customFont);
        lowercaseUtensilButton.setText("Lowercaser");
        lowercaseUtensilButton.setSize(new Dimension(50, 50));
        lowercaseUtensilButton.setFocusPainted(false);
        lowercaseUtensilButton.addActionListener(actionEvent -> {
            Lowercaser lowercaser = new Lowercaser();
            lowercaser.setVisible(true);
        });
        contentPane.add(Box.createRigidArea(new Dimension(2, 0)));
        contentPane.add(lowercaseUtensilButton);
    }

}
