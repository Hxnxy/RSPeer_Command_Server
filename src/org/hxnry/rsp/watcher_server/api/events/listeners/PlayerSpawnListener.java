package org.hxnry.rsp.watcher_server.api.events.listeners;

import org.hxnry.rsp.watcher_server.api.events.PlayerSpawnEvent;
import org.rspeer.runetek.event.EventListener;

public interface PlayerSpawnListener extends EventListener {

    void loaded(PlayerSpawnEvent playerEvent);

    void unloaded(PlayerSpawnEvent playerEvent);
}
