package org.hxnry.rsp.watcher_server.server;

import org.hxnry.rsp.watcher_server.Launch;
import org.hxnry.rsp.watcher_server.api.Bet;
import org.hxnry.rsp.watcher_server.api.util.Base64;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.UUID;

public class CommandServer extends Server {

    public CommandServer(int port) {
        super(port);
    }



    @Override
    public void process(DatagramPacket packet) {

        String encryptedData = new String(packet.getData());
        byte[] dirty = encryptedData.getBytes(StandardCharsets.US_ASCII);
        String data = Base64.decode(new String(dirty, StandardCharsets.US_ASCII)).replaceAll("[^\\p{Print}]", "");

        String op = data.substring(0, data.indexOf("/"));
        PacketOpCodes.OpCode opCode = PacketOpCodes.getByOpcodeId(op);
        String raw = data.substring(data.indexOf("/") + 1).trim();
        String[] arguments =  raw.split("~");
        //System.out.println("args: " + Arrays.asList(arguments));
        Iterator<ServerClient> clientIterator = clients.iterator();
        ServerClient current = null;
        String key = arguments[0];
        String rawUser = arguments[0];
        while(clientIterator.hasNext()) {
            ServerClient serverClient = clientIterator.next();
            if(serverClient != null) {
                current = serverClient;
                if(current.UID.length() > 0 && current.getUID().equalsIgnoreCase(rawUser)) {
                    rawUser = current.name;
                }
            }
        }

        if(current != null) {
            rawUser = current.name;
        }

        String user = rawUser;

        String message = arguments[1];

        switch (opCode) {
            case CONNECT:
                UUID uuid = UUID.randomUUID();
                String uid = "=" + uuid.toString() + "=";
                clients.add(new ServerClient(message, packet.getAddress(), packet.getPort(), uid));
                debug(message + " has connected! Given unique key: " + uid);
                sendToName(message, PacketOpCodes.OpCode.CONNECT.getOpCodeOut() + uid);
                sendToName(message, PacketOpCodes.OpCode.SET_UID.getOpCodeOut() + uid);
                break;
            case DISCONNECT:
                if(clients.removeIf(client -> client.UID.equals(key))) {
                    debug(user + " (key:" + key + ") has disconnected.");
                }
                break;
            case PING:
                debug("ping from " + user + ": " + message);
                break;
            case SERVER:
                if(arguments.length < 3) return;
                String bet = arguments[2];
                debug("message from " + user + ": " + message  + " bet:" +  bet);
                break;
            case UPDATE_BET_INFO:
                Launch.scoutViewer.console(data);
                break;
            case FETCH_BET_INFO:
                Bet b = Launch.scoutViewer.getStakerByName(message);
                if(b == null) return;
                sendToUid(key, PacketOpCodes.OpCode.FETCH_BET_INFO.getOpCodeOut() + b.name + "~" + b.toString());
                debug("Sent bet info to " + user + " (key:" + key + ") " + b.name + "( bet: " + b.toString() + ")");
                break;
            case FETCH_ALL_BETS:
                debug("Fetching all bets...");
                Launch.scoutViewer.getAllBets();
                break;
            default:
                Launch.scoutViewer.console(data);
                break;
        }
    }

    public void debug(String debug) {
        System.out.println("[DEBUG] " + debug);
    }
}
