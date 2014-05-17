package info.omgwtfhax.internals;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
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
		values.put("Color", Color.class);
		values.put("ChatColor", ChatColor.class);
		values.put("Effect", Effect.class);
		values.put("Enchantment", Enchantment.class);
		values.put("Sound", Sound.class);
		values.put("Instrument", org.bukkit.Instrument.class);
		
		Bukkit.getPluginManager().registerEvents(new ScriptListener(this), this);
		
	}
	
	/**
	 * Creates a new context, using the current plugin's map
	 * 
	 * @return A new context using the values from getMappedValues()
	 * */
	public MapContext getContext()
	{
		return new MapContext(getMappedValues());
	}
	
	/**
	 * Returns the map containing all values used by this plugin's MapContext.
	 * */
	public Map<String, Object> getMappedValues()
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

}
