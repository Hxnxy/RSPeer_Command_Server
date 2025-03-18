package org.hxnry.rsp.watcher_server.server;

public class ServerManager {

    private int port;
    private CommandServer server;

    public ServerManager(int port) {
        this.port = port;
        server = new CommandServer(port);
    }

    public CommandServer getServer() {
        return this.server;
    }
}
