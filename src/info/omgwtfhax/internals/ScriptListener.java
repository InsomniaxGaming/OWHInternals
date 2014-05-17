package info.omgwtfhax.internals;

import org.apache.commons.jexl2.JexlException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ScriptListener implements Listener{
	
	BukkitPlugin plugin;
	
	public ScriptListener(BukkitPlugin instance)
	{
		plugin = instance;
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e)
	{
		if(Vault.permission.has(e.getPlayer(), Vault.getAdminNode()))
		{
			try
			{
				String parsed = plugin.parse(e.getPlayer().getName(), e.getMessage());
				if(parsed.equals(""))
					e.setCancelled(true);
				else
					e.setMessage(parsed);
			}
			catch(Exception ex)
			{
				e.getPlayer().sendMessage(ex.getMessage());
				e.setCancelled(true);
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e)
	{
		if(Vault.permission.has(e.getPlayer(), Vault.getAdminNode()))
		{
			try
			{
				String parsed = plugin.parse(e.getPlayer().getName(), e.getMessage());
				if(parsed.equals("/"))
					e.setCancelled(true);
				else
					e.setMessage(parsed);
			}
			catch(Exception ex)
			{
				e.getPlayer().sendMessage(ex.getMessage());
				e.setCancelled(true);
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onCommand(ServerCommandEvent e)
	{
		try
		{
			e.setCommand(plugin.parse(e.getSender().getName(), e.getCommand()));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			e.setCommand("");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		//Add a reference to this player in the map as $playername.
		plugin.getMap().put("$"+e.getPlayer().getName(), e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		//Remove player's reference from the map.
		plugin.getMap().remove("$"+e.getPlayer().getName());
	}
}
