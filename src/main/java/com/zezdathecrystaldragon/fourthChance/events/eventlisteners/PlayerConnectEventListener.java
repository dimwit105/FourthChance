package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.fourthChance.util.PDCUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectEventListener implements Listener
{
    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event)
    {
        DownedPlayer dp = PDCUtil.getDownedPlayerData(event.getPlayer());
        if(dp != null)
            dp.onPlayerReconnect();
    }
}
