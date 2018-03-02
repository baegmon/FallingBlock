package baegmon.fallingblock.game;

import baegmon.fallingblock.BlockPlugin;
import baegmon.fallingblock.manager.PlayerInfoManager;
import baegmon.fallingblock.timer.Countdown;
import baegmon.fallingblock.timer.Game;
import baegmon.fallingblock.tools.ArenaUtils;
import baegmon.fallingblock.tools.PlayerInfo;
import baegmon.fallingblock.tools.SignType;
import baegmon.fallingblock.tools.Strings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Arena {

    // variables used for identification of the Arena
    private String name; // name of Arena
    private String world; // name of world that contains the Arena
    private Location lobby; // waiting lobby of arena
    private Location pos1; // position of corner of arena
    private Location pos2; // position of corner of arena
    private int minPlayers = -1; // minimum number of players required to start arena
    private int maxPlayers = -1; // maximum number of players that can join an arena
    private int wait = 30; // number of seconds to wait inside of the lobby

    // variables for the running the arena
    private Set<UUID> players = new HashSet<>();
    private ArenaState arenaState;
    private GameState gameState;

    // Tasks that will run the arena
    private boolean forceStarted = false;
    private Countdown countdown = new Countdown(this);
    private Game game = new Game(this);

    // variables for in-game
    private Set<UUID> remaining = new HashSet<>();
    private Set<UUID> spectating = new HashSet<>();

    // variables for the arena
    private ArrayList<FallingBlock> blocks = new ArrayList<>();

    public Arena(String name){
        this.name = name;
        this.arenaState = ArenaState.DISABLED;
        this.gameState = GameState.WAITING;
    }

    public boolean joinable(){
        return gameState == GameState.WAITING || gameState == GameState.COUNTDOWN;
    }

    public boolean isPlayerInsideArena(Player player){
        return players.contains(player.getUniqueId());
    }

    private int playerCount(){
        return players.size();
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public boolean playerInsideArena(Player player){
        return players.contains(player.getUniqueId());
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getWait() {
        return wait;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public Game getGame() {
        return game;
    }

    public Set<UUID> getRemaining() {
        return remaining;
    }

    public Set<UUID> getSpectating() {
        return spectating;
    }

    public ArrayList<FallingBlock> getBlocks() {
        return blocks;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setArenaState(boolean enabled){
        if(enabled){
            arenaState = ArenaState.ENABLED;
        } else {
            arenaState = ArenaState.DISABLED;
        }
    }

    public void setArenaState(ArenaState arenaState){
        this.arenaState = arenaState;
    }

    public void setLobby(Location lobby){ this.lobby =  lobby; }

    public void setPos1(Location pos1){
        this.pos1 = pos1;
        if(pos1 != null && pos2 != null){
            if(pos1.getWorld().getName().equals(pos2.getWorld().getName())){
                world = pos1.getWorld().getName();
            }
        }
    }

    public void setPos2(Location pos2){
        this.pos2 = pos2;
        if(pos1 != null && pos2 != null){
            if(pos1.getWorld().getName().equals(pos2.getWorld().getName())){
                world = pos1.getWorld().getName();
            }
        }
    }

    public void setMinPlayers(int minPlayers){
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    public void setWait(int wait) {
        if(wait <= 0){
            this.wait = 30;
        } else {
            this.wait = wait;
        }
    }

    private void clearArenaBlocks() {
        Iterator<FallingBlock> iterator = blocks.iterator();

        while(iterator.hasNext()){
            Block b = iterator.next().getLocation().getBlock();
            b.setType(Material.AIR);
            iterator.remove();
        }
    }

    public void setRemaining(Set<UUID> remaining) {
        this.remaining = remaining;
    }

    public String getLobbyString(){
        if(lobby == null) {
            return "[LOBBY NOT SET]";
        }
        return "[" + lobby.getBlockX() + ", " + lobby.getBlockY() + ", "  + lobby.getBlockZ() + "]";
    }

    public String getStringPos1(){
        if(pos1 == null){
            return "[POS1 NOT SET]";
        }
        return "[" + pos1.getBlockX() + ", " + pos1.getBlockY() + ", " + pos1.getBlockZ() + "]";
    }

    public String getStringPos2(){
        if(pos2 == null){
            return "[POS2 NOT SET]";
        }
        return "[" + pos2.getBlockX() + ", " + pos2.getBlockY() + ", " + pos2.getBlockZ() + "]";
    }

    public boolean isForceStarted(){
        return forceStarted;
    }

    public boolean isLobbyValid(){
        return lobby != null && !(lobby.getBlockX() == 0 && lobby.getBlockY() == 0 && lobby.getBlockZ() == 0);
    }

    public boolean canSetPos1(Location location) {
        return pos2 == null || location.getWorld().getName().equals(pos2.getWorld().getName());
    }

    public boolean canSetPos2(Location location) {
        return pos1 == null || location.getWorld().getName().equals(pos1.getWorld().getName());
    }

    public boolean isPos1Valid(){
        return pos1 != null && !(pos1.getBlockX() == 0 && pos1.getBlockY() == 0 && pos1.getBlockZ() == 0);
    }

    public boolean isPos2Valid(){
        return pos2 != null && !(pos2.getBlockX() == 0 && pos2.getBlockY() == 0 && pos2.getBlockZ() == 0);
    }

    public boolean isPlayerRequirementsValid(){
        return minPlayers > 0 && maxPlayers > 0 && (maxPlayers >= minPlayers);
    }

    public boolean isWorldValid(){
        return world != null && !world.isEmpty() && Bukkit.getWorld(world) != null;
    }

    public boolean isArenaValid(){
        return arenaState == ArenaState.ENABLED && isWorldValid() && isPlayerRequirementsValid() && isPos1Valid() && isPos2Valid();
    }

    public void forceStart(){
        forceStarted = true;
        startCountdown();

        Scoreboard scoreboard = generateScoreboard();

        for(UUID id : players){
            Player p = Bukkit.getPlayer(id);
            p.setScoreboard(scoreboard);
        }
    }

    private void startCountdown(){
        if(countdown.hasStarted()){
            countdown = new Countdown(this);
        }

        countdown.start(wait);
    }

    public void join(Player player){
        players.add(player.getUniqueId());

        PlayerInfo info = new PlayerInfo(player);
        PlayerInfoManager.INSTANCE.addInfo(player.getUniqueId(), info);

        player.getInventory().clear();

        if(players.size() >= minPlayers){
            startCountdown();
        }

        Scoreboard scoreboard = generateScoreboard();

        for(UUID id : players){
            Player p = Bukkit.getPlayer(id);
            p.setScoreboard(scoreboard);
            p.sendMessage(Strings.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.AQUA + " has joined the arena!");
        }

        player.setGameMode(GameMode.ADVENTURE);

        if(lobby == null){
            // if no lobby set, teleport player to random location inside of arena
            player.teleport(ArenaUtils.getRandomLocation(world, pos1, pos2));
        } else {
            player.teleport(lobby);
        }

        updateSigns();
    }

    public void leave(Player player){

        player.setLevel(0);

        PlayerInfo info = PlayerInfoManager.INSTANCE.getInfo(player.getUniqueId());
        info.restorePlayer();

        players.remove(player.getUniqueId());

        if(spectating.contains(player.getUniqueId())){
            spectating.remove(player.getUniqueId());
        }

        if(remaining.contains(player.getUniqueId())){
            remaining.remove(player.getUniqueId());
        }

        // remove scoreboard from player that left the lobby
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        Scoreboard scoreboard = null;
        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN){
            // update scoreboard for those already inside of the lobby
            scoreboard = generateScoreboard();
        } else if (gameState == GameState.STARTED){
            // player left while game is on
            scoreboard = generateGameScoreboard();
        }

        for(UUID id : players){
            Player p = Bukkit.getPlayer(id);

            p.sendMessage(Strings.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.AQUA + " has left the arena!");

            if(scoreboard != null){
                p.setScoreboard(scoreboard);
            } else {
                System.out.println("This error should never appear.");
            }
        }

        player.teleport(Global.INSTANCE.getLobby());

        player.sendMessage(Strings.PREFIX + ChatColor.AQUA + "You have left " + ChatColor.WHITE + name + ChatColor.AQUA + " !");
        updateSigns();

    }

    private Scoreboard generateScoreboard(){
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("FallingBlock", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Strings.PREFIX);

        Score line = objective.getScore(ChatColor.GRAY + "==================");
        line.setScore(7);

        Score line2 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Arena: " + ChatColor.GOLD + "" + name);
        line2.setScore(6);

        Score line3 = objective.getScore("");
        line3.setScore(5);

        Score line4;
        if(gameState == GameState.WAITING){
            line4 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Status: " + ChatColor.RED + "" + gameState);
        } else if (gameState == GameState.COUNTDOWN){
            line4 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Status: " + ChatColor.YELLOW + "" + gameState);
        } else {
            line4 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Status: " + ChatColor.GREEN + "" + gameState);
        }

        line4.setScore(4);

        Score line5 = objective.getScore(" ");
        line5.setScore(3);

        Score line6 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Players: " + ChatColor.GREEN + "" + playerCount() + " / " + maxPlayers);
        line6.setScore(2);

        int counter = 1;

        if(minPlayers > players.size() && !forceStarted){
            Score line7 = objective.getScore(ChatColor.RED + "" + ChatColor.ITALIC + "Need at least " + minPlayers + " players!");
            line7.setScore(1);
            counter--;
        }

        Score line8 = objective.getScore(ChatColor.GRAY + "================== ");
        line8.setScore(counter);

        return scoreboard;
    }

    public Scoreboard generateGameScoreboard(){
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("[FallingBlock]", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + "FallingBlock");

        Score line = objective.getScore(ChatColor.GRAY + "==================");
        line.setScore(4);

        Score line2 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Arena: " + ChatColor.GOLD + "" + name);
        line2.setScore(3);

        Score line3 = objective.getScore("");
        line3.setScore(2);

        Score line4 = objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Remaining: " + ChatColor.GREEN + "" +  remaining.size());
        line4.setScore(1);

        Score line5 = objective.getScore(ChatColor.GRAY + "================== ");
        line5.setScore(0);

        return scoreboard;
    }

    public void setLevelCountdown(int time){
        for (UUID u : players) {
            Player player = Bukkit.getPlayer(u);
            player.setLevel(time);
        }
    }

    public void alertPlayers(int time, Sound sound, String title, String subtitle){
        for (UUID u : players) {
            Player player = Bukkit.getPlayer(u);
            player.setLevel(time);
            player.playSound(player.getLocation(), sound, 1, 1);
            player.sendTitle(title, subtitle, 5, 20, 10);
        }
    }

    public void sendMessage(String message){
        for(UUID id : players){
            Bukkit.getPlayer(id).sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle){
        for (UUID u : players) {
            Player player = Bukkit.getPlayer(u);
            player.sendTitle(title, subtitle, 5, 20, 10);
        }
    }

    public void sendTitle(String title, String subtitle, int length){
        for (UUID u : players) {
            Player player = Bukkit.getPlayer(u);
            player.sendTitle(title, subtitle, 5, length, 10);
        }
    }

    public void saveArenaBlocks(){
        if(isPos1Valid() && isPos2Valid() && arenaState == ArenaState.ENABLED){
            Location l1 = new Location(Bukkit.getWorld(world), pos1.getX(), pos1.getY(), pos1.getZ());
            Location l2 = new Location(Bukkit.getWorld(world), pos2.getX(), pos2.getY(), pos2.getZ());
        }
    }

    public void removeScoreboard(){
        for(UUID id : players){
            Bukkit.getPlayer(id).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    public void updateScoreboard(){
        for(UUID id : players){
            Player player = Bukkit.getPlayer(id);
            player.setScoreboard(generateGameScoreboard());
        }
    }

    public void updateSigns(){
        for(ArenaSign sign : Global.INSTANCE.getSigns()){
            if(sign.getType() == SignType.JOIN  && sign.getArena().equals(name)){
                Block block = Bukkit.getWorld(sign.getWorld()).getBlockAt(sign.getX(), sign.getY(), sign.getZ());

                if(block != null && block.getState() instanceof Sign){
                    Sign s = (Sign) block.getState();

                    String[] lines = s.getLines();
                    lines[2] = gameState.toString();
                    lines[3] = players.size() + " / " + maxPlayers;
                    s.update();
                }
            }
        }
    }

    private void resetPlayers(){
        for(UUID id : players){
            Player player = Bukkit.getPlayer(id);

            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

            PlayerInfo info = PlayerInfoManager.INSTANCE.getInfo(id);
            info.restorePlayer();

            player.teleport(Global.INSTANCE.getLobby());
        }
    }

    public void reset(){
        this.arenaState = ArenaState.DISABLED;
        forceStarted = false;

        resetPlayers();

        players.clear();
        remaining.clear();
        spectating.clear();

        countdown = new Countdown(this);
        game = new Game(this);

        // create runnable to restore the block states
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BlockPlugin.getPlugin(BlockPlugin.class), () -> {
            clearArenaBlocks();
            arenaState = ArenaState.ENABLED;
            gameState = GameState.WAITING;
            updateSigns();
        });

        updateSigns();
    }
}
