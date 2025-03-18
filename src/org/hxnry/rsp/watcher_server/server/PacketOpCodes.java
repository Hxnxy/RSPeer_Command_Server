package org.hxnry.rsp.watcher_server.server;

import java.util.Arrays;

public class PacketOpCodes {

    public enum OpCode {

        FINDER("bets"),
        CLOUD_INFORMATION("cloud_information"),
        HOSTS_ONLINE("host_online"),
        SERVER("server"),
        INTERACTIONS("interactions"),
        CONNECT("connect"),
        DISCONNECT("disconnect"),
        SERVER_MESSAGE("server_message"),
        CLIENT_LOCATION_MESSAGE("client_location_message"),
        PING("ping"),
        TOGGLE_AUTOCHAT_CHAT_FILTER("toggle_autochat_chat_filter"),
        SET_UID("set_uid"),
        UPDATE_BET_INFO("update_bet_info"),
        FETCH_BET_INFO("fetch_bet_info"),
        FETCH_ALL_BETS("fetch_all_bets"),
        UPDATE_STATS("update_stats"),
        IGNORE_UPDATE_GUI("ignore_update_gui"),
        UPDATE_MESSAGE("update_message"),
        UPDATE_BEEP_INFO("update_beep_info"),
        UPDATE_DUEL("update_duel"),
        CREATE_DUEL("create_duel");

        String opCode;

        OpCode(String opcode) {
            this.opCode = opcode;
        }

        public String getOpCode() {
            return this.opCode;
        }

        public String getOpCodeOut() {
            return this.opCode + "/";
        }
    }


    public static OpCode getByOpcodeId(final String opCode) {
        return Arrays.stream(OpCode.values()).filter(code -> code.opCode.equalsIgnoreCase(opCode)).findFirst().orElse(null);
    }
}
