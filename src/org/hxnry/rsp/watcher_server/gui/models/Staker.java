package org.hxnry.rsp.watcher_server.gui.models;

import org.hxnry.rsp.watcher_server.api.Bet;

import java.util.Date;

public class Staker {

    public String message = "unknown";
    public String name;
    public Bet bet;

    public Staker(String name, String minBet, String maxBet, String message) {
        this.name = name.toLowerCase();
        this.bet = new Bet(name, minBet, maxBet);
        this.message = message;
    }

    public String getName() {
        return this.name;
    }

    public Bet getBet() {
        return this.bet;
    }

    public Date getDate() {
        return new Date(System.currentTimeMillis());
    }

    public String getMessage() {
        return message;
    }
}
