package org.hxnry.rsp.watcher_server.api.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Hxnry
 * @since July 28, 2018
 */
public class SyncWriter {

    private static final SyncWriter inst = new SyncWriter();

    private SyncWriter() {
        super();
    }

    @SuppressWarnings("unchecked")
    public synchronized String read(String path) {
        try {
            return new BufferedReader(new FileReader(path)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public synchronized String[] readLines(String path) {
        List<String> lines = new ArrayList<>();
        try {
            try (Stream<String> stream = Files.lines(Paths.get(path))) {
                stream.forEach(lines::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);
    }

    public synchronized boolean writeToFile(boolean create, String path, String data) {
        if(!create)
            return false;
        File file = new File(path);
        if(!file.exists()) {
            try {
                file.createNewFile();
                return writeToFile(path, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized boolean appendToFile(String path, String data) {
        File file = new File(path);
        boolean created = false;
        if(!file.exists()) {
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.write(file.toPath(), data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return created;
    }

    @SuppressWarnings("unchecked")
    public synchronized boolean writeToFile(String path, String data) {
        File file = new File(path);
        boolean created = false;
        if(!file.exists()) {
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return created;
    }

    public static SyncWriter getInstance() {
        return inst;
    }

}