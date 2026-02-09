package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
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
