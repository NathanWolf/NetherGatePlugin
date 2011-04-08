package com.elmakers.mine.bukkit.plugins.nether.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.magic.Spell;
import com.elmakers.mine.bukkit.nether.spells.Phase;
import com.elmakers.mine.bukkit.nether.spells.Portal;
import com.elmakers.mine.bukkit.nether.spells.Window;
import com.elmakers.mine.bukkit.plugins.nether.NetherGatePlugin;
import com.elmakers.mine.bukkit.plugins.nether.NetherManager;

public class NetherSpellsPlugin extends JavaPlugin
{
    public List<Spell> getSpells()
    {
        if (nether == null) return null;
        
        List<Spell> spells = new ArrayList<Spell>();

        spells.add(new Phase(nether));
        spells.add(new Portal(nether));
        spells.add(new Window(nether));
        
        return spells;
    }
    
    public void onEnable()
    {
        PluginManager pluginManager = getServer().getPluginManager();
        Plugin checkForNether = pluginManager.getPlugin("NetherGate");

        if (checkForNether == null) 
        {
            log.warning("NetherSpells: Requires the NetherGate plugin");
            pluginManager.disablePlugin(this);
            return;
        }
        
        NetherGatePlugin plugin = (NetherGatePlugin)checkForNether;
        nether = plugin.getManager();
    }
    
    public void onDisable()
    {
    }

    protected NetherManager nether = null;
    protected final Logger  log    = Logger.getLogger("Minecraft");
}
