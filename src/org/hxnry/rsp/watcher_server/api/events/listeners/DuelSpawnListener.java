package org.hxnry.rsp.watcher_server.api.events.listeners;

import org.hxnry.rsp.watcher_server.api.events.DuelSpawnEvent;
import org.rspeer.runetek.event.EventListener;

public interface DuelSpawnListener extends EventListener {

    void initiated(DuelSpawnEvent duelSpawnEvent);

    void result(DuelSpawnEvent duelSpawnEvent);
}