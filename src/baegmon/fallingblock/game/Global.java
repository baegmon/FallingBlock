package baegmon.fallingblock.game;

import org.bukkit.Location;

import java.util.ArrayList;

public enum Global {

    INSTANCE;

    private Location lobby; // location of the main lobby

    private ArrayList<ArenaSign> signs = new ArrayList<>();

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public String getStringLobby(){
        if(lobby == null){
            return "[LOBBY HAS NOT BEEN SET]";
        }
        return "[" + lobby.getBlockX() + ", " + lobby.getBlockY() + ", " + lobby.getBlockZ() + "]";
    }

    public boolean isLobbyValid(){
        return lobby != null && !(lobby.getBlockX() == 0 && lobby.getBlockY() == 0 && lobby.getBlockZ() == 0);
    }

    public ArrayList<ArenaSign> getSigns() {
        return signs;
    }

    public int getSignID(){ return signs.size(); }

    public void addSign(ArenaSign sign){
        signs.add(sign);
    }

}
