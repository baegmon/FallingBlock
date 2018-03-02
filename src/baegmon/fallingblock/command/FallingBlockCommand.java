package baegmon.fallingblock.command;

import baegmon.fallingblock.configuration.ConfigurationManager;
import baegmon.fallingblock.game.Arena;
import baegmon.fallingblock.game.ArenaState;
import baegmon.fallingblock.game.GameState;
import baegmon.fallingblock.game.Global;
import baegmon.fallingblock.manager.ArenaManager;
import baegmon.fallingblock.tools.NumberUtils;
import baegmon.fallingblock.tools.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FallingBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player){

            Player player = (Player) commandSender;

            if(strings == null || strings.length == 0) {
                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "VERSION " + ChatColor.AQUA + Strings.VERSION);
                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "CREATED BY " + Strings.CREATOR);
                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Available Commands:");
                commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA  + "admin" + ChatColor.WHITE + " - display admin commands");
                commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA  + "player" + ChatColor.WHITE + " - display player commands");
                commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA  + "arena" + ChatColor.WHITE +  " - display arena setup commands");
            } else {

                String usage = strings[0];

                if(usage.equalsIgnoreCase("arena") && strings.length == 1){
                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena Setup Commands");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "setmainlobby: " + ChatColor.WHITE + "set current player position as the return point when a game finishes or player exits an arena");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "status <arena>: " + ChatColor.WHITE + "check the setup status of an arena");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "create <arena>: " + ChatColor.WHITE + "create an <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "delete <arena>: " + ChatColor.WHITE + "delete an <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "enable <arena>: " + ChatColor.WHITE + "enable an <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "disable <arena>: " +  ChatColor.WHITE + "disable an <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "lobby <arena>: " + ChatColor.WHITE + "set position as the waiting lobby of <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "pos1 <arena>: " + ChatColor.WHITE + "set position #1 of arena field");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "pos2 <arena>: " + ChatColor.WHITE + "set position #2 of arena field");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "setminplayers <arena> <amount>: " + ChatColor.WHITE + "set minimum number of players for the <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "setmaxplayers <arena> <amount>: " + ChatColor.WHITE + "set maximum number of players for the <arena>");
                } else if (usage.equalsIgnoreCase("admin") && strings.length == 1){
                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Administrator Commands");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "forcestart <arena>: " + ChatColor.WHITE + "force-start <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "forcestop <arena>: " + ChatColor.WHITE + "force-stop <arena>");
                } else if (usage.equalsIgnoreCase("player") && strings.length == 1){
                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Player Commands");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "list: " + ChatColor.WHITE + "list all available arenas");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "join <arena>: " + ChatColor.WHITE + "join <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "leave: " + ChatColor.WHITE + "leave current arena");
                } else {

                    ////////////////////////////////////////////////////////////////////////////
                    // LIST
                    ////////////////////////////////////////////////////////////////////////////

                    if(usage.equalsIgnoreCase("list")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER) ||
                                player.hasPermission(Strings.PERMISSION_LIST)){

                            if(strings.length == 1){

                                if(ArenaManager.INSTANCE.getArenas().isEmpty()){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "No arenas have been created.");
                                } else {

                                    StringBuilder builder = new StringBuilder();
                                    builder.append(Strings.PREFIX).append(ChatColor.WHITE).append("Available Arenas: ");

                                    for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                        builder.append(arena.getName()).append(" ");
                                    }

                                    commandSender.sendMessage(builder.toString());
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "list");
                            }

                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // STATUS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("status")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArenas().get(arenaName);

                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + " Arena Information: " + ChatColor.AQUA + arena.getName());

                                    if(Global.INSTANCE.isLobbyValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Main Lobby: " + ChatColor.GOLD + Global.INSTANCE.getStringLobby() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Main Lobby: " + ChatColor.GOLD + Global.INSTANCE.getStringLobby() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.getArenaState() == ArenaState.ENABLED){
                                        commandSender.sendMessage(ChatColor.WHITE + "Active: " + ChatColor.GOLD + arena.getArenaState() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Active: " + ChatColor.GOLD + arena.getArenaState() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isWorldValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "World: " + ChatColor.GOLD + arena.getWorld() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "World: " + ChatColor.GOLD + arena.getWorld() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isLobbyValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Using Lobby: " + ChatColor.GOLD + arena.getLobbyString() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Using Lobby: " + ChatColor.RED + " [FALSE]");
                                    }

                                    if(arena.isPos1Valid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 1: " + ChatColor.GOLD + arena.getStringPos1() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 1: " + ChatColor.GOLD + arena.getStringPos1() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isPos2Valid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 2: " + ChatColor.GOLD + arena.getStringPos2() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 2: " + ChatColor.GOLD + arena.getStringPos2() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isPlayerRequirementsValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Minimum Players Required: " + ChatColor.GOLD + arena.getMinPlayers() + ChatColor.GREEN + " [READY]");
                                        commandSender.sendMessage(ChatColor.WHITE + "Maximum Players Required: " + ChatColor.GOLD + arena.getMaxPlayers() + ChatColor.GREEN + " [READY]");
                                    } else {

                                        if(arena.getMinPlayers() > 0){
                                            commandSender.sendMessage(ChatColor.WHITE + "Minimum Players Required: " + ChatColor.GOLD + arena.getMinPlayers() + ChatColor.GREEN + " [READY]");
                                        } else {
                                            commandSender.sendMessage(ChatColor.WHITE + "Minimum Players Required: " + ChatColor.GOLD + arena.getMinPlayers() + ChatColor.RED + " [NOT READY]");
                                        }

                                        if(arena.getMaxPlayers() > 0 && arena.getMaxPlayers() >= arena.getMinPlayers()){
                                            commandSender.sendMessage(ChatColor.WHITE + "Maximum Players Required: " + ChatColor.GOLD + arena.getMaxPlayers() + ChatColor.GREEN + " [READY]");
                                        } else {
                                            commandSender.sendMessage(ChatColor.WHITE + "Maximum Players Required: " + ChatColor.GOLD + arena.getMaxPlayers() + ChatColor.RED + " [NOT READY]");
                                        }

                                        if(arena.getMinPlayers() >= arena.getMaxPlayers()){
                                            commandSender.sendMessage(ChatColor.RED + "Minimum players required cannot be higher than maximum players required!");
                                        }
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "status <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // CREATE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("create")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){

                                    Arena arena = new Arena(arenaName);

                                    ArenaManager.INSTANCE.addArena(arenaName, arena);
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Arena " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " created successfully.");

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".Enabled", false);
                                    arenaConfiguration.set("Arenas." + arenaName + ".LobbyTime", 30);
                                    arenaConfiguration.set("Arenas." + arenaName + ".Difficulty", 1);

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                } else {
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Arena " + ChatColor.WHITE + arenaName +  ChatColor.RED + " already exists.");
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "create <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DELETE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("delete")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {
                                    ArenaManager.INSTANCE.removeArena(arenaName);
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Arena " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " deleted.");


                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName, null);

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "delete <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // ENABLE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("enable")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState() == ArenaState.DISABLED){
                                        arena.setArenaState(ArenaState.ENABLED);
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.GREEN + " enabled.");

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Enabled", true);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " is already enabled!");
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "enable <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DISABLE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("disable")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState() == ArenaState.DISABLED){
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " is already disabled!");
                                    } else {
                                        arena.setArenaState(ArenaState.DISABLED);
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " disabled.");

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Enabled", false);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "disable <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // FORCE START
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("forcestart")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_FORCE_START)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState().equals(ArenaState.ENABLED) && arena.getGameState().equals(GameState.WAITING)){
                                        if(arena.getPlayers().size() >= 2){
                                            arena.forceStart();
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arenaName +  ChatColor.WHITE + " has been force-started!");
                                        } else {
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You can only force-start an arena if it has at least two players.");
                                        }
                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You can only force-start an arena that is enabled and is waiting.");
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "forcestart <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // FORCE STOP
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("forcestop")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_FORCE_STOP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState().equals(ArenaState.ENABLED) && arena.getGameState().equals(GameState.STARTED)){
                                        arena.reset();
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arenaName +  ChatColor.WHITE + " has been force-stopped!");
                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You can only force-stop an arena that is enabled and is in-game.");
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "forcestop <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // SET MAIN LOBBY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setmainlobby")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 1){

                                Location location = player.getLocation();

                                Global.INSTANCE.setLobby(location);

                                FileConfiguration configuration = ConfigurationManager.INSTANCE.getConfiguration();
                                configuration.set("Game.Lobby.World", location.getWorld().getName());
                                configuration.set("Game.Lobby.x", location.getX());
                                configuration.set("Game.Lobby.y", location.getY());
                                configuration.set("Game.Lobby.z", location.getZ());

                                ConfigurationManager.INSTANCE.saveConfiguration();

                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Main Lobby set to " + ChatColor.GOLD + Global.INSTANCE.getStringLobby());

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "setmainlobby");
                            }

                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // SET LOBBY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("lobby")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                            } else {

                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                Location lobby = player.getLocation();
                                arena.setLobby(lobby);

                                commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Lobby of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getLobbyString());

                                FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.World", lobby.getWorld().getName());
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.x", lobby.getBlockX());
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.y", lobby.getBlockY());
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.z", lobby.getBlockZ());

                                ConfigurationManager.INSTANCE.saveArenaConfiguration();
                            }

                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // POS1
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("pos1")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    Location location = player.getLocation().clone().subtract(0, 1, 0);

                                    if(arena.canSetPos1(location)){
                                        arena.setPos1(location);

                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Pos1 of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getStringPos1());

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position1.World", location.getWorld().getName());
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position1.x", location.getBlockX());
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position1.y", location.getBlockY());
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position1.z", location.getBlockZ());

                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    } else {
                                        commandSender.sendMessage(Strings.ERROR_POS_SAME_WORLD);
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "pos1 <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // POS2
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("pos2")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    Location location = player.getLocation().clone().subtract(0, 1, 0);

                                    if(arena.canSetPos2(location)){
                                        arena.setPos2(location);

                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Pos2 of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getStringPos2());

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position2.World", location.getWorld().getName());
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position2.x", location.getBlockX());
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position2.y", location.getBlockY());
                                        arenaConfiguration.set("Arenas." + arenaName + ".Position2.z", location.getBlockZ());

                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    } else {
                                        commandSender.sendMessage(Strings.ERROR_POS_SAME_WORLD);
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "pos2 <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // MINPLAYERS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setminplayers")) {

                        if (player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)) {
                            if (strings.length == 3) {

                                String arenaName = strings[1];
                                String number = strings[2];
                                boolean isInteger = NumberUtils.isInteger(number);

                                if (isInteger) {
                                    int amount = Integer.parseInt(strings[2]);

                                    if (ArenaManager.INSTANCE.getArena(arenaName) == null) {
                                        commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                    } else {

                                        if (amount <= 1) {
                                            commandSender.sendMessage(Strings.ERROR_MIN_MORE_THAN_ONE);
                                        } else {
                                            Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                            arena.setMinPlayers(amount);
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Minimum number of players for " + ChatColor.WHITE + arenaName
                                                    + ChatColor.GREEN + " set to " + amount);

                                            FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                            arenaConfiguration.set("Arenas." + arenaName + ".MinimumPlayers", amount);
                                            ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                        }
                                    }

                                } else {
                                    commandSender.sendMessage(Strings.ERROR_INVALID_NUMBER(number));
                                }

                                return true;

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "setminplayers <arena> <amount>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // MAXPLAYERS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setmaxplayers")){

                        if (player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)) {
                            if(strings.length == 3){

                                String arenaName = strings[1];
                                String number = strings[2];
                                boolean isInteger = NumberUtils.isInteger(number);

                                if(isInteger){
                                    int amount = Integer.parseInt(strings[2]);

                                    if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                        commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                    } else {

                                        if(amount <= 1){
                                            commandSender.sendMessage(Strings.ERROR_MAX_MORE_THAN_ONE);
                                        }

                                        Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                        arena.setMaxPlayers(amount);
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Maximum number of players for " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " set to " + amount);

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".MaximumPlayers", amount);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    }

                                } else {
                                    commandSender.sendMessage(Strings.ERROR_INVALID_NUMBER(number));
                                }

                                return true;

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "setmaxplayers <arena> <amount>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // JOIN
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("join")){

                        if (player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER) ||
                                player.hasPermission(Strings.PERMISSION_JOIN)) {

                            if(strings.length == 2){

                                boolean playerAlreadyInGame = false;

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    if(arena.playerInsideArena(player)){
                                        commandSender.sendMessage(Strings.ERROR_ALREADY_IN_ARENA);
                                        playerAlreadyInGame = true;
                                        break;
                                    }
                                }

                                if(!playerAlreadyInGame){

                                    if(Global.INSTANCE.isLobbyValid()) {
                                        String arenaName = strings[1];

                                        if(ArenaManager.INSTANCE.getArenas().containsKey(arenaName)){
                                            Arena arena = ArenaManager.INSTANCE.getArenas().get(arenaName);

                                            if(arena.isArenaValid()){

                                                if(arena.joinable()){
                                                    if(arena.getPlayers().size() < arena.getMaxPlayers()){
                                                        arena.join(player);
                                                    } else {
                                                        commandSender.sendMessage(Strings.ERROR_ARENA_FULL(arenaName));
                                                    }
                                                } else {
                                                    commandSender.sendMessage(Strings.ERROR_ARENA_STARTED(arenaName));
                                                }
                                            } else {
                                                commandSender.sendMessage(Strings.ERROR_ARENA_INVALID(arenaName));
                                            }

                                        } else {
                                            commandSender.sendMessage(Strings.ERROR_ARENA_DOES_NOT_EXIST(arenaName));
                                        }

                                    } else{
                                        commandSender.sendMessage(Strings.LOBBY_INVALID);
                                    }
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "join <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // LEAVE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("leave")){

                        if (player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER)||
                                player.hasPermission(Strings.PERMISSION_LEAVE)) {

                            if(strings.length == 1){

                                boolean playerInsideArena = false;

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    if(arena.playerInsideArena(player)){

                                        arena.leave(player);
                                        playerInsideArena = true;
                                        break;
                                    }
                                }

                                if(!playerInsideArena){
                                    commandSender.sendMessage(Strings.ERROR_PLAYER_NOT_JOINED_ARENA);
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "leave");
                            }
                        } else {
                            commandSender.sendMessage(Strings.ERROR_INSUFFICIENT_PERMISSION);
                        }

                        return true;
                    }

                    // No such command exists
                    else {
                        commandSender.sendMessage( Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + usage + ChatColor.RED + " is not a valid command");
                    }

                }

            }

        } else {
            commandSender.sendMessage(Strings.ERROR_COMMAND_ONLY_PLAYER);
        }

        return true;
    }

}