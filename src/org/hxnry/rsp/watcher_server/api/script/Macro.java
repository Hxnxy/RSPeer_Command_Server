package org.hxnry.rsp.watcher_server.api.script;

import org.hxnry.rsp.watcher_server.api.events.PlayerSpawnEvent;
import org.hxnry.rsp.watcher_server.api.events.listeners.PlayerSpawnListener;
import org.hxnry.rsp.watcher_server.api.graphics.Paintable;
import org.hxnry.rsp.watcher_server.api.util.F;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.Script;
import org.rspeer.ui.Log;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hxnry
 * @since November 07, 2016
 */
public abstract class Macro extends Script implements RenderListener {

    private final RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private final LinkedBlockingDeque<Module> deque = new LinkedBlockingDeque<>();
    private final AtomicInteger speed = new AtomicInteger(250);
    public PlayerSpawnListener playerListener;
    public final ScriptEventManager scriptEventManager = new ScriptEventManager();
    private Thread events;

    private Module module = null;
    public int state = 0;

    public abstract void init();

    public abstract void bye();

    public abstract void render(Graphics graphics);

    public PlayerSpawnListener getDefaultPlayerListener() {
        return playerListener != null ? playerListener : new PlayerSpawnListener() {
            @Override
            public void loaded(PlayerSpawnEvent playerEvent) {
                //Log.fine("spotted -> " + playerEvent.getSource().getName());
            }

            @Override
            public void unloaded(PlayerSpawnEvent playerEvent) {
                //Log.fine("un-rendered -> " + playerEvent.getSource().getName());
            }
        };
    }

    public void setFramesOnTop(boolean b) {
        for(Frame frame : Frame.getFrames()) {
            frame.setAlwaysOnTop(b);
        }
    }

    public void debug(String s) {
        Log.info(s);
    }

    @Override
    public void onStart() {
        init();
        Log.info("Welcome to " + super.getMeta().name() + "!");
        //EventManager.setNamesToFind(new String[] { "RJDale", "Sorc Wizard", "PLay and eat" });
        events = new Thread(() -> {
            while(playerListener != null && Game.getEventDispatcher().isRegistered(playerListener)) {
                scriptEventManager.cycle();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Log.fine("Exited event manager thread.");
        });
        playerListener = getDefaultPlayerListener();
        if (!Game.getEventDispatcher().isRegistered(playerListener)) {
            //Log.fine("added : " + "item listener" + " to the event dispatcher");
            Game.getEventDispatcher().register(playerListener);
            events.start();
        }
    }

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics2D graphics = (Graphics2D) renderEvent.getSource();
        graphics.setRenderingHints(renderingHints);
        if (module != null) {
            List<?> children = module.getChildren();
            children.stream().flatMap(F.instancesOf(Paintable.class)).forEach(p -> p.render(graphics));
            module.render(graphics);
        } else {
            graphics.drawString("Module is null!", 7, 35);
        }
        this.render(graphics);
    }

    @Override
    public void onStop() {
        onMacroExit();
        this.state = 1;
    }

    @Override
    public int loop() {
        Module module = getCurrentModule();
        if(module != null) {
            if(module.getChildren().size() > 0) {
                module.runAll();
            } else {
                module.run();
            }
        } else {
            return -1;
        }
        return getLoopingSpeed();
    }

    public void onMacroExit() {
        bye();
        if (Game.getEventDispatcher().isRegistered(playerListener)) {
            //Log.fine("removed : " + "player listener" + " from the event dispatcher");
            Game.getEventDispatcher().deregister(playerListener);
        }
        //Log.fine("Goodbye! - " + getMeta().name() + " ver" + getMeta().version());
    }

    protected void add(Module... modules) {
        Arrays.asList(modules).forEach(deque::offer);
    }

    protected void setLoopingSpeed(int speed) {
        this.speed.set(speed);
    }

    public int getLoopingSpeed() {
        return this.speed.get();
    }

    private Module getCurrentModule() {
        if (module == null) {
            module = deque.poll();
        } else if (module.isCompleted()) {
            module = null;
        }
        return module;
    }
}
