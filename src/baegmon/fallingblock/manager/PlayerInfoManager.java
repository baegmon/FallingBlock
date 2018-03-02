package baegmon.fallingblock.manager;

import baegmon.fallingblock.tools.PlayerInfo;

import java.util.HashMap;
import java.util.UUID;

public enum PlayerInfoManager {

    INSTANCE;

    private HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();

    public void addInfo(UUID id, PlayerInfo info){
        playerInfo.put(id, info);
    }

    public void removeInfo(UUID id){
        playerInfo.remove(id);
    }

    public PlayerInfo getInfo(UUID id){
        return playerInfo.get(id);
    }

}
