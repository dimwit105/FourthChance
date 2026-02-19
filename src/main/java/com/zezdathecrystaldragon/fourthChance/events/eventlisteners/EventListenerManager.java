package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EventListenerManager
{
    public EventListenerManager()
    {
        Plugin plugin = FourthChance.PLUGIN;

        Bukkit.getPluginManager().registerEvents(new PlayerDamagedEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerRevivingPlayerEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDisconnectEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerMobTargetEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerHealEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerAddPotionEffectEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerUseItemEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerRessurrectEventListener(), plugin);

    }
}
