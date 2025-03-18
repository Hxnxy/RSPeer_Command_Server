package org.hxnry.rsp.watcher_server.api.script;

import org.hxnry.rsp.watcher_server.api.graphics.Painter;
import org.hxnry.rsp.watcher_server.api.util.F;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * @author Hxnry
 * @since November 07, 2016
 */
public abstract class Module extends Task {

    private Painter painter;
    private String status = "Default status";
    public Optional<Task> current = null;
    private boolean completed = false;
    private static boolean isRunAll;
    private boolean paused;

    public Module() {
        super(null);
        start();
    }

    public Module(Module module) {
        super(module);
        start();
    }

    public void setExecuteState(boolean runAll) {
        isRunAll = runAll;
    }

    public void start() {

    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void execute() {
        executeNextChild();
    }


    public void toggleCompleted() {
        completed = !completed;
    }

    public boolean togglePaused() {
        return paused ^= true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setStatus(String message) {
        status = message;
    }

    public String getStatus() {
        return status;
    }

    public Painter getPainter() {
        return this.painter;
    }

    public void exchangePainter(Painter painter) {
        this.painter = painter;
    }

    public void executeNextChild() {
        List<?> children = getChildren();
        Optional<Task> next = children.stream().flatMap(F.instancesOf(Task.class))
                .sorted((j1, j2) -> j2.priority() - j1.priority())
                .filter(Task::validate)
                .findAny();
        if(next.isPresent()) {
            current = next;
            next.get().execute();
        }
    }

    public void executeAllChildren() {
        List<?> children = getChildren();
        children.stream().flatMap(F.instancesOf(Task.class))
                .sorted((j1, j2) -> j2.priority() - j1.priority())
                .filter(Task::validate)
                .forEach(Task::execute);
    }

    @Override
    public void render(Graphics2D g) {
        Painter painter = getPainter();
        if(painter != null) {
            getPainter().render(g);
        } else {
            g.drawString("painter isnt loaded...", 7, 35);
        }
    }

    public boolean isIsRunAll() {
        return isRunAll;
    }

    public boolean isPaused() {
        return paused;
    }
}
