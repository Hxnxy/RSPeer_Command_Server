package org.hxnry.rsp.watcher_server.server;

import org.hxnry.rsp.watcher_server.gui.lnf.ClientFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class Client extends ClientFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JTextField inputField;

    private String name;
    private String address;
    private int port;
    private JTextArea consoleArea;
    private DefaultCaret caret;

    private DatagramSocket socket;
    private InetAddress ip;

    private Thread send, receive;

    boolean running;

    public Client(String name, String address, int port) {

        this.name = name;
        this.address = address;
        this.port = port;

        boolean connect = openConnection(address);

        if(!connect) {
            System.err.println("Connection failed!");
            console("Connection has failed.");
            return;
        }
        createWindow();

        String connection = name + " connected from " + address + " on port number " + port;

        send(connection.getBytes());
    }

    private boolean openConnection(String address) {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void receive() {
        receive = new Thread("Receive") {
            @Override
            public void run() {
                while(running) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receive.start();
    }

    public void send(final byte[] data) {
        send = new Thread("Send") {
            @Override
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    private void console(String message) {
        console(message, false);
    }

    private void console(String message, boolean network) {
        if(network) {
            send((name + ": " + message).getBytes());
        }
        consoleArea.append(message + "\n\r");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    private void createWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUndecorated(false);
        setTitle("Command Center - UDP Client");
        setSize(850, 550);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);


        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{13, 824, 13};
        gbl_contentPane.rowHeights = new int[]{25, 480, 45};
        gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0};
        gbl_contentPane.rowWeights = new double[]{1.0, 1.0, 1.0};
        contentPane.setLayout(gbl_contentPane);

        consoleArea = new JTextArea();
        consoleArea.setFont(new Font("Helvetica", Font.BOLD, 10));
        consoleArea.setEditable(false);
        caret = (DefaultCaret) consoleArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(consoleArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        GridBagConstraints scollConstraints = new GridBagConstraints();
        scollConstraints.fill = GridBagConstraints.BOTH;
        scollConstraints.gridx = 1;
        scollConstraints.gridy = 1;
        scollConstraints.insets = new Insets(2, 2, 2, 2);
        contentPane.add(scrollPane, scollConstraints);

        inputField = new JTextField();
        inputField.setFont(new Font("Helvetica", Font.BOLD, 10));
        inputField.addActionListener(evt -> {
            console(inputField.getText(), true);
            inputField.setText("");
        });
        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.fill = GridBagConstraints.BOTH;
        inputConstraints.gridx = 1;
        inputConstraints.gridy = 2;
        contentPane.add(inputField, inputConstraints);

        setVisible(true);
        running = true;
        receive();
    }
}
