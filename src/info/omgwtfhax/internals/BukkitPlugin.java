package info.omgwtfhax.internals;

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
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin{

	private JexlEngine jexl = new JexlEngine();
	private Map<String,Object> values = new HashMap<String, Object>();
	
	private Vault vault;
	
	public void onEnable()
	{
		vault = new Vault(this);		
		
		vault.setupPermissions();
		vault.setupEconomy();
		vault.setupChat();
		
		values.put("System", System.class);
		values.put("Bukkit", Bukkit.class);
		values.put("vault", vault);
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
		
		Bukkit.getPluginManager().registerEvents(new ScriptListener(this), this);
		
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
	
	/**
	 * Retrieves this plugin's JexlEngine. There is nothing special about it.
	 * 
	 * @return This plugin's jexl engine.
	 * */
	public JexlEngine getEngine()
	{
		return jexl;
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
				
				Object value = getEngine().createScript(code).execute(getContext());
				
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
