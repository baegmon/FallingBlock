package baegmon.fallingblock.listener;

import baegmon.fallingblock.configuration.ConfigurationManager;
import baegmon.fallingblock.game.*;
import baegmon.fallingblock.manager.ArenaManager;
import baegmon.fallingblock.tools.ArenaUtils;
import baegmon.fallingblock.tools.Strings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.Iterator;

public class ArenaListener implements Listener {

    /*
    Listens for whenever players break blocks and check
    if the block to break exists within the BlockSurvival Arena
    and cancels the event.

    TODO: Disable modifications caused from other plugins such as WorldEdit
    */

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event){

        Block b = event.getBlock();

        if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN){

            for (final Iterator iterator = Global.INSTANCE.getSigns().iterator(); iterator.hasNext(); ) {
                ArenaSign sign = (ArenaSign) iterator.next();

                if(sign != null && sign.compareLocation(b.getLocation())){

                    Player player = event.getPlayer();

                    if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                        player.sendMessage(Strings.SIGN_REMOVED);

                        FileConfiguration gameConfiguration = ConfigurationManager.INSTANCE.getConfiguration();
                        gameConfiguration.set("Game.Signs." + sign.getId(), null);

                        ConfigurationManager.INSTANCE.saveConfiguration();

                        iterator.remove();

                    } else {
                        event.setCancelled(true);
                    }

                    break;
                }
            }

        } else {

            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.getArenaState() == ArenaState.ENABLED){

                    Vector v1 = Vector.getMaximum(arena.getPos1().toVector(), arena.getPos2().toVector());
                    Vector v2 = Vector.getMinimum(arena.getPos1().toVector(), arena.getPos2().toVector());

                    Vector v = new Vector(b.getX(), b.getY(), b.getZ());
                    boolean insideArena = v.isInAABB(v2, v1);

                    if(insideArena){
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.playerInsideArena(player)){

                    // if player is waiting inside of an arena they should take no damage
                    if(arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.COUNTDOWN){
                        event.setCancelled(true);

                        if(arena.getLobby() == null){
                            player.teleport(ArenaUtils.getRandomLocation(
                                    arena.getWorld(),
                                    arena.getPos1(),
                                    arena.getPos2()));
                        } else {
                            player.teleport(arena.getLobby());
                        }

                    } else if (arena.getGameState() == GameState.STARTED){

                        if(event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK || event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
                            arena.getGame().eliminatePlayer(player);
                        }

                        event.setCancelled(true);

                    } else if (arena.getGameState() == GameState.FINISHED || arena.getGameState() == GameState.RESTORING){
                        event.setCancelled(true);
                    }

                    break;
                }
            }
        }
    }

    // Players inside of the lobby / game should not lose hunger
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof  Player){
            Player player = (Player) event.getEntity();
            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.playerInsideArena(player)){
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
            if(arena != null && arena.playerInsideArena(player)) {
                arena.leave(player);

                if(arena.getGameState() == GameState.STARTED){
                    arena.getGame().playerLeave(player);
                }
                break;
            }
        }
    }
}
