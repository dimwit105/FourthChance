package com.zezdathecrystaldragon.com.fourthChance;

import com.tcoded.folialib.FoliaLib;
import com.zezdathecrystaldragon.com.fourthChance.config.ConfigurationManager;
import com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners.EventListenerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FourthChance extends JavaPlugin {

    public static FourthChance PLUGIN;
    public static ConfigurationManager CONFIG;
    private FoliaLib foliaLib;


    @Override
    public void onEnable()
    {
        PLUGIN = this;
        CONFIG = new ConfigurationManager();
        CONFIG.loadConfig();
        foliaLib = new FoliaLib(this);
        new EventListenerManager();
    }

    @Override
    public void onDisable()
    {
    }
    public FoliaLib getFoliaLib()
    {
        return foliaLib;
    }
}
