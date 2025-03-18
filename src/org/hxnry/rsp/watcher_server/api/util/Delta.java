package org.hxnry.rsp.watcher_server.api.util;

/**
 * @author Hxnry
 * @since November 08, 2016
 */
public class Delta {

    private long last_time = System.nanoTime();

    public long getDelta() {
        long time = System.nanoTime();
        int delta_time = (int) ((time - last_time) / 1000000);
        last_time = time;
        return delta_time;
    }
}
