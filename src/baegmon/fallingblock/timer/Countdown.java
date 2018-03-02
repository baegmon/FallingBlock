package baegmon.fallingblock.timer;

import baegmon.fallingblock.BlockPlugin;
import baegmon.fallingblock.game.Arena;
import baegmon.fallingblock.game.ArenaState;
import baegmon.fallingblock.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private final Arena arena;
    private int countdown = 0;
    private int time;
    private boolean hasStarted = false;

    public Countdown(Arena arena){
        this.arena = arena;
    }

    public void start(int time){
        arena.setGameState(GameState.COUNTDOWN);
        this.time = time;
        this.countdown = time;
        this.runTaskTimer(BlockPlugin.getPlugin(BlockPlugin.class),0L,20L);
        hasStarted = true;
    }

    @Override
    public void run() {
        if(arena.getArenaState() == ArenaState.ENABLED){

            if(countdown > 0){

                if(countdown == time){
                    arena.sendTitle(ChatColor.GREEN + "Game Starting!", "");
                }

                if(arena.getMinPlayers() > arena.getPlayers().size() && arena.getGameState() == GameState.COUNTDOWN){
                    if(!arena.isForceStarted()){
                        arena.setGameState(GameState.WAITING);
                        arena.setLevelCountdown(0);
                        cancel();
                    }
                }

                countdown--;

                switch(countdown){
                    case 5:
                        arena.alertPlayers(countdown, Sound.BLOCK_NOTE_PLING, ChatColor.GREEN + "5", "");
                        return;
                    case 4:
                        arena.alertPlayers(countdown, Sound.BLOCK_NOTE_PLING, ChatColor.GREEN + "4", "");
                        return;
                    case 3:
                        arena.alertPlayers(countdown, Sound.BLOCK_NOTE_PLING, ChatColor.YELLOW + "3", "");
                        return;
                    case 2:
                        arena.alertPlayers(countdown, Sound.BLOCK_NOTE_PLING, ChatColor.YELLOW + "2", "");
                        return;
                    case 1:
                        arena.alertPlayers(countdown, Sound.BLOCK_NOTE_PLING, ChatColor.RED + "1", "");
                        return;
                    case 0:
                        arena.alertPlayers(countdown, Sound.ENTITY_PLAYER_LEVELUP, "", ChatColor.RED + "Survive!");
                        arena.updateSigns();
                        arena.getGame().start();
                        this.cancel();
                        return;
                    default:
                        arena.setLevelCountdown(countdown);
                        break;
                }
            }
        }
    }

    public boolean hasStarted(){
        return hasStarted;
    }
}
