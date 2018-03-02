package baegmon.fallingblock.game;

import baegmon.fallingblock.tools.SignType;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ArenaSign {

    private int id;
    private String arena;
    private SignType type;

    // Location of the sign
    private int x, y, z;
    private String world;

    // Used for initial loading of signs
    public ArenaSign(int id){
        this.id = id;
    }

    // Used for LEAVE signs
    public ArenaSign(int id, SignType type, Location location){
        this.id = id;
        this.type = type;
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    // Used for JOIN signs
    public ArenaSign(int id, String arena, SignType type, Location location){
        this.id = id;
        this.type = type;
        this.arena = arena;
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public boolean compareLocation(Location toCompare){
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        return location.equals(toCompare);
    }

    public void setLocation(Location location){
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public int getId(){
        return id;
    }

    public String getArena() {
        return arena;
    }

    public SignType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArena(String arena) {
        this.arena = arena;
    }

    public void setType(SignType type) {
        this.type = type;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
