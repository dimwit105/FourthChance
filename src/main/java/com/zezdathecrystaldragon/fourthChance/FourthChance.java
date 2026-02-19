package com.zezdathecrystaldragon.fourthChance;

import com.tcoded.folialib.FoliaLib;
import com.zezdathecrystaldragon.fourthChance.config.ConfigurationManager;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayerManager;
import com.zezdathecrystaldragon.fourthChance.downedplayer.tasks.AbsorptionReviveTask;
import com.zezdathecrystaldragon.fourthChance.downedplayer.tasks.RevivingPlayerTask;
import com.zezdathecrystaldragon.fourthChance.events.eventlisteners.EventListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class FourthChance extends JavaPlugin {

    public static FourthChance PLUGIN;
    public static ConfigurationManager CONFIG;
    public static DownedPlayerManager DOWNED_PLAYERS;
    public static final Random RANDOM = new Random();
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
        getFoliaLib().getScheduler().cancelAllTasks();
        AbsorptionReviveTask.onDisable();
        RevivingPlayerTask.onDisable();

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
