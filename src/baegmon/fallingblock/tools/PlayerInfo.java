package baegmon.fallingblock.tools;

import baegmon.fallingblock.manager.PlayerInfoManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInfo {

    private final UUID uuid;
    private int expLevel;
    private float exp;
    private GameMode gameMode;

    private ItemStack[] contents;
    private ItemStack[] armorContents;
    private ItemStack[] extraContents;

    public PlayerInfo(final Player player) {
        this.uuid = player.getUniqueId();
        this.expLevel = player.getLevel();
        this.exp = player.getExp();
        this.gameMode = player.getGameMode();

        this.contents = player.getInventory().getContents().clone();
        this.armorContents = player.getInventory().getArmorContents().clone();
        this.extraContents = player.getInventory().getExtraContents().clone();
    }

    public void restorePlayer(){
        Player player = Bukkit.getPlayer(uuid);

        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armorContents);
        player.getInventory().setExtraContents(extraContents);

        player.setGameMode(gameMode);
        player.setExp(exp);
        player.setLevel(expLevel);

        player.updateInventory();

        PlayerInfoManager.INSTANCE.removeInfo(uuid);
    }

}
