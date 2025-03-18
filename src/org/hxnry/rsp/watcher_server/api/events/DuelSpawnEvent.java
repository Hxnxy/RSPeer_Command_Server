package org.hxnry.rsp.watcher_server.api.events;

import org.hxnry.rsp.watcher_server.api.events.listeners.DuelSpawnListener;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;

public class DuelSpawnEvent extends Event<Player> {

    public Type type;

    public enum Type {
        INITIATED, RESULT
    }

    public DuelSpawnEvent(Player source, Type type) {
        super(source);
        this.type = type;
    }

    @Override
    public void forward(EventListener eventListener) {
        if (eventListener instanceof DuelSpawnListener) {
            if(type.equals(Type.INITIATED)) {
                ((DuelSpawnListener) eventListener).initiated(this);
            } else if(type.equals(Type.RESULT)) {
                ((DuelSpawnListener) eventListener).result(this);
            }
        }
    }
}

