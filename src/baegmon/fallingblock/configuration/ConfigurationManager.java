package baegmon.fallingblock.configuration;

import baegmon.fallingblock.BlockPlugin;
import baegmon.fallingblock.game.Arena;
import baegmon.fallingblock.game.ArenaSign;
import baegmon.fallingblock.game.Global;
import baegmon.fallingblock.manager.ArenaManager;
import baegmon.fallingblock.tools.NumberUtils;
import baegmon.fallingblock.tools.SignType;
import baegmon.fallingblock.tools.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum ConfigurationManager {

    INSTANCE;

    private static BlockPlugin plugin = BlockPlugin.getPlugin(BlockPlugin.class);

    private FileConfiguration configuration;
    private FileConfiguration arenaConfiguration;

    private File arenaFile;
    private File configFile;

    public void setup(){

        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");
        arenaFile = new File(plugin.getDataFolder(), "arenas.yml");

        if(!configFile.exists()){
            try{
                configFile.createNewFile();
            } catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Could not create config.yml");
            }
        }

        if(!arenaFile.exists()){
            try{
                arenaFile.createNewFile();
            } catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Could not create arenas.yml");
            }
        }

        configuration = YamlConfiguration.loadConfiguration(configFile);
        arenaConfiguration = YamlConfiguration.loadConfiguration(arenaFile);

        if(configuration.contains("Game")){
            World mainLobby = Bukkit.getWorld(configuration.getString("Game.Lobby.World"));

            if(mainLobby != null){
                Global.INSTANCE.setLobby(
                        new Location(mainLobby,
                                configuration.getInt("Game.Lobby.x"),
                                configuration.getInt("Game.Lobby.y"),
                                configuration.getInt("Game.Lobby.z")
                        )
                );
            }

            if(configuration.contains("Game.Signs")){
                for(String key : configuration.getConfigurationSection("Game.Signs").getKeys(false)){
                    boolean flag = NumberUtils.isInteger(key);

                    if(flag){

                        ArenaSign sign = new ArenaSign(Integer.parseInt(key));

                        String type = configuration.getString("Game.Signs." + key + ".Type");

                        if(type.equalsIgnoreCase("LEAVE")){
                            sign.setType(SignType.LEAVE);
                            sign.setWorld(configuration.getString("Game.Signs." + key + ".World"));
                            sign.setX(configuration.getInt("Game.Signs." + key + ".x"));
                            sign.setY(configuration.getInt("Game.Signs." + key + ".y"));
                            sign.setZ(configuration.getInt("Game.Signs." + key + ".z"));
                        } else if (type.equalsIgnoreCase("JOIN")){
                            sign.setType(SignType.JOIN);
                            sign.setWorld(configuration.getString("Game.Signs." + key + ".World"));
                            sign.setArena(configuration.getString("Game.Signs." + key + ".Arena"));
                            sign.setX(configuration.getInt("Game.Signs." + key + ".x"));
                            sign.setY(configuration.getInt("Game.Signs." + key + ".y"));
                            sign.setZ(configuration.getInt("Game.Signs." + key + ".z"));
                        }

                        Global.INSTANCE.addSign(sign);
                    }

                }
            }

        } else {
            saveConfiguration();
        }

        if(arenaConfiguration.contains("Arenas")){
            for(String key : arenaConfiguration.getConfigurationSection("Arenas").getKeys(false)){

                Arena arena = new Arena(key);

                arena.setArenaState(arenaConfiguration.getBoolean("Arenas." + key + ".Enabled"));
                arena.setMinPlayers(arenaConfiguration.getInt("Arenas." + key + ".MinimumPlayers"));
                arena.setMaxPlayers(arenaConfiguration.getInt("Arenas." + key + ".MaximumPlayers"));

                if(arenaConfiguration.contains("Arenas." + key + ".Lobby")){

                    World lobby = Bukkit.getWorld(arenaConfiguration.getString("Arenas." + key + ".Lobby.World"));
                    if(lobby != null){
                        arena.setLobby(
                                new Location(lobby,
                                        arenaConfiguration.getDouble("Arenas." + key + ".Lobby.x"),
                                        arenaConfiguration.getDouble("Arenas." + key + ".Lobby.y"),
                                        arenaConfiguration.getDouble("Arenas." + key + ".Lobby.z")
                                )
                        );
                    }
                }

                World pos1 = Bukkit.getWorld(arenaConfiguration.getString("Arenas." + key + ".Position1.World"));
                if(pos1 != null){
                    arena.setPos1(
                            new Location(pos1,
                                    arenaConfiguration.getInt("Arenas." + key + ".Position1.x"),
                                    arenaConfiguration.getInt("Arenas." + key + ".Position1.y"),
                                    arenaConfiguration.getInt("Arenas." + key + ".Position1.z")
                            )
                    );
                }

                World pos2 = Bukkit.getWorld(arenaConfiguration.getString("Arenas." + key + ".Position2.World"));
                if(pos2 != null){
                    arena.setPos2(
                            new Location(pos2,
                                    arenaConfiguration.getInt("Arenas." + key + ".Position2.x"),
                                    arenaConfiguration.getInt("Arenas." + key + ".Position2.y"),
                                    arenaConfiguration.getInt("Arenas." + key + ".Position2.z")
                            )
                    );
                }

                arena.setWait(arenaConfiguration.getInt("Arenas." + key + ".LobbyTime"));

                arena.saveArenaBlocks();

                ArenaManager.INSTANCE.addArena(arena.getName(), arena);

            }
        }

        for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
            if(arena != null){
                arena.updateSigns();
            }
        }

        Bukkit.getServer().getConsoleSender().sendMessage( Strings.PREFIX + ChatColor.GREEN + "Plugin configuration file (config.yml) was loaded!");
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.GREEN + "Arenas configuration file (arenas.yml) was loaded!");
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public FileConfiguration getArenaConfiguration() {
        return arenaConfiguration;
    }

    public void saveConfiguration(){
        try {
            configuration.save(configFile);
        } catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Plugin configuration file (config.yml) could not be saved!");
        }
    }

    public void saveArenaConfiguration(){
        try {
            arenaConfiguration.save(arenaFile);
        } catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Arenas configuration file (arenas.yml) could not be saved!");
        }
    }
}
