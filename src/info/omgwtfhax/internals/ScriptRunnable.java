package info.omgwtfhax.internals;

import org.bukkit.Bukkit;

public class ScriptRunnable implements Runnable{
	
	BukkitPlugin plugin;
	String code;
	String player;
	
	Object result = null;
	
	public ScriptRunnable(BukkitPlugin instance, String player, String code)
	{
		this.plugin = instance;
		this.player = player;
		this.code 	= code;
	}

	@Override
	public void run() {		
		synchronized(plugin)
		{
			try
			{
			result = plugin.parse(player, code);
			}
			catch(Exception e)
			{
				Bukkit.getPlayer(player).sendMessage(e.getMessage());
				result = "";
			}
			
			plugin.notifyAll();
		}
	}
	
	public Object getResult()
	{
		return result;
	}

}
