package baegmon.fallingblock.timer;

import baegmon.fallingblock.BlockPlugin;
import baegmon.fallingblock.game.Arena;
import baegmon.fallingblock.game.GameState;
import baegmon.fallingblock.tools.ArenaUtils;
import baegmon.fallingblock.tools.Strings;

import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class Game extends BukkitRunnable {

    private Arena arena;

    private int dropCounter = 0, total = 0;

    private int minX, maxX, minZ, maxZ, height;
    private int numberOfFallingBlocks = -1;

    public Game(Arena arena){
        this.arena = arena;
    }

    void start(){

        long tick = 60L;

        arena.setGameState(GameState.STARTED);
        arena.setRemaining(new HashSet<>(arena.getPlayers()));

        Location pos1 = arena.getPos1();
        Location pos2 = arena.getPos2();

        minX = Math.min(pos1.getBlockX(), pos2.getBlockX()) + 1;
        maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) - 1;

        minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) + 1;
        maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) - 1;

        height = Math.max(pos1.getBlockY(), pos2.getBlockY());

        total = (maxX - minX + 1) * (maxZ - minZ + 1);

        numberOfFallingBlocks = (int) (total * 0.35);

        for(UUID id : arena.getPlayers()){
            Player player = Bukkit.getPlayer(id);
            player.setScoreboard(arena.generateGameScoreboard());

            // teleport players into a random position inside of the arena
            if(!ArenaUtils.insideArena(player.getLocation(), arena.getPos1(), arena.getPos2())){
                player.teleport(ArenaUtils.getRandomLocation(arena.getWorld(), arena.getPos1(), arena.getPos2()));
            }
        }

        this.runTaskTimer(BlockPlugin.getPlugin(BlockPlugin.class), 0L, tick);
    }

    @Override
    public void run() {

        if(arena.getGameState() == GameState.STARTED){

            if(dropCounter == total){
                // CANCEL ARENA IF COUNTER HAS GONE PAST TOTAL NUMBER OF POSSIBLE BLOCKS
                arena.setGameState(GameState.FINISHED);
                arenaComplete();
            }

            String arenaWorld = arena.getWorld();

            for(int i = 0; i < numberOfFallingBlocks; ++i){

                Location location = new Location(
                        Bukkit.getWorld(arenaWorld),
                        ArenaUtils.randomInteger(minX, maxX) + 0.5,
                        height,
                        ArenaUtils.randomInteger(minZ, maxZ) + 0.5
                );

                // first check the location does not already have a block
                if(location.getBlock().getType().equals(Material.AIR)){
                    FallingBlock block = Bukkit.getWorld(arenaWorld).spawnFallingBlock(location, new MaterialData(Material.WOOL));
                    block.setDropItem(false);
                    block.setHurtEntities(true);

                    arena.getBlocks().add(block);

                    dropCounter++;
                }
            }
        }
    }


    public void eliminatePlayer(Player player){
        if(arena.getGameState() == GameState.STARTED && arena.getRemaining().contains(player.getUniqueId())){
            arena.getRemaining().remove(player.getUniqueId());

            player.setGameMode(GameMode.SPECTATOR);

            arena.sendMessage(Strings.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.AQUA + " has been eliminated!");
            arena.updateScoreboard();

            if(arena.getRemaining().size() == 1){
                arena.setGameState(GameState.FINISHED);
                arenaComplete();
            }
        }
    }

    public void playerLeave(Player player){
        if(arena.getRemaining().size() == 1){
            arena.getRemaining().remove(player.getUniqueId());
            arena.updateScoreboard();
            arena.setGameState(GameState.FINISHED);
            arenaComplete();
        }
    }

    private void arenaComplete(){

        if(arena.getGameState() == GameState.FINISHED){

            arena.setGameState(GameState.RESTORING);
            arena.removeScoreboard();

            if(arena.getRemaining().size() == 1){
                Player winner = Bukkit.getPlayer(arena.getRemaining().iterator().next());
                arena.sendTitle(winner.getDisplayName() + " has won!", "", 120);
                winner.setAllowFlight(true);
                winner.setFlying(true);

                Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BlockPlugin.getPlugin(BlockPlugin.class), new Runnable(){
                    int counter = 0;

                    @Override
                    public void run() {
                        if(counter < 5){
                            Firework firework = winner.getWorld().spawn(winner.getLocation(), Firework.class);
                            firework.setFireworkMeta(ArenaUtils.generateRandomFireWorkEffect(firework));
                            counter++;
                        } else {
                            cancel();
                        }
                    }

                }, 0, 20L);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BlockPlugin.getPlugin(BlockPlugin.class), () -> {
                    cancel();
                    arena.reset();
                    winner.setAllowFlight(false);
                    winner.setFlying(false);
                }, 140L);

            }
        }
    }
}
