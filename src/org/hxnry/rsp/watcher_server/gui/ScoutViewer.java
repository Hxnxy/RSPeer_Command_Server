package org.hxnry.rsp.watcher_server.gui;

import org.hxnry.rsp.watcher_server.Launch;
import org.hxnry.rsp.watcher_server.api.Bet;
import org.hxnry.rsp.watcher_server.api.util.Clock;
import org.hxnry.rsp.watcher_server.api.util.GoldFormatter;
import org.hxnry.rsp.watcher_server.api.util.ImageDecoder;
import org.hxnry.rsp.watcher_server.gui.encodes.Encodes;
import org.hxnry.rsp.watcher_server.gui.lnf.ClientFrame;
import org.hxnry.rsp.watcher_server.gui.models.Staker;
import org.hxnry.rsp.watcher_server.gui.models.renderers.*;
import org.hxnry.rsp.watcher_server.server.PacketOpCodes;
import org.hxnry.rsp.watcher_server.server.ServerManager;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class ScoutViewer extends ClientFrame {

    public ServerManager serverManager;

    private static final long OFFLINE_TRESHOLD = 120000;
    private static final long UNSEEN_THRESHOLD = 15000;
    private static final long TEN_MINUTE_TIMER = 600000;
    private static final long FIVE_MINUTE_TIMER = 300000;
    private int betBeepAmount = 500000000;

    private JPanel panel;
    private JPanel chatPanel;
    private JPanel slavePanel;
    private JPanel serverPanel;
    private JPanel infoPanel;
    private JPanel beepPanel;
    private JPanel duelPanel;

    private JTable slaveTable;
    private JTable beepTable;
    private JTable duelTable;

    private JTable chatTable;
    private JTable betsTable;
    private JTable interactTable;

    private JScrollPane slaveScrollPane;
    private JScrollPane interactScrollPane;
    private JScrollPane chatScrollPane;
    private JScrollPane beepScrollPane;
    private JScrollPane betsScrollPane;
    private JScrollPane serverScrollPane;
    private JTextArea serverArea;
    private JTextArea infoArea;

    private JPanel chatSettingsPanel;

    public JCheckBox autochatCheckBox;
    public JCheckBox whaleCheckBox;
    private JCheckBox scrollWhalesCheckBox;
    private JCheckBox scrollInteractionsCheckBox;

    private String TITLE = "Watcher Deluxe Command Center";

    private DefaultTableModel slaveTableModel;
    private DefaultTableModel chatTableModel;
    private DefaultTableModel betsTableModel;
    private DefaultTableModel duelsTableModel;
    private DefaultTableModel interactTableModel;
    private DefaultTableModel beepTableModel;

    public Object[] whaleColNames = { "Player", "World", "Location", "Last Seen", "Index", "Beep"};
    public Object[] slaveColNames = { "Slave", "World", "Mode", "Last Seen"};
    public Object[] beepColNames = { "Whale", "Beep", "World", "Location", "Last Seen"};

    public int chatCount, interactCount, serverCount, infoCount;

    public Timer timer = new Timer(5000, (actionEvent -> {
        if(!isVisible()) {
            ((Timer)actionEvent.getSource()).stop();
            return;
        }
        refresh();
        if(chatCount >= 5000) {
            DefaultTableModel model = (DefaultTableModel) chatTable.getModel();
            model.setRowCount(0);
            chatCount = 0;
        }
        if(interactCount >= 2000) {
            DefaultTableModel model = (DefaultTableModel) betsTable.getModel();
            model.setRowCount(0);
            interactCount = 0;
        }
        if(serverCount >= 1000) {
            serverArea.setText("");
            serverCount = 0;
        }
        if(infoCount >= 2000) {
            infoArea.setText("");
            infoCount = 0;
        }
        //removeMaxedMains();
        sortBets();
        sortWhales();
        setOnline();
        if(serverManager != null) {
            serverManager.getServer().sendToAll(PacketOpCodes.OpCode.PING.getOpCodeOut() + "marco");
        }
    }));
    private DefaultTableModel maxedMainTableModel;
    private JTable maxedMainTable;
    private JTable maxedHpTable;
    private DefaultTableModel maxedHpTableModel;
    private DefaultTableModel medsTableModel;
    private JTable medsTable;

    private void removeMaxedMains() {
        for (int i = 0; i < betsTableModel.getRowCount(); i++) {
            Object current = betsTableModel.getValueAt(i, 1);
            if (String.valueOf(current).equalsIgnoreCase("max")) {
                betsTableModel.removeRow(i);
            }
        }
    }


    private void sortBets() {

        for (int i = 0; i < betsTableModel.getRowCount(); i++) {
            for(int j = 0; j < betsTableModel.getColumnCount(); j++) {
                Object bet = betsTableModel.getValueAt(i, j);
                if(bet == null && j == 5) {
                    betsTableModel.setValueAt(new Bet("", "0", "0"), i, j);
                    betsTableModel.fireTableDataChanged();
                }
            }
        }

        for (int r = 0; r  < betsTableModel.getRowCount(); r++) {
            for (int c = 0; c  < betsTableModel.getColumnCount(); c++) {
                if(c == 8) {
                    Object val = betsTableModel.getValueAt(r, c);
                    if(val != null) {
                        if((System.currentTimeMillis() - Long.valueOf(val.toString()) >= FIVE_MINUTE_TIMER)) {
                            betsTableModel.removeRow(r);
                            betsTableModel.fireTableDataChanged();
                        }
                    }
                }
            }
        }


        TableRowSorter<TableModel> sorter = new TableRowSorter<>(betsTable.getModel());
        betsTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        int betsCol = 5;
        sortKeys.add(new RowSorter.SortKey(betsCol, SortOrder.ASCENDING));
        sorter.setComparator(betsCol, (Comparator<Bet>) (o1, o2) -> (int) (o2.getMax() - o1.getMax()));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private void sortWhales() {

        for (int i = 0; i < beepTableModel.getRowCount(); i++) {
            for(int j = 0; j < beepTableModel.getColumnCount(); j++) {
                Object bet = beepTableModel.getValueAt(i, j);
                if(bet == null && j == 5) {
                    beepTableModel.setValueAt(new Bet("", "0", "0"), i, j);
                    beepTableModel.fireTableDataChanged();
                }
            }
        }


        TableRowSorter<TableModel> sorter = new TableRowSorter<>(beepTable.getModel());
        beepTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        int betsCol = 4;
        sortKeys.add(new RowSorter.SortKey(betsCol, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private int removeOnce = 0;

    private void refreshInteract() {
        /**
         SwingUtilities.invokeLater(() -> {
         if(!betsScrollPane.getVerticalScrollBar().getValueIsAdjusting()){
         int extent = betsScrollPane.getVerticalScrollBar().getModel().getExtent();
         int maximum = betsScrollPane.getVerticalScrollBar().getModel().getMaximum();
         if(extent + betsScrollPane.getVerticalScrollBar().getValue() == maximum){
         betsScrollPane.scrollRectToVisible(betsTable.getCellRect(betsTable.getRowCount() - 1, 0, true));
         }
         }
         betsTable.setModel(betsTableModel);
         });
         **/
    }

    private void refreshChat() {
        SwingUtilities.invokeLater(() -> {
            int extent = chatScrollPane.getVerticalScrollBar().getModel().getExtent();
            int maximum = chatScrollPane.getVerticalScrollBar().getModel().getMaximum();
            if(extent + chatScrollPane.getVerticalScrollBar().getValue() == maximum){
                chatScrollPane.scrollRectToVisible(chatTable.getCellRect(chatTable.getRowCount() - 1, 0, true));
                chatTable.scrollRectToVisible(chatTable.getCellRect(chatTable.getRowCount() - 1, 0, true));
            }
            chatTable.setModel(chatTableModel);
        });
    }

    private void setOnline() {

        int online = 0;
        int row = slaveTable.getRowCount();
        int column = slaveTable.getColumnCount();

        for (int r = 0; r  < row; r++) {
            for (int c = 0; c  < column; c++) {
                if(c == 3) {
                    Object val = slaveTableModel.getValueAt(r, c);
                    if(val != null) {
                        if(!Clock.hasExpired(Long.valueOf(val.toString()), UNSEEN_THRESHOLD)) {
                            online++;
                        }
                    }
                }
            }
        }
        int whalesOnline = 0;
        int offline = 0;
        int recentSpotted = 0;
        row = beepTableModel.getRowCount();
        column = beepTableModel.getColumnCount();

        for (int r = 0; r  < row; r++) {
            for (int c = 0; c  < column; c++) {
                if(c == 4) {
                    Object val = beepTableModel.getValueAt(r, c);
                    if(val != null) {
                        if(!Clock.hasExpired(Long.valueOf(val.toString()), UNSEEN_THRESHOLD)) {
                            whalesOnline++;
                        } else if(!Clock.hasExpired(Long.valueOf(val.toString()), OFFLINE_TRESHOLD)) {
                            recentSpotted++;
                        } else {
                            offline++;
                        }
                    }
                }
            }
        }

        setTitle(TITLE + " - " + "Slave Connections [" + online + "]  " + "Whale Info: Online [" + whalesOnline + "] " +  "Recent [" + recentSpotted + "]  " + "Offline [" + offline + "]");


    }

    int sortColumn = 3;

    private String getLastSeen(String timestamp) {
        return Clock.formatTime(Long.parseLong(timestamp)) +  " " + Clock.formatTime(Clock.DATE_FORMAT, Long.parseLong(timestamp));
    }

    private String getStatusLocation(String location, String value) {
        return Clock.hasExpired(Long.valueOf(value), OFFLINE_TRESHOLD) ? "Unknown" : location;
    }

    private Font nameFont = new Font("Hellvetica", Font.BOLD, 12);
    private Font serverFont = new Font("Hellvetica", Font.BOLD, 10);

    int start;
    int end;

    boolean enterBet;

    public void beep(final int hz, final int msecs) throws LineUnavailableException {
        byte[] buf = new byte[msecs * 8];
        for (int i = 0; i < buf.length; i++) {
            double angle = i / (8000.0 / hz) * 2.0 * Math.PI;
            buf[i] = (byte) (Math.sin(angle) * 80.0);
        }
        AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        sdl.write(buf, 0, buf.length);
        sdl.drain();
        sdl.close();
    }

    long lastBeep = System.currentTimeMillis();

    public boolean isMaxedMain(int... args) {
        int sum = Arrays.stream(args).sum();
        return  sum == 396;
    }

    boolean containsUUID(String match) {
        return false;
    }

    public void console(String data) {

        try {

            String op = data.substring(0, data.indexOf("/"));
            PacketOpCodes.OpCode opCode = PacketOpCodes.getByOpcodeId(op);
            String message = data.substring(data.indexOf("/") + 1).trim();

            if(opCode == null) {
                System.err.println("Unhandled OpCode: " + op + " : " + message);
            }


            String[] args = message.split("~");

            if(op.isEmpty()) {
                return;
            }

            String packetDetails = "Packet ID: " + opCode.toString() + " -> " + Arrays.asList(args).toString();

            serverArea.append(packetDetails + "\n\r");
            serverCount++;

            switch (opCode) {
                case FINDER:

                    break;
                case UPDATE_BEEP_INFO:
                    if(beepTableModel.getRowCount() > 0) {
                        for(int i = 0; i < beepTableModel.getRowCount(); i++) {
                            Object o = beepTableModel.getValueAt(i, 0);
                            if(o == null) {
                                return;
                            }
                            Object beepValue = beepTableModel.getValueAt(i, 1);
                            boolean canBeep = beepValue == null || Boolean.parseBoolean(String.valueOf(beepValue));
                            if(!canBeep) continue;
                            Object lastSeen = beepTableModel.getValueAt(i, 4);
                            if(lastSeen == null) {
                                return;
                            }
                            String name = String.valueOf(o);
                            if(name.equalsIgnoreCase(args[1])) {
                                String n = String.valueOf(lastSeen);
                                long seen = Long.valueOf(n);
                                if(seen - lastBeep >= 1500) {
                                    System.out.println("BEEP!");
                                    new Thread(() -> {
                                        try {
                                            beep(500, 500);
                                        } catch (LineUnavailableException e) {
                                            e.printStackTrace();
                                        }
                                    }).start();
                                    lastBeep = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                    updateRow(beepTableModel, args[1].toLowerCase(), new Object[] {
                            args[1], null, args[5] + " - " + args[2], args[3], args[4]
                    });
                    break;
                case HOSTS_ONLINE:
                    Object[] host_args = {args[1], args[2], args[3], args[4]};
                     updateRow(slaveTableModel, args[1], host_args);
                    break;
                case CLOUD_INFORMATION:
                    String cloudTimeStamp = args[0];
                    String cloudSource = args[1];
                    chatCount++;
                    chatTableModel.addRow(new Object[] {cloudTimeStamp, cloudSource});
                    break;
                case INTERACTIONS:
                    break;
                case UPDATE_DUEL:

                    String uuid = String.valueOf(args[1]);

                    DefaultTableModel[] models = {maxedHpTableModel, maxedMainTableModel, medsTableModel};

                    SwingUtilities.invokeLater(() -> {
                        for(DefaultTableModel model : models) {
                            if(model.getRowCount() > 0) {
                                for (int i = 0; i < model.getRowCount(); i++) {
                                    Object current = model.getValueAt(i, 0);
                                    if (String.valueOf(current).equalsIgnoreCase(uuid)) {

                                        int[] sOne = {Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8])};
                                        int[] sTwo = {Integer.parseInt(args[13]), Integer.parseInt(args[14]), Integer.parseInt(args[15]), Integer.parseInt(args[16])};

                                        updateRow(model, args[1], new Object[] {
                                                args[1].toLowerCase(), //player one
                                                args[2], //weapons
                                                args[3], //hp
                                                args[4], //att
                                                isMaxedMain(sOne) ? "max" : args[5], //str
                                                isMaxedMain(sOne) ? "max" : args[6], //def
                                                isMaxedMain(sOne) ? "max" : args[7], //bet,
                                                isMaxedMain(sOne) ? "max" : args[8],
                                                args[9],
                                                args[10],
                                                args[11],
                                                args[12],
                                                isMaxedMain(sTwo) ? "max" : args[13],
                                                isMaxedMain(sTwo) ? "max" : args[14],
                                                isMaxedMain(sTwo) ? "max" : args[15],
                                                isMaxedMain(sTwo) ? "max" : args[16],
                                                args[17],
                                                System.currentTimeMillis()
                                        });
                                        return;
                                    }
                                }
                            }
                        }
                    });


                    break;
                case CREATE_DUEL:

                    int[] sOne = {Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7])};
                    int[] sTwo = {Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[10]), Integer.parseInt(args[11])};

                    DefaultTableModel chosen = null;

                    System.out.println(args[2] + ":" + Arrays.toString(sOne) + "    vs    " + args[3] + ":" + Arrays.toString(sTwo));

                    if((sOne[0] == 99 || sTwo[0] == 99) && !isMaxedMain(sOne) && !isMaxedMain(sTwo)) {
                        chosen = maxedHpTableModel;
                    } else if(isMaxedMain(sOne) || isMaxedMain(sTwo)) {
                        chosen = maxedMainTableModel;
                    } else {
                        chosen = medsTableModel;
                    }


                    updateRowInsert(chosen, args[1], new Object[] {
                            args[1].toLowerCase(),
                            args[2],
                            "...",
                            "100% hp",
                            isMaxedMain(sOne) ? "max" : args[4],
                            isMaxedMain(sOne) ? "max" : args[5],
                            isMaxedMain(sOne) ? "max" : args[6],
                            isMaxedMain(sOne) ? "max" : args[7],
                            "0m",
                            args[3],
                            "...",
                            "100% hp",
                            isMaxedMain(sTwo) ? "max" : args[8],
                            isMaxedMain(sTwo) ? "max" : args[9],
                            isMaxedMain(sTwo) ? "max" : args[10],
                            isMaxedMain(sTwo) ? "max" : args[11],
                            "0m",
                            System.currentTimeMillis(),
                    });
                    break;
                case UPDATE_STATS:

                    boolean isOddsStaker = Boolean.parseBoolean(args[6]);

                    updateRow(betsTableModel, args[1], new Object[] {
                            args[1].toLowerCase(), //name
                            args[2], //hp
                            args[3], //att
                            args[4], //str
                            args[5], //def
                            null, //bet
                            null, //message,
                            isOddsStaker,
                            System.currentTimeMillis()
                    });

                    break;
                case UPDATE_BET_INFO:
                    Staker staker = new Staker(args[1], args[2], args[3], args[4]);
                    //System.out.println("min: " + args[2] + " max: " + args[3] + " (new staker: " + staker.getBet().toString() + ")");
                    if(staker.getBet().getMax() >= betBeepAmount) {
                        if(!containsName(staker.getName(), betsTableModel)) {
                            infoArea.append("[" + Clock.getDate()  + " " + Clock.getTime() + "]" + "  *Beep*  " + staker.getName().toLowerCase() + " is betting " + staker.getBet().toString() + " -> " + staker.getMessage() +  "\n\r");
                            infoCount++;
                            new Thread(() -> {
                                try {
                                    beep(750, 600);
                                } catch (LineUnavailableException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    updateRow(betsTableModel, args[1], new Object[] {
                            staker.getName(),
                            null,
                            null,
                            null,
                            null,
                            staker.getBet(),
                            staker.getMessage(),
                            null,
                            System.currentTimeMillis()
                    });
                    break;
                case UPDATE_MESSAGE:
                    updateRow(betsTableModel, args[1], new Object[] {
                            args[1].toLowerCase(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            args[2],
                            null,
                            System.currentTimeMillis()
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel getTableModeForUUID(String uuid, DefaultTableModel... tableModels) {
        AtomicReference<DefaultTableModel> chosen = null;
        SwingUtilities.invokeLater(() -> {
            for(DefaultTableModel model : tableModels) {
                if(model.getRowCount() > 0) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        Object current = model.getValueAt(i, 0);
                        if (String.valueOf(current).equalsIgnoreCase(uuid)) {
                            chosen.set(model);
                            return;
                        }
                    }
                }
            }
        });
        return chosen.get();
    }

    private void updateRow(DefaultTableModel dtm , String name, Object[] data) {
        SwingUtilities.invokeLater(() -> {
            if(dtm.getRowCount() > 0) {
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    Object current = dtm.getValueAt(i, 0);
                    if (String.valueOf(current).equalsIgnoreCase(name)) {
                        for (int j = 0; j < data.length; j++) {
                            if(data[j] == null) continue;
                            dtm.setValueAt(data[j], i, j);
                        }
                        return;
                    }
                }
                dtm.addRow(data);
                dtm.fireTableDataChanged();
            } else {
                dtm.addRow(data);
                dtm.fireTableDataChanged();
            }
        });
    }

    public boolean containsName(String name, DefaultTableModel dtm) {
        if(dtm.getRowCount() > 0) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                Object current = dtm.getValueAt(i, 0);
                if (String.valueOf(current).equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    };


    private void updateRowInsert(DefaultTableModel dtm , String name, Object[] data) {
        SwingUtilities.invokeLater(() -> {
            if(dtm.getRowCount() > 0) {
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    Object current = dtm.getValueAt(i, 0);
                    if (String.valueOf(current).equalsIgnoreCase(name)) {
                        for (int j = 0; j < data.length; j++) {
                            if(data[j] == null) continue;
                            dtm.setValueAt(data[j], i, j);
                        }
                        return;
                    }
                }
                dtm.insertRow(0, data);
                dtm.fireTableDataChanged();
            } else {
                dtm.insertRow(0, data);
                dtm.fireTableDataChanged();
            }
        });
    }

    private Dimension PREFERED_SIZE = new Dimension(950,  512);
    private Dimension MINIMUM_SIZE = new Dimension(520, 200);

    public ScoutViewer(int port) {

        this.serverManager = new ServerManager(port);

        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new SubstanceGraphiteLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle(TITLE);
        setUndecorated(true);
        setMinimumSize(MINIMUM_SIZE);
        setSize(PREFERED_SIZE);

        autochatCheckBox = new JCheckBox("Show Autochat");
        autochatCheckBox.setFocusPainted(false);
        autochatCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        autochatCheckBox.addActionListener(evt -> {
            serverManager.getServer().sendToAll(PacketOpCodes.OpCode.TOGGLE_AUTOCHAT_CHAT_FILTER.getOpCodeOut() + autochatCheckBox.isSelected());
        });

        whaleCheckBox = new JCheckBox("Show Filtered");
        whaleCheckBox.setFocusPainted(false);
        whaleCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        whaleCheckBox.addActionListener(evt -> {

        });

        scrollWhalesCheckBox = new JCheckBox("Scroll");
        scrollWhalesCheckBox.setSelected(true);
        scrollWhalesCheckBox.setFocusPainted(false);
        scrollWhalesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollInteractionsCheckBox = new JCheckBox("Scroll");
        scrollInteractionsCheckBox.setSelected(true);
        scrollInteractionsCheckBox.setFocusPainted(false);
        scrollInteractionsCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);


        chatSettingsPanel = new JPanel();
        chatSettingsPanel.setLayout(new BoxLayout(chatSettingsPanel, BoxLayout.LINE_AXIS));
        chatSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatSettingsPanel.add(autochatCheckBox);
        chatSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        chatSettingsPanel.add(whaleCheckBox);
        chatSettingsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        chatSettingsPanel.add(scrollWhalesCheckBox);
        chatSettingsPanel.add(Box.createRigidArea(new Dimension(248, 0)));
        chatSettingsPanel.add(scrollInteractionsCheckBox);

        JTabbedPane contentTabbedPane = new JTabbedPane();
        contentTabbedPane.setUI(new JhromeTabbedPaneUI());

        setupDuels();
        setupBets();
        setupBeeper();
        setupCloud();
        setupSlaves();
        setupServerConsole();
        setupInfoPanel();

        JTabbedPane homeTabbedPane = new JTabbedPane();

        homeTabbedPane.addTab("Duels", top);
        homeTabbedPane.addTab("Bets", panel);
        homeTabbedPane.addTab("Beeper", beepPanel);
        homeTabbedPane.addTab("Information", infoPanel);
        homeTabbedPane.addTab("Slaves", slavePanel);
        homeTabbedPane.addTab("Server", serverPanel);
        homeTabbedPane.setUI(new JhromeTabbedPaneUI());

        contentTabbedPane.addTab("Staking", homeTabbedPane);
        contentTabbedPane.addTab("Utensils", new UtensilsPanel());
        add(contentTabbedPane);

        timer.start();
    }

    JSplitPane top;

    private void setupDuels() {

        duelPanel = new JPanel();
        JPanel hpPanel = new JPanel();
        JPanel medPanel = new JPanel();
        JPanel maxedMainPanel = new JPanel();
        duelPanel = new JPanel();
        duelPanel = new JPanel();
        duelPanel.setLayout(new BoxLayout(duelPanel, BoxLayout.PAGE_AXIS));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, medPanel, maxedMainPanel);
        top = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, hpPanel, splitPane);


        JScrollPane maxedMainScrollPane = new JScrollPane(maxedMainTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        maxedMainTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    case 2:
                        return JLabel.class;
                    case 3:
                        return JLabel.class;
                    case 4:
                        return JLabel.class;
                    case 5:
                        return JLabel.class;
                    case 6:
                        return JLabel.class;
                    case 7:
                        return JLabel.class;
                    case 8:
                        return JLabel.class;
                    case 9:
                        return JLabel.class;
                    case 10:
                        return JLabel.class;
                    case 11:
                        return JLabel.class;
                    case 12:
                        return JLabel.class;
                    case 13:
                        return JLabel.class;
                    case 14:
                        return JLabel.class;
                    case 15:
                        return JLabel.class;
                    case 16:
                        return JLabel.class;
                    case 17:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
        maxedMainTableModel.addColumn("ID");
        maxedMainTableModel.addColumn("Player");
        maxedMainTableModel.addColumn("Weapon(s)");
        maxedMainTableModel.addColumn("Health");
        maxedMainTableModel.addColumn("Hp");
        maxedMainTableModel.addColumn("Att");
        maxedMainTableModel.addColumn("Str");
        maxedMainTableModel.addColumn("Def");
        maxedMainTableModel.addColumn("Bet");
        maxedMainTableModel.addColumn("Player");
        maxedMainTableModel.addColumn("Weapon(s)");
        maxedMainTableModel.addColumn("Health");
        maxedMainTableModel.addColumn("Hp");
        maxedMainTableModel.addColumn("Att");
        maxedMainTableModel.addColumn("Str");
        maxedMainTableModel.addColumn("Def");
        maxedMainTableModel.addColumn("Bet");
        maxedMainTableModel.addColumn("When");

        maxedMainTable = new JTable(maxedMainTableModel);
        maxedMainTable.getTableHeader().setFont(Launch.customFont);
        maxedMainTable.addPropertyChangeListener(pce -> {
            if(maxedMainTable.getRowCount() >= 150) {
                DefaultTableModel model = (DefaultTableModel) maxedMainTable.getModel();
                if(model != null)
                    model.removeRow(model.getRowCount() - 1);
            }
        });
        maxedMainTable.getColumnModel().getColumn(0).setPreferredWidth(3);
        maxedMainTable.getColumnModel().getColumn(0).setCellRenderer(new CommandTableCellRenderer());
        maxedMainTable.getColumnModel().getColumn(1).setCellRenderer(new DuelTableCellRenderer());
        maxedMainTable.getColumnModel().getColumn(2).setCellRenderer(new DuelTableCellRenderer());
        maxedMainTable.getColumnModel().getColumn(3).setCellRenderer(new DuelTableCellRenderer());
        maxedMainTable.getColumnModel().getColumn(4).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        maxedMainTable.getColumnModel().getColumn(5).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        maxedMainTable.getColumnModel().getColumn(6).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        maxedMainTable.getColumnModel().getColumn(7).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        maxedMainTable.getColumnModel().getColumn(8).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        maxedMainTable.getColumnModel().getColumn(9).setCellRenderer(new DuelTableCellRenderer());
        maxedMainTable.getColumnModel().getColumn(10).setCellRenderer(new DuelTableCellRenderer());
        maxedMainTable.getColumnModel().getColumn(11).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        maxedMainTable.getColumnModel().getColumn(12).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        maxedMainTable.getColumnModel().getColumn(13).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        maxedMainTable.getColumnModel().getColumn(14).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        maxedMainTable.getColumnModel().getColumn(15).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        maxedMainTable.getColumnModel().getColumn(16).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        maxedMainTable.getColumnModel().getColumn(17).setCellRenderer(timestampRenderer);
        maxedMainScrollPane.add(maxedMainTable);
        maxedMainScrollPane.setViewportView(maxedMainTable);
        maxedMainPanel.setFont(Launch.customFont);
        maxedMainPanel.setLayout(new BorderLayout());
        maxedMainPanel.add(maxedMainScrollPane);
        maxedMainPanel.setBorder(new TitledBorder("Minimum of 1 maxed main"));
        //duelPanel.add(maxedMainPanel);

        JScrollPane maxedHpScrollPane = new JScrollPane(maxedHpTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        maxedHpTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    case 2:
                        return JLabel.class;
                    case 3:
                        return JLabel.class;
                    case 4:
                        return JLabel.class;
                    case 5:
                        return JLabel.class;
                    case 6:
                        return JLabel.class;
                    case 7:
                        return JLabel.class;
                    case 8:
                        return JLabel.class;
                    case 9:
                        return JLabel.class;
                    case 10:
                        return JLabel.class;
                    case 11:
                        return JLabel.class;
                    case 12:
                        return JLabel.class;
                    case 13:
                        return JLabel.class;
                    case 14:
                        return JLabel.class;
                    case 15:
                        return JLabel.class;
                    case 16:
                        return JLabel.class;
                    case 17:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
        maxedHpTableModel.addColumn("ID");
        maxedHpTableModel.addColumn("Player");
        maxedHpTableModel.addColumn("Weapon(s)");
        maxedHpTableModel.addColumn("Health");
        maxedHpTableModel.addColumn("Hp");
        maxedHpTableModel.addColumn("Att");
        maxedHpTableModel.addColumn("Str");
        maxedHpTableModel.addColumn("Def");
        maxedHpTableModel.addColumn("Bet");
        maxedHpTableModel.addColumn("Player");
        maxedHpTableModel.addColumn("Weapon(s)");
        maxedHpTableModel.addColumn("Health");
        maxedHpTableModel.addColumn("Hp");
        maxedHpTableModel.addColumn("Att");
        maxedHpTableModel.addColumn("Str");
        maxedHpTableModel.addColumn("Def");
        maxedHpTableModel.addColumn("Bet");
        maxedHpTableModel.addColumn("When");
        maxedHpTable = new JTable(maxedHpTableModel);
        maxedHpTable.getTableHeader().setFont(Launch.customFont);
        maxedHpTable.addPropertyChangeListener(pce -> {
            if(maxedHpTable.getRowCount() >= 150) {
                DefaultTableModel model = (DefaultTableModel) maxedHpTable.getModel();
                if(model != null)
                    model.removeRow(model.getRowCount() - 1);
            }
        });
        maxedHpTable.getColumnModel().getColumn(0).setPreferredWidth(3);
        maxedHpTable.getColumnModel().getColumn(0).setCellRenderer(new CommandTableCellRenderer());
        maxedHpTable.getColumnModel().getColumn(1).setCellRenderer(new DuelTableCellRenderer());
        maxedHpTable.getColumnModel().getColumn(2).setCellRenderer(new DuelTableCellRenderer());
        maxedHpTable.getColumnModel().getColumn(3).setCellRenderer(new DuelTableCellRenderer());
        maxedHpTable.getColumnModel().getColumn(4).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        maxedHpTable.getColumnModel().getColumn(5).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        maxedHpTable.getColumnModel().getColumn(6).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        maxedHpTable.getColumnModel().getColumn(7).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        maxedHpTable.getColumnModel().getColumn(8).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        maxedHpTable.getColumnModel().getColumn(9).setCellRenderer(new DuelTableCellRenderer());
        maxedHpTable.getColumnModel().getColumn(10).setCellRenderer(new DuelTableCellRenderer());
        maxedHpTable.getColumnModel().getColumn(11).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        maxedHpTable.getColumnModel().getColumn(12).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        maxedHpTable.getColumnModel().getColumn(13).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        maxedHpTable.getColumnModel().getColumn(14).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        maxedHpTable.getColumnModel().getColumn(15).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        maxedHpTable.getColumnModel().getColumn(16).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        maxedHpTable.getColumnModel().getColumn(17).setCellRenderer(timestampRenderer);
        maxedHpTable.getTableHeader().setFont(Launch.customFont);
        maxedHpScrollPane.add(maxedHpTable);
        maxedHpScrollPane.setViewportView(maxedHpTable);
        hpPanel.setLayout(new BorderLayout());
        hpPanel.add(maxedHpScrollPane);
        hpPanel.setFont(Launch.customFont);
        hpPanel.setBorder(new TitledBorder("Non-maxed with 99 HP"));
        //duelPanel.add(hpPanel);

        JScrollPane duelScrollPane = new JScrollPane(medsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        medsTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    case 2:
                        return JLabel.class;
                    case 3:
                        return JLabel.class;
                    case 4:
                        return JLabel.class;
                    case 5:
                        return JLabel.class;
                    case 6:
                        return JLabel.class;
                    case 7:
                        return JLabel.class;
                    case 8:
                        return JLabel.class;
                    case 9:
                        return JLabel.class;
                    case 10:
                        return JLabel.class;
                    case 11:
                        return JLabel.class;
                    case 12:
                        return JLabel.class;
                    case 13:
                        return JLabel.class;
                    case 14:
                        return JLabel.class;
                    case 15:
                        return JLabel.class;
                    case 16:
                        return JLabel.class;
                    case 17:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
        medsTableModel.addColumn("ID");
        medsTableModel.addColumn("Player");
        medsTableModel.addColumn("Weapon(s)");
        medsTableModel.addColumn("Health");
        medsTableModel.addColumn("Hp");
        medsTableModel.addColumn("Att");
        medsTableModel.addColumn("Str");
        medsTableModel.addColumn("Def");
        medsTableModel.addColumn("Bet");
        medsTableModel.addColumn("Player");
        medsTableModel.addColumn("Weapon(s)");
        medsTableModel.addColumn("Health");
        medsTableModel.addColumn("Hp");
        medsTableModel.addColumn("Att");
        medsTableModel.addColumn("Str");
        medsTableModel.addColumn("Def");
        medsTableModel.addColumn("Bet");
        medsTableModel.addColumn("When");
        medsTable = new JTable(medsTableModel);
        medsTable.addPropertyChangeListener(pce -> {
            if(medsTable.getRowCount() > 150) {
                DefaultTableModel model = (DefaultTableModel) medsTable.getModel();
                if(model != null)
                    model.removeRow(model.getRowCount() - 1);
            }
        });
        medsTable.getColumnModel().getColumn(0).setPreferredWidth(3);
        medsTable.getColumnModel().getColumn(0).setCellRenderer(new CommandTableCellRenderer());
        medsTable.getColumnModel().getColumn(1).setCellRenderer(new DuelTableCellRenderer());
        medsTable.getColumnModel().getColumn(2).setCellRenderer(new DuelTableCellRenderer());
        medsTable.getColumnModel().getColumn(3).setCellRenderer(new DuelTableCellRenderer());
        medsTable.getColumnModel().getColumn(4).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        medsTable.getColumnModel().getColumn(5).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        medsTable.getColumnModel().getColumn(6).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        medsTable.getColumnModel().getColumn(7).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        medsTable.getColumnModel().getColumn(8).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        medsTable.getColumnModel().getColumn(9).setCellRenderer(new DuelTableCellRenderer());
        medsTable.getColumnModel().getColumn(10).setCellRenderer(new DuelTableCellRenderer());
        medsTable.getColumnModel().getColumn(11).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        medsTable.getColumnModel().getColumn(12).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        medsTable.getColumnModel().getColumn(13).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        medsTable.getColumnModel().getColumn(14).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        medsTable.getColumnModel().getColumn(15).setCellRenderer(new DuelStatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        medsTable.getColumnModel().getColumn(16).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        medsTable.getColumnModel().getColumn(17).setCellRenderer(timestampRenderer);
        medsTable.getTableHeader().setFont(Launch.customFont);
        duelScrollPane.add(medsTable);
        duelScrollPane.setViewportView(medsTable);
        medPanel.setFont(Launch.customFont);
        medPanel.setLayout(new BorderLayout());
        medPanel.add(duelScrollPane);
        medPanel.setBorder(new TitledBorder("Other (Under 99 hp & not maxed)"));
        //duelPanel.add(medPanel);

    }

    private void setupBeeper() {

        beepPanel = new JPanel();
        beepPanel.setLayout(new BorderLayout());

        beepScrollPane = new JScrollPane(beepTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        beepTable = new JTable(beepTableModel);
        beepTable.getTableHeader().setFont(Launch.customFont);

        beepScrollPane.add(beepTable);
        beepScrollPane.setViewportView(beepTable);

        beepTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {

                final JTable jTable = (JTable) e.getSource();
                final int row = beepTable.getSelectedRow();
                final int column = beepTable.getSelectedColumn();

                if(e.getClickCount() == 2) {
                    if(e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        if (column != 0) return;
                        final String valueInCell = (String) jTable.getValueAt(row, column);
                        int response = JOptionPane.showConfirmDialog(null, valueInCell, "Delete?",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(response == 1) return;
                        for(int i = 0; i < beepTableModel.getRowCount(); i++) {
                            Object valueAt = beepTableModel.getValueAt(i, 0);
                            String currentName = String.valueOf(valueAt);
                            if(currentName.equalsIgnoreCase(String.valueOf(valueInCell))) {
                                beepTableModel.removeRow(i);
                            }
                        }
                    } else {
                        if (column != 1) return;
                        String value = String.valueOf(beepTable.getValueAt(row, column));
                        if(value == null || value.isEmpty()) return;
                        beepTable.setValueAt(value.equalsIgnoreCase("true") ? "false" : "true", row, column);
                    }
                }
            }
        });

        beepTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JCheckBox.class;
                    case 2:
                        return JLabel.class;
                    case 3:
                        return JLabel.class;
                    case 4:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
        beepPanel.add(beepScrollPane, BorderLayout.CENTER);
        Stream.of(beepColNames).forEach(name -> beepTableModel.addColumn(name));
        beepTable.setModel(beepTableModel);
        TableColumnModel slaveTableColumnModel = beepTable.getColumnModel();

        for (int i = 0; i < slaveTableColumnModel.getColumnCount(); i++) {
            if (i < slaveTableColumnModel.getColumnCount()) {
                TableColumn tableColumn = slaveTableColumnModel.getColumn(i);
                switch (i) {
                    case 0:
                        tableColumn.setPreferredWidth(85);
                        break;
                    case 1:
                        tableColumn.setPreferredWidth(45);
                        break;
                    case 2:
                        tableColumn.setPreferredWidth(90);
                        break;
                    case 3:
                        tableColumn.setPreferredWidth(134);
                        break;
                    case 4:
                        tableColumn.setPreferredWidth(134);
                        break;
                }
            } else break;
        }

        beepTable.getColumnModel().getColumn(0).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.whale_icon)));
        beepTable.getColumnModel().getColumn(1).setCellRenderer(beepRenderer);
        beepTable.getColumnModel().getColumn(2).setCellRenderer(commandRenderer);
        beepTable.getColumnModel().getColumn(3).setCellRenderer(commandRenderer);
        beepTable.getColumnModel().getColumn(4).setCellRenderer(whaleTimeRenderer);
    }

    private void setupServerConsole() {
        serverPanel = new JPanel();
        serverPanel.setLayout(new BorderLayout());
        serverScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        serverArea = new JTextArea();
        serverArea.setFont(Launch.customFont);
        serverArea.setFont(serverFont);
        serverScrollPane.add(serverArea);
        serverScrollPane.setViewportView(serverArea);
        serverPanel.add(serverScrollPane, BorderLayout.CENTER);
    }

    private void setupInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        JScrollPane infoScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        infoArea = new JTextArea();
        infoArea.setFont(Launch.customFont);
        infoScrollPane.add(infoArea);
        infoScrollPane.setViewportView(infoArea);
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);
    }

    private void setupCloud() {

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
        JPanel filterPanel = new JPanel();
        JTabbedPane chatTabbedPane = new JTabbedPane();
        setupChat();
        setupInteract();

        chatTabbedPane.addTab("Chat", chatScrollPane);
        chatTabbedPane.addTab("Filter", filterPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.add(chatTabbedPane);
        splitPane.add(interactScrollPane);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(530);
        chatPanel.add(chatSettingsPanel);
        chatPanel.add(splitPane);


    }

    TimestampRenderer timestampRenderer = new TimestampRenderer();
    CommandTableCellRenderer commandRenderer = new CommandTableCellRenderer();
    BeepRenderer beepRenderer = new BeepRenderer();
    WhaleTimeRenderer whaleTimeRenderer = new WhaleTimeRenderer();

    private void setupInteract() {

        interactScrollPane = new JScrollPane(interactTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        TableModelListener interactTableListener = e -> SwingUtilities.invokeLater(() -> {
            if(scrollInteractionsCheckBox.isSelected())
                interactTable.scrollRectToVisible(interactTable.getCellRect(interactTable.getRowCount() - 1, 0, true));
        });

        interactTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    case 2:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }

        };
        interactTableModel.addColumn("Timestamp");
        interactTableModel.addColumn("Interaction");
        interactTableModel.addColumn("Target");


        interactTable = new JTable(interactTableModel);
        interactTable.setAutoscrolls(true);
        interactTable.getModel().addTableModelListener(interactTableListener);
        interactTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        interactTable.getColumnModel().getColumn(0).setCellRenderer(timestampRenderer);
        interactTable.getColumnModel().getColumn(1).setCellRenderer(commandRenderer);
        interactTable.getColumnModel().getColumn(2).setCellRenderer(commandRenderer);

        interactScrollPane.add(interactTable);
        interactScrollPane.setAutoscrolls(true);
        interactScrollPane.setViewportView(interactTable);
    }

    public Bet getStakerByName(String player) {
        for(int row = 0;row < betsTableModel.getRowCount();row++) {
            for(int col = 0;col < betsTableModel.getColumnCount();col++) {
                Object name = betsTableModel.getValueAt(row, 0);
                Bet bet = (Bet) betsTableModel.getValueAt(row, 5);
                if(player.equalsIgnoreCase(String.valueOf(name))) {
                    return bet;
                }
            }

        }
        System.out.println("couldn't fetch bet for " + player);
        return null;
    }

    public CommandTableCellRenderer commandTableCellRenderer = new CommandTableCellRenderer();

    private void setupBets() {

        panel = new JPanel(new BorderLayout());

        betsScrollPane = new JScrollPane(betsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        betsTableModel = getBetsTableModel();
        betsTableModel.addColumn("Name");
        betsTableModel.addColumn("HP");
        betsTableModel.addColumn("Attack");
        betsTableModel.addColumn("Strength");
        betsTableModel.addColumn("Defence");
        betsTableModel.addColumn("Bet");
        betsTableModel.addColumn("Message");
        betsTableModel.addColumn("< 750 Total");
        betsTableModel.addColumn("When");
        betsTable = new JTable(betsTableModel){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                String message = (String) getValueAt(row, 6);
                if(message == null) return c;
                if(message.length() > 0) {
                    if(!message.contains("[autochat]")) {
                        if(message.startsWith("Stake: ")) {
                            c.setForeground(new Color(74, 188, 63));
                        } else {
                            if(col > 0 && col != 7) {
                                c.setForeground(new Color(248, 245, 137));
                            }
                        }
                    }  else if(col > 0 && col != 7) {
                        c.setForeground(Color.LIGHT_GRAY);
                    }
                }
                return c;
            }
        };
        betsTable.getTableHeader().setFont(Launch.customFont);
        betsTable.setAutoscrolls(true);
        betsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        betsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final JTable jTable = (JTable) e.getSource();
                final int row = jTable.getSelectedRow();
                final int column = jTable.getSelectedColumn();
                if (e.getClickCount() == 1) {
                    if (column != 0) return;
                    final String valueInCell = (String) jTable.getValueAt(row, column);
                    StringSelection stringSelection = new StringSelection(valueInCell);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                } else if(e.getClickCount() == 2) {
                    if(e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        if (column != 0) return;
                        final String valueInCell = (String) jTable.getValueAt(row, column);
                        int response = JOptionPane.showConfirmDialog(null, valueInCell, "Delete?",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(response == 1) return;
                        for(int i = 0; i < betsTableModel.getRowCount(); i++) {
                            Object valueAt = betsTableModel.getValueAt(i, 0);
                            String currentName = String.valueOf(valueAt);
                            if(currentName.equalsIgnoreCase(String.valueOf(valueInCell))) {
                                betsTableModel.removeRow(i);
                            }
                        }
                    } else {
                        if (column != 0) return;
                        final String valueInCell = (String) jTable.getValueAt(row, column);
                        int response = JOptionPane.showConfirmDialog(null, "Send " + valueInCell + " ignore?", "Confirm",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(response == 1) return;
                        serverManager.getServer().sendToAll(PacketOpCodes.OpCode.IGNORE_UPDATE_GUI.getOpCodeOut() + valueInCell);
                    }
                }
            }
        });

        TableColumnModel betsColumnModel = betsTable.getColumnModel();
        for (int i = 0; i < betsColumnModel.getColumnCount(); i++) {
            if (i < betsColumnModel.getColumnCount()) {
                TableColumn tableColumn = betsColumnModel.getColumn(i);
                switch (i) {
                    case 0:
                        tableColumn.setMinWidth(100);
                        tableColumn.setMaxWidth(100);
                        tableColumn.setWidth(70);
                        break;
                    case 1:
                        tableColumn.setMaxWidth(60);
                        tableColumn.setWidth(20);
                        break;
                    case 2:
                        tableColumn.setMaxWidth(60);
                        tableColumn.setWidth(20);
                        break;
                    case 3:
                        tableColumn.setMaxWidth(60);
                        tableColumn.setWidth(25);
                        break;
                    case 4:
                        tableColumn.setMaxWidth(60);
                        tableColumn.setWidth(25);
                        break;
                    case 5:
                        tableColumn.setMaxWidth(100);
                        tableColumn.setMinWidth(100);
                        tableColumn.setWidth(100);
                        break;
                    case 6:
                        tableColumn.setMinWidth(310);
                        tableColumn.setWidth(310);
                        break;
                }
            } else break;
        }

        betsTable.getColumnModel().getColumn(0).setCellRenderer(new BetsNameRenderer());
        betsTable.getColumnModel().getColumn(1).setCellRenderer(new StatRenderer(ImageDecoder.decodeToImage(Encodes.hp_icon)));
        betsTable.getColumnModel().getColumn(2).setCellRenderer(new StatRenderer(ImageDecoder.decodeToImage(Encodes.att_icon)));
        betsTable.getColumnModel().getColumn(3).setCellRenderer(new StatRenderer(ImageDecoder.decodeToImage(Encodes.str_icon)));
        betsTable.getColumnModel().getColumn(4).setCellRenderer(new StatRenderer(ImageDecoder.decodeToImage(Encodes.def_icon)));
        betsTable.getColumnModel().getColumn(5).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.bet_icon)));
        betsTable.getColumnModel().getColumn(6).setCellRenderer(new IconRenderer(ImageDecoder.decodeToImage(Encodes.message_icon)) {

            @Override
            public Font getFont() {
                return Launch.customFont.deriveFont(13f);
            }

            @Override
            public int getAlignment() {
                return JLabel.LEFT;
            }
        });
        betsTable.getColumnModel().getColumn(7).setCellRenderer(new BetsNameRenderer());
        betsTable.getColumnModel().getColumn(8).setCellRenderer(new TimestampRenderer());

        betsScrollPane.add(betsTable);
        betsScrollPane.setAutoscrolls(true);
        betsScrollPane.setViewportView(betsTable);

        JLabel betBeepInfoLabel = new JLabel();
        betBeepInfoLabel.setText("Beep >=");
        JLabel betBeepLabel = new JLabel();
        betBeepLabel.setText("500m");
        betBeepLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Object input = JOptionPane.showInputDialog("");
                if(input == null || String.valueOf(input).isEmpty()) return;
                int amount = Integer.parseInt(String.valueOf(input).replaceAll("[^0-9]", "")) * 1000000;
                if(amount == 0) return;
                String thresh = GoldFormatter.gpFormatter(amount);
                System.out.println("Now beeping for bets over " + thresh.toLowerCase());
                betBeepLabel.setText(thresh.toLowerCase());
                betBeepAmount = amount;
            }
        });

        JButton reset = new JButton("reset");
        reset.setFocusPainted(false);
        reset.addActionListener(al -> {
            JDialog.setDefaultLookAndFeelDecorated(true);
            int response = JOptionPane.showConfirmDialog(null, "Empty the table?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(response == 1) return;
            DefaultTableModel tMOdel = (DefaultTableModel) betsTable.getModel();
            tMOdel.setRowCount(0);
        });
        reset.setMaximumSize(new Dimension(50, 35));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(betBeepInfoLabel);
        buttonPane.add(buttonPane.add(Box.createRigidArea(new Dimension(5, 0))));
        buttonPane.add(betBeepLabel);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(reset);

        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(boxlayout);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPane);
        panel.add(betsScrollPane);
    }

    private DefaultTableModel getDuelsTableModel() {
        return new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
    }

    private DefaultTableModel getBetsTableModel() {

        return new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    case 2:
                        return JLabel.class;
                    case 3:
                        return JLabel.class;
                    case 4:
                        return JLabel.class;
                    case 5:
                        return Bet.class;
                    case 6:
                        return JLabel.class;
                    case 7:
                        return JLabel.class;
                    case 8:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
    }

    private void setupChat() {

        chatScrollPane = new JScrollPane(chatTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        TableModelListener chatTableListener = e -> SwingUtilities.invokeLater(() -> {
            if(scrollWhalesCheckBox.isSelected())
                chatTable.scrollRectToVisible(chatTable.getCellRect(chatTable.getRowCount() - 1, 0, true));
        });

        chatTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
        chatTableModel.addColumn("Timestamp");
        chatTableModel.addColumn("Message");

        chatTable = new JTable(chatTableModel);
        chatTable.getModel().addTableModelListener(chatTableListener);
        chatTable.getColumnModel().getColumn(0).setMaxWidth(75);

        chatScrollPane.add(chatTable);
        chatScrollPane.setViewportView(chatTable);
    }

    private void setupSlaves() {

        slavePanel = new JPanel();

        slavePanel.setLayout(new BorderLayout());

        slaveScrollPane = new JScrollPane(slaveTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        slaveTable = new JTable(slaveTableModel);
        slaveTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {

                SwingUtilities.invokeLater(() -> {


                    int row = slaveTable.rowAtPoint(evt.getPoint());
                    int col = slaveTable.columnAtPoint(evt.getPoint());

                    if(evt.getClickCount() == 2) {

                        if (evt.getButton() == 0) {
                            if (row >= 0 && col >= 0) {
                                if (col == 2) {
                                    String inputValue = JOptionPane.showInputDialog("Enter a location");
                                    Object name = slaveTableModel.getValueAt(row, 0);
                                    serverManager.getServer().sendToName(name.toString().trim(), PacketOpCodes.OpCode.CLIENT_LOCATION_MESSAGE.getOpCodeOut() + inputValue);
                                }
                            }
                        }
                    }
                });
            }
        });

        slaveScrollPane.add(slaveTable);
        slaveScrollPane.setViewportView(slaveTable);
        slaveTableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    case 2:
                        return JLabel.class;
                    case 3:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
        slavePanel.add(slaveScrollPane, BorderLayout.CENTER);
        Stream.of(slaveColNames).forEach(name -> slaveTableModel.addColumn(name));
        slaveTable.setModel(slaveTableModel);
        TableColumnModel slaveTableColumnModel = slaveTable.getColumnModel();

        for (int i = 0; i < slaveTableColumnModel.getColumnCount(); i++) {
            if (i < slaveTableColumnModel.getColumnCount()) {
                TableColumn tableColumn = slaveTableColumnModel.getColumn(i);
                switch (i) {
                    case 0:
                        tableColumn.setPreferredWidth(85);
                        break;
                    case 1:
                        tableColumn.setPreferredWidth(45);
                        break;
                    case 2:
                        tableColumn.setPreferredWidth(90);
                        break;
                    case 3:
                        tableColumn.setPreferredWidth(134);
                        break;
                }
            } else break;
        }
        slaveTable.getTableHeader().setFont(Launch.customFont);
        slaveTable.getColumnModel().getColumn(3).setCellRenderer(timestampRenderer);
    }

    public void refresh() {

    }

    public void getAllBets() {
        SwingUtilities.invokeLater(() -> {
            for(int row = 0;row < betsTableModel.getRowCount();row++) {
                for(int col = 0;col < betsTableModel.getColumnCount();col++) {
                    Object name = betsTable.getValueAt(row, 0);
                    Object bet = betsTable.getValueAt(row, 1);
                    System.out.println(String.valueOf(name) + "( bet: " + String.valueOf(bet) + ")");
                }

            }
        });
    }
}
