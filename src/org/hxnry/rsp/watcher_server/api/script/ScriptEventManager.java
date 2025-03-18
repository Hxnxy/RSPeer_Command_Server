package org.hxnry.rsp.watcher_server.api.script;

import org.hxnry.rsp.watcher_server.api.events.PlayerSpawnEvent;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;
import org.rspeer.runetek.api.scene.Players;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScriptEventManager {

    private static String[] names = new String[0];
    private static ConcurrentHashMap<String, Player> playerHashMap = new ConcurrentHashMap <>();

    /** START OF PLAYER EVENT SECTION **/

    public void checkForPlayers() {
        playerHashMap.entrySet().removeIf(entry -> {
            boolean result;
            if((result = !exists().test(entry.getValue()))) {
                PlayerSpawnEvent playerEvent = new PlayerSpawnEvent(entry.getValue(), PlayerSpawnEvent.Type.LOADED);
                Game.getEventDispatcher().immediate(playerEvent);
            }
            return result;
        });
        PositionableQueryResults<Player> currentPlayers = names.length > 0 ? Players.newQuery().names(names).results() :
                Players.newQuery().results();
        currentPlayers.forEach(spottedPlayer -> {
            String name = spottedPlayer.getName();
            if(!playerHashMap.containsKey(name)) {
                PlayerSpawnEvent playerEvent = new PlayerSpawnEvent(spottedPlayer, PlayerSpawnEvent.Type.UNLOADED);
                Game.getEventDispatcher().immediate(playerEvent);
            }
            playerHashMap.put(name, spottedPlayer);
        });
    }


    public static Player[] getLoadedPlayers() {
        List<Player> players = new ArrayList<>();
        Set<Map.Entry<String, Player>> set = playerHashMap.entrySet().stream().filter(entry -> {
            Player instance = entry.getValue();
            return instance != null && exists().test(instance);
        }).collect(Collectors.toSet());
        set.forEach(s -> players.add(s.getValue()));
        return players.size() == 0 ? new Player[0] : players.toArray(Player[]::new);
    }

    /**
    public Player[] getLoadedPlayers() {
        List<Player> players = new ArrayList<>();
        playerHashMap.forEach((k, v) -> {
            players.add(v);
        });
        return players.toArray(Player[]::new);
    }
     **/

    private static Predicate<Player> exists() {
        return p -> p != null && !Players.newQuery().names(p.getName()).results().isEmpty();
    }

    public static void setNames(String[] newNames) {
        names = newNames;
    }

    final void cycle() {
        checkForPlayers();
    }

    /** END OF PLAYER EVENT SECTION **/
}
