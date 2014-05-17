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
				String parsed = parse(e.getPlayer().getName(), e.getMessage());
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
				String parsed = parse(e.getPlayer().getName(), e.getMessage());
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
			e.setCommand(parse(e.getSender().getName(), e.getCommand()));
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
		plugin.getMappedValues().put("$"+e.getPlayer().getName(), e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		//Remove player's reference from the map.
		plugin.getMappedValues().remove("$"+e.getPlayer().getName());
	}
	
	public String parse(String player, String s) throws JexlException
	{
		String message = s;
		
		int openBracketIndex = -1;
		int closeBracketIndex = -1;
		
		while((openBracketIndex = message.indexOf("{>")) != -1)
		{
			if((closeBracketIndex = message.indexOf("<}")) > openBracketIndex)
			{
				String code = message.substring(openBracketIndex+2,closeBracketIndex);
				Bukkit.getLogger().info("Parsing JEXL code issued by " + player + ": " + code);
				
				Object value = plugin.getEngine().createScript(code).execute(plugin.getContext());
				
				if(value == null)
					value = "";
				
				s = s.replace("{>"+code+"<}", value.toString());
				message = message.substring(closeBracketIndex+2);
			}
			else
			{
				break;
			}
		}
		return s;
	}
	

}
