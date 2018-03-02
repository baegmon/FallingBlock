package baegmon.fallingblock.manager;

import baegmon.fallingblock.game.Arena;

import java.util.HashMap;

public enum ArenaManager {

    INSTANCE;

    private HashMap<String, Arena> arenas = new HashMap<>();

    public boolean doesArenaExist(String arenaName){ return arenas.containsKey(arenaName); }

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(String arenaName){
        return arenas.get(arenaName);
    }

    public void addArena(String arenaName, Arena arena){
        arenas.put(arenaName, arena);
    }

    public void removeArena(String arenaName){
        arenas.remove(arenaName);
    }

}
