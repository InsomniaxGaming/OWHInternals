package info.omgwtfhax.internals;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

	/**
	 * Base permission node for this plugin, generated from the plugin name and trimed out whitespace
	 * */
	private static String BASE_NODE;
	
	/**Admin node -- a simple extention of the base node to give to administrators (if your plugin uses it.)*/
	private static String PERMISSION_NODE_ADMINISTRATOR;
	
	BukkitPlugin myPlugin;
	public static Permission permission = null;
	
	public static Economy economy = null;
	
	public static Chat chat = null;
	
	public boolean failed;
	
	public Vault(BukkitPlugin instance)
	{
		myPlugin = instance;
		
		BASE_NODE = instance.getName().replace(" ", "").toLowerCase();
		PERMISSION_NODE_ADMINISTRATOR = BASE_NODE + ".administrator";
	}
	
	public static synchronized boolean has(Player player, String perm)
	{
		return permission.has(player, perm);
	}
	
	/**
	 * Initialization method to setup the permissions for this plugin. Must be executed prior to using any permission checking methods.
	 * */
	public boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = myPlugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        failed = !(permission != null);
        return (permission != null);
    }

    public boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = myPlugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    public boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = myPlugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public String getPrimaryGroup(String player)
	{
		return permission.getPrimaryGroup(player,null);
	}
	
	/**
	 * Check if the user has a specific node.
	 * 
	 * @param	sender			the user to check for permission.
	 * @param	node			the node to check for permission.
	 * @param	allowConsole	whether to allow console access, in the event that this is a ConsoleCommandSender.
	 * */
	public boolean has(CommandSender sender, String node, boolean allowConsole)
	{
		if(sender instanceof ConsoleCommandSender)
			return allowConsole;
		
		return permission.has(sender, node);
	}
	
	/**
	 * Check if the user has a command node, based on this plugin's generated permissions.
	 * 
	 * @param	sender			the user to check for permission.
	 * @param	command			the command to check for permission.
	 * @param	allowConsole	whether to allow console access, in the event that this is a ConsoleCommandSender.
	 * */
	public boolean has(CommandSender sender, Command command, boolean allowConsole)
	{
		if(sender instanceof ConsoleCommandSender)
			return allowConsole;
		if(failed)
			return ((Player)sender).isOp();
		
		return permission.has(sender, getBaseNode() + ".commands." + command.getName());
	}

	/**
	 * Check if the user has a command node, based on this plugin's generated permissions.
	 * 
	 * @param	sender			the user to check for permission.
	 * @param	command			the command to check for permission.
	 * @param	args			args to add as a sub-node of this command.
	 * @param	allowConsole	whether to allow console access, in the event that this is a ConsoleCommandSender.
	 * */
	public boolean has(CommandSender sender, Command command, String[] args, boolean allowConsole)
	{
		if(sender instanceof ConsoleCommandSender)
			return allowConsole;
		if(failed)
			return ((Player)sender).isOp();
		
		return permission.has(sender, getBaseNode() + ".commands." + command.getName() + "." + StringUtils.join(args).toLowerCase());
	}
	
	public Permission getPermissions()
	{
		return permission;
	}

	public static String getBaseNode()
	{
		return BASE_NODE;
	}
	
	public static String getAdminNode()
	{
		return PERMISSION_NODE_ADMINISTRATOR;
	}
}
