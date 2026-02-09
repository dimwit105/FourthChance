package com.zezdathecrystaldragon.com.fourthChance;

import com.tcoded.folialib.FoliaLib;
import com.zezdathecrystaldragon.com.fourthChance.config.ConfigurationManager;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayerManager;
import com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners.EventListenerManager;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class FourthChance extends JavaPlugin {

    public static FourthChance PLUGIN;
    public static ConfigurationManager CONFIG;
    public static DownedPlayerManager DOWNED_PLAYERS;
    private FoliaLib foliaLib;


    @Override
    public void onEnable()
    {
        PLUGIN = this;
        CONFIG = new ConfigurationManager();
        CONFIG.loadConfig();
        DOWNED_PLAYERS = new DownedPlayerManager();
        foliaLib = new FoliaLib(this);
        new EventListenerManager();

    }

    @Override
    public void onDisable()
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            DownedPlayer dp = DOWNED_PLAYERS.downedPlayers.get(p);
            if(dp != null)
                dp.onPluginDisable();
        }
    }
    public FoliaLib getFoliaLib()
    {
        return foliaLib;
    }
}
