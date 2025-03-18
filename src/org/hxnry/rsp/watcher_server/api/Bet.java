package org.hxnry.rsp.watcher_server.api;

import org.hxnry.rsp.watcher_server.api.util.GoldFormatter;

public class Bet {


    public String name = "unknown";
    String minBet = "0";
    String maxBet = "0";
    double min = 0;
    double max = 0;

    public Bet(String name, String minBet, String maxBet) {
        this.name = name;
        this.minBet = minBet.replaceAll("-", "").replaceAll(",", ".");
        this.maxBet = maxBet.replaceAll("-", "").replaceAll(",", ".");
        process();
    }


    public void process() {
        //System.out.println("processing " + name);
        if(this.minBet.contains("m")) {
            String stripped = minBet.replaceAll("[^0-9]", "");
            this.min = Double.parseDouble(stripped) * 1000000;
            //System.out.println("min (m):" + this.min);
        } else if(this.minBet.contains("b")) {
            String stripped = minBet.replaceAll("[^0-9]", "");
            if(stripped.contains(".") ? Double.parseDouble(stripped) > 2 : Integer.parseInt(stripped) > 2) {
                this.min = 2147483647;
                //System.out.println("min (maxed + b):" + this.min);
            } else {
                this.min = Double.parseDouble(stripped) * 1000000000;
                //System.out.println("min (b):" + this.min);
            }
        } else {
            this.min = Integer.parseInt(minBet) * 1000000;
            //System.out.println("min (else):" + this.min);
        }

        if(this.maxBet.contains("m")) {
            String stripped = maxBet.replaceAll("[^0-9]", "");
            this.max = Double.parseDouble(stripped) * 1000000;
            //System.out.println("max (m):" + this.max);
        } else if(this.maxBet.contains("b")) {
            String stripped = maxBet.replaceAll("[^0-9]", "");
            if(stripped.contains(".") ? Double.parseDouble(stripped) > 2 : Double.parseDouble(stripped) > 2) {
                this.max = 2147483647;
                //System.out.println("max (maxed b):" + this.max);
            } else {
                this.max = Double.parseDouble(stripped) * 1000000000;
                //System.out.println("max (b):" + this.max);
            }
        } else {
            this.max = Double.parseDouble(maxBet) * 1000000;
            //System.out.println("max (else):" + this.max);
        }

        //System.out.println(System.lineSeparator());
    }

    @Override
    public String toString() {
        return min == max ? GoldFormatter.gpFormatter(min).toLowerCase() : GoldFormatter.gpFormatter(min).toLowerCase() + " - " + GoldFormatter.gpFormatter(max).toLowerCase();
    }

    public double getMax() {
        return this.max;
    }

    public double getMin() {
        return this.min;
    }
}
