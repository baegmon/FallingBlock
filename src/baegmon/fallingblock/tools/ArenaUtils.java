package baegmon.fallingblock.tools;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaUtils {

    public static Location getRandomLocation(String world, Location pos1, Location pos2) {

        double minX = Math.min(pos1.getBlockX(), pos2.getBlockX()) + 1;
        double minY = Math.min(pos1.getY() + 1, pos2.getY() + 1);
        double minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) + 1;

        double maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) - 1;
        double maxY = Math.max(pos1.getY() + 1, pos2.getY() + 1);
        double maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) - 1;

        boolean safe = false;
        Location location = null;

        while(!safe){
            location = new Location(Bukkit.getWorld(world), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
            safe = isSafeLocation(location);
        }

        return location;
    }

    public static boolean insideArena(Location playerLocation, Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX()) + 0.5;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 0.5;

        double px = playerLocation.getX();
        double py = playerLocation.getY();
        double pz = playerLocation.getZ();

        return (px >= minX && px <= maxX) && (py >= minY) && (pz >= minZ && pz <= maxZ);
    }

    private static double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }

    public static int randomInteger(int min, int max) {
        return min + ThreadLocalRandom.current().nextInt(Math.abs(max - min + 1));
    }

    private static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        return ground.getType().isSolid();
    }

    public static FireworkMeta randomFireWorkEffect(Firework f){
        FireworkMeta fm = f.getFireworkMeta();
        fm.setPower(0);

        Random random = new Random();
        int effectAmount = random.nextInt(3) + 1;
        for(int i = 0; i < effectAmount; i++) {
            FireworkEffect.Builder b = FireworkEffect.builder();
            int colorAmount = random.nextInt(3) + 1;
            for(int ii = 0; ii < colorAmount; ii++) {
                b.withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
            b.with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]);
            b.flicker(random.nextInt(2) != 0);
            b.trail(random.nextInt(2) != 0);
            fm.addEffect(b.build());
        }

        return fm;
    }

}
