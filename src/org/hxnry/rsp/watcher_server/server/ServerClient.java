package org.hxnry.rsp.watcher_server.server;

import java.net.InetAddress;

public class ServerClient {

    public String name;
    public InetAddress ip;
    public int port;
    public final String UID;
    public int attemmpt = 0;

    public ServerClient(String name, InetAddress ip, int port, final String UID) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

}
