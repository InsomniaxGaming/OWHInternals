package info.omgwtfhax.internals;

import info.omgwtfhax.listener.ScriptListener;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin{

	private JexlEngine jexl = new JexlEngine();
	private Map<String,Object> values = new HashMap<String, Object>();
	
	String bracketRegex = "[\\{\\}\\<\\>.\\[\\]\\(\\);*/%+-]{1}";
	
	String openBracket; //Open bracket for code
	String closeBracket; //Close bracket for code

	String BASE_NODE;
	String PERMISSION_NODE_ADMINISTRATOR;

	Listener listener;
	
	public void onEnable()
	{
		this.saveDefaultConfig();

		values.put("System", System.class);
		values.put("Bukkit", Bukkit.class);
		values.put("logger", this.getLogger());
		values.put("String", String.class);
		values.put("StringUtils", StringUtils.class);
		values.put("ChatColor", ChatColor.class);
		values.put("Effect", Effect.class);
		values.put("Enchantment", Enchantment.class);
		values.put("Sound", Sound.class);
		values.put("Instrument", org.bukkit.Instrument.class);
		values.put("Material", Material.class);
		values.put("this", this);
		values.put("Egg", Egg.class);
		values.put("Snowball", Snowball.class);

		BASE_NODE = getName().replace(" ", "").toLowerCase();
		PERMISSION_NODE_ADMINISTRATOR = BASE_NODE + ".administrator";

		listener = new ScriptListener(this);
		Bukkit.getPluginManager().registerEvents(listener, this);
		
		this.setOpenBracket(getConfig().getString("openbracket"));
		this.setCloseBracket(getConfig().getString("closebracket"));
		
	}
	
	public void setOpenBracket(String bracket)
	{
		if(bracket.matches(bracketRegex))
		{
			this.getLogger().warning("Opening bracket '"+bracket+"' is already in use by the java compiler. Falling back to '{>' to avoid conflict.");
			this.openBracket = "{>";
		}
		else
		{
			this.openBracket = bracket;
		}
	}
	
	public void setCloseBracket(String bracket)
	{
		if(bracket.matches(bracketRegex))
		{
			this.getLogger().warning("Closing bracket '"+bracket+"' is already in use by the java compiler. Falling back to '<}' to avoid conflict.");
			this.closeBracket = "<}";
		}
		else
		{
			this.closeBracket = bracket;
		}
	}
	
	public String getOpenBracket()
	{
		return this.openBracket;
	}
	
	public String getCloseBracket()
	{
		return this.closeBracket;
	}
	
	public Listener getScriptListener()
	{
		return listener;
	}
	
	public void setScriptListener(Listener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Creates a new context, using the current plugin's map
	 * 
	 * @return A new context using the values from getMap()
	 * */
	public MapContext getContext()
	{
		return new MapContext(getMap());
	}
	
	/**
	 * Returns the map containing all values used by this plugin's MapContext.
	 * */
	public Map<String, Object> getMap()
	{
		return values;
	}
	
	public void add(String key, Object value)
	{
		getMap().put(key, value);
	}
	
	/**
	 * Retrieves this plugin's JexlEngine. There is nothing special about it.
	 * 
	 * @return This plugin's jexl engine.
	 * */
	public JexlEngine getEngine()
	{
		return jexl;
	}
	
	protected String parse(String player, String s) throws JexlException
	{
		String message = s;
		
		int openBracketIndex = -1;
		int closeBracketIndex = -1;
		
		while((openBracketIndex = message.indexOf(openBracket)) != -1)
		{
			if((closeBracketIndex = message.indexOf(closeBracket)) > openBracketIndex)
			{
				String code = message.substring(openBracketIndex+2,closeBracketIndex);
				Bukkit.getLogger().info("Parsing JEXL code issued by " + player + ": " + code);
				
				Object value;
				
				value = getEngine().createScript(code).execute(getContext());
				
				if(value == null)
					value = "";
				
				s = s.replace(openBracket+code+closeBracket, value.toString());
				message = message.substring(closeBracketIndex+2);
			}
			else
			{
				break;
			}
		}
		return s;
	}
	
	public String syncParse(String player, String s)
	{
		ScriptRunnable runnable = new ScriptRunnable(this,player,s);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable);
		
		synchronized(this)
		{
			while(runnable.getResult() == null)
			{
				try
				{
					wait();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		return runnable.getResult().toString();
	}

	public String getAdminNode() {
		return PERMISSION_NODE_ADMINISTRATOR;
	}
}
