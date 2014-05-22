package info.omgwtfhax.internals;

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
			result = plugin.parse(player, code);	
		
			if(result == null)
				result = "";
			
			plugin.notifyAll();
		}
	}
	
	public Object getResult()
	{
		return result;
	}

}
