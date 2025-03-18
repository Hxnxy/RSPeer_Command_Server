package org.hxnry.rsp.watcher_server.api.util;

/**
 * @author Hxnry
 * @since November 08, 2016
 */
public enum EssenceType {
    PURE(7936),
    NORMAL(1436);
    final int ID;

    private EssenceType(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return this.ID;
    }
}
