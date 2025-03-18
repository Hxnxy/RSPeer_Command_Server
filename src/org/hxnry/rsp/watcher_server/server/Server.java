package org.hxnry.rsp.watcher_server.server;

import org.hxnry.rsp.watcher_server.api.util.Base64;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server implements Runnable {

    public List<ServerClient> clients = new ArrayList<ServerClient>();

    private int port;
    protected DatagramSocket socket;
    protected boolean runnning;

    protected Thread init, manage, send, receive;

    public Server(int port) {
        this.port = port;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        init = new Thread(this, "Server");
        init.start();
    }

    public int getPort() {
        return this.port;
    }

    public void sendToAll(String data) {
        for(int i = 0; i < clients.size(); i++) {
            ServerClient client = clients.get(i);
            send(data.getBytes(), client.ip, client.port);
            //System.out.println("Sent Packet to " + client.name + " @ ip: " + client.ip + " :" + io);
        }
    }

    public void sendToName(String name, String data) {
        for(int i = 0; i < clients.size(); i++) {
            ServerClient client = clients.get(i);
            if(client != null && client.name.equalsIgnoreCase(name)) {
                send(data.getBytes(), client.ip, client.port);
                //System.out.println("Sent Packet to " + name + " @ ip: " + client.ip + " :" + io);
                break;
            }
        }
    }

    public void sendToUid(String uid, String data) {
        for(int i = 0; i < clients.size(); i++) {
            ServerClient client = clients.get(i);
            if(client != null && client.getUID().equalsIgnoreCase(uid)) {
                send(data.getBytes(), client.ip, client.port);
                //System.out.println("Sent Packet to " + name + " @ ip: " + client.ip + " :" + io);
                break;
            }
        }
    }

    @Override
    public void run() {
        runnning = true;
        System.out.println("UDP Server has been created on port: " + port);
        //manageClients();
        receive();
    }

    private void send(final byte[] data, InetAddress address, int port) {
        send = new Thread("Send") {
            @Override
            public void run() {
                byte[] encoded = new byte[0];
                encoded = Base64.encode(data, data.length).getBytes(StandardCharsets.US_ASCII);
                DatagramPacket packet = new DatagramPacket(encoded, encoded.length, address, port);
                try {
                    socket.send(packet);
                    System.out.println("-> " + Base64.decode(new String(encoded)) + " ---> " + new String(encoded));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }


    private void manageClients() {
        manage = new Thread("Manage") {
            @Override
            public void run() {
                while(runnning) {

                }
            }
        };
        manage.start();
    }

    protected void receive() {
        receive = new Thread("Receive") {
            @Override
            public void run() {
                while(runnning) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                        process(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receive.start();
    }

    public void process(DatagramPacket packet) {
        String data = new String(packet.getData());
        UUID uuid = UUID.randomUUID();
        if(data.startsWith("6")) {
            clients.add(new ServerClient(data.substring(1), packet.getAddress(), packet.getPort(), uuid.toString()));
            System.out.println("UUID: " + uuid.toString());
        } else {

        }
    }
}