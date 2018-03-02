package baegmon.fallingblock.tools;

import org.bukkit.ChatColor;

public final class Strings {

    public static final String CREATOR = ChatColor.GOLD + "baegmon@gmail.com";
    public static final String VERSION = "1.0.0";
    public static final String PREFIX = ChatColor.GREEN + "[ " + ChatColor.GOLD +  "FallingBlocks" + ChatColor.GREEN+ " ] ";
    public static final String COMMAND_PREFIX = ChatColor.GOLD +  "/FallingBlock ";

    public static final String LOBBY_INVALID = PREFIX + ChatColor.RED + "The lobby has not been set or is currently invalid. Contact an administrator to fix this issue.";

    public static final String SIGN_REMOVED = PREFIX + ChatColor.WHITE + "Sign has been removed successfully!";

    public static final String INCORRECT_ARGUMENTS = Strings.PREFIX + ChatColor.RED + "Error: Incorrect Arguments";

    public static final String ERROR_INSUFFICIENT_PERMISSION = PREFIX + ChatColor.RED + "You do not have the permission to run this command.";
    public static final String ERROR_INSUFFICIENT_SIGN_PERMISSION = Strings.PREFIX + ChatColor.RED + "You do not have the permission to use this sign";

    public static final String ERROR_COMMAND_ONLY_PLAYER = PREFIX + ChatColor.RED + "Commands can only be executed as a player!";
    public static final String ERROR_PLAYER_NOT_JOINED_ARENA = PREFIX + ChatColor.RED + "Error: You have not joined an arena!";

    public static final String ERROR_ALREADY_IN_ARENA = PREFIX + ChatColor.RED + "Error: You cannot join an arena while inside of an arena!";

    public static final String ERROR_MIN_MORE_THAN_ONE = PREFIX + ChatColor.RED + "Error: Minimum number of players must be greater than 1.";
    public static final String ERROR_MAX_MORE_THAN_ONE = PREFIX + ChatColor.RED + "Error: Maximum number of players must be greater than 1.";

    public static final String ERROR_POS_SAME_WORLD = PREFIX + ChatColor.RED + "Error: Arena positions must be set in the same world.";

    public static final String SERVER_NAME = "www.server.com";

    public static String ERROR_INVALID_NUMBER(String arg){
        return Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + arg + ChatColor.RED + " is not a valid number.";
    }

    public static String ERROR_ARENA_STARTED(String arenaName){
        return PREFIX + ChatColor.GOLD + arenaName + ChatColor.RED + " has already started!";
    }

    public static String ERROR_ARENA_FULL(String arenaName){
        return PREFIX + ChatColor.GOLD + arenaName + ChatColor.RED + " cannot be joined because it is full!";
    }

    public static String ERROR_ARENA_DOES_NOT_EXIST(String arenaName){
        return PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.";
    }

    public static String ERROR_ARENA_INVALID(String arenaName){
        return PREFIX + ChatColor.GOLD + arenaName + ChatColor.RED + " cannot be started because it is currently invalid!";
    }

    // PERMISSION
    public static final String PERMISSION_ALL = "fallingblock.*";
    public static final String PERMISSION_ADMIN = "fallingblock.admin.*";
    public static final String PERMISSION_PLAYER = "fallingblock.player.*";

    // INDIVIDUAL PLAYER PERMISSION
    public static final String PERMISSION_JOIN = "fallingblock.player.join";
    public static final String PERMISSION_LEAVE = "fallingblock.player.leave";
    public static final String PERMISSION_LIST = "fallingblock.player.list";

    // INDIVIDUAL ADMIN PERMISSION
    public static final String PERMISSION_FORCE_STOP = "fallingblock.admin.forcestop";
    public static final String PERMISSION_FORCE_START = "fallingblock.admin.forcestart";
    public static final String PERMISSION_ARENA_SETUP = "fallingblock.admin.setup";

    // SIGN PERMISSION
    public static final String PERMISSION_USE_SIGN = "fallingblock.player.sign";

}

