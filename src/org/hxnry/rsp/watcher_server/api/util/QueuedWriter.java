package org.hxnry.rsp.watcher_server.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.h
 * User: Purple
 * Date: 11/25/2015
 * Time: 7:48 AM
 */
public class QueuedWriter {

    public static final String PATH = "C:\\pie.dreambot";
    public static final String MATCHES_FILE_PATH = "\\matches2.txt";
    public static String next = "";
    public static final String GLOBAL_TIMESTAMPS = "\\whales\\whales.txt";
    public static final String GLOBAL_DIRS = PATH + "\\whales";
    private final Queue<String[]> shitToSubmit = new LinkedBlockingQueue<>();


    public void addToQueue(String path, String outcome) {
        shitToSubmit.offer(new String[] {path, System.lineSeparator() + "[" + Clock.getDate()  + " " + Clock.getTime() + "] " + outcome});
    }

    private boolean makeDir(File check) {
        try {
            return check.exists() || check.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void processQueue() {
        //System.out.println("----writer---");
        final String[] toWrite = shitToSubmit.poll();
        if(toWrite != null) {
            try {
                File file = new File(PATH + toWrite[0]);
                new File(GLOBAL_DIRS).mkdirs();
                makeDir(file);
                Files.write(file.toPath(), toWrite[1].getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
