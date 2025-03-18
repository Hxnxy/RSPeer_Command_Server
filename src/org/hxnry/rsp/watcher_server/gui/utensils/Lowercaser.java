package org.hxnry.rsp.watcher_server.gui.utensils;

import org.hxnry.rsp.watcher_server.Launch;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Lowercaser extends JFrame {

    private JPanel contentPanel = new JPanel();
    private JTextField input = new JTextField();
    private JTextField output = new JTextField();
    private JPanel bottomPanel = new JPanel();
    private JButton copyButton = new JButton();

    public Lowercaser() {

        setTitle("Lowercaser");
        setLocationRelativeTo(null);
        setSize(new Dimension(300, 150));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        input.setFont(Launch.customFont.deriveFont(22f));

        output.setFont(Launch.customFont.deriveFont(22f));
        output.setEnabled(false);

        input.addActionListener(actionEvent -> {
            output.setText(input.getText().toLowerCase());
        });

        copyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                StringSelection stringSelection = new StringSelection(output.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(input);
        contentPanel.add(output);


        bottomPanel.setLayout(new BorderLayout());
        copyButton.setFocusPainted(false);
        copyButton.setText("copy output to clipboard");
        copyButton.setFont(Launch.customFont);
        bottomPanel.add(copyButton);
        contentPanel.add(bottomPanel);


        add(contentPanel);
    }
}
