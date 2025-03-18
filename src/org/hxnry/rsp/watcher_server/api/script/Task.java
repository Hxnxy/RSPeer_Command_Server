package org.hxnry.rsp.watcher_server.api.script;

import org.hxnry.rsp.watcher_server.api.graphics.Paintable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Hxnry
 * @since November 07, 2016
 */
public abstract class Task<T extends Module> implements Paintable {

    private final List<Task> children = Collections.synchronizedList(new ArrayList<>());

    protected T parent;

    public Task(T parent) {
        this.parent = parent;
    }

    public abstract boolean validate();
    public abstract void execute();

    public void exchangeParent(T parent) {
        this.parent = parent;
    }

    public int priority() {
        return 0;
    }

    public final void run() {
        if(validate()) {
            execute();
            return;
        }
        for(Task child : children) {
            if (child.validate()) {
                child.execute();
                return;
            }
            child.run();
        }
    }

    public final void runAll() {
        if(validate()) {
            execute();
        }
        for(Task child : children) {
            if (child.validate()) {
                child.execute();
                return;
            }
            child.run();
        }
    }

    public List<?> getChildren() {
        return children;
    }

    public void add(Task... tasks) {
        children.addAll(Arrays.asList(tasks));
    }

    public T getParent() {
        return parent;
    }

    public void render(Graphics2D graphics2D) {

    }
}
