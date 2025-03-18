package org.hxnry.rsp.watcher_server.api.events;

import org.hxnry.rsp.watcher_server.api.events.listeners.PlayerSpawnListener;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;

public class PlayerSpawnEvent extends Event<Player> {

    public Type type;

    public enum Type {
        LOADED, UNLOADED
    }

    public PlayerSpawnEvent(Player source, Type type) {
        super(source);
        this.type = type;
    }

    @Override
    public void forward(EventListener eventListener) {
        if (eventListener instanceof PlayerSpawnListener) {
            if(type.equals(Type.LOADED)) {
                ((PlayerSpawnListener) eventListener).loaded(this);
            } else if(type.equals(Type.UNLOADED)) {
                ((PlayerSpawnListener) eventListener).unloaded(this);
            }
        }
    }
}
