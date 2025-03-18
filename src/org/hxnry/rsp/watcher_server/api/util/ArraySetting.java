package org.hxnry.rsp.watcher_server.api.util;


import org.json.JSONArray;

import java.util.stream.Stream;

/**
 * @author Hxnry
 * @since July 20, 2018
 */
public class ArraySetting extends JSONArray {

    public String value;
    public String[] entries;

    @SuppressWarnings("unchecked")
    public ArraySetting(String value, String... entries) {
        this.value = value;
        this.entries = entries;
        Stream.of(entries).forEach(this::put);
    }

    public String getValue() {
        return this.value;
    }

    public String[] getEntries() {
        return entries;
    }



}
