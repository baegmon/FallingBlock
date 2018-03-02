package baegmon.fallingblock;

import baegmon.fallingblock.command.FallingBlockCommand;
import baegmon.fallingblock.configuration.ConfigurationManager;
import baegmon.fallingblock.listener.ArenaListener;
import baegmon.fallingblock.listener.SignListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockPlugin extends JavaPlugin  {

    @Override
    public void onEnable() {
        this.getCommand("fallingblock").setExecutor(new FallingBlockCommand());
        this.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        this.getServer().getPluginManager().registerEvents(new SignListener(), this);

        // This line allows the server to finish loading all plugins before the configuration files are loaded
        // Allows custom worlds plugins such as Multiverse etc to be compatible
        getServer().getScheduler().scheduleSyncDelayedTask(this, this::loadConfiguration, 20);

    }

    @Override
    public void onDisable() {

    }

    private void loadConfiguration(){
        ConfigurationManager.INSTANCE.setup();
    }

}
