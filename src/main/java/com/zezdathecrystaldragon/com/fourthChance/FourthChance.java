package com.zezdathecrystaldragon.com.fourthChance;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import com.zezdathecrystaldragon.com.fourthChance.config.ConfigurationManager;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayerManager;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.AbsorptionReviveTask;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.CancellableRunnable;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.RevivingPlayerTask;
import com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners.EventListenerManager;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Level;

import static com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.AbsorptionReviveTask.ABSORPTION_BUFF;

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
