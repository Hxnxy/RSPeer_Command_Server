package org.hxnry.rsp.watcher_server.api.graphics;

import org.hxnry.rsp.watcher_server.api.script.Task;
import org.hxnry.rsp.watcher_server.api.util.Delta;
import org.hxnry.rsp.watcher_server.api.util.Stopwatch;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * @author Hxnry
 * @since November 19, 2016
 */
public abstract class Painter<T extends Task> implements Paintable, MouseMotionListener {

    protected T module;
    protected Stopwatch stopwatch = new Stopwatch();
    protected String debug = "";
    protected PaintWindow paintWindow = new PaintWindow(0, new Text("..."));
    protected Delta delta = new Delta();

    public Painter(T module) {
        this.module = module;
    }

    public String getDebug() {
        return this.debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public void setBackGround(Color color) {
        this.paintWindow.setBackground(color);
    }

    public static long timeTnl(int xpTnl, int xpPh) {
        if (xpPh > 0) {
            long timeTNL = (long) ((double) xpTnl / (double) xpPh * 3600000);
            return timeTNL;
        }
        return 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        paintWindow.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        paintWindow.mouseMoved(e);
    }

    public PaintWindow getPaintWindow() {
        return paintWindow;
    }
}
