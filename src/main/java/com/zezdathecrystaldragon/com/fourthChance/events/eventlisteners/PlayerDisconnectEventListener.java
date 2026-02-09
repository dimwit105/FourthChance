package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectEventListener implements Listener
{
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event)
    {
        DownedPlayer dp = FourthChance.DOWNED_PLAYERS.downedPlayers.get(event.getPlayer());
        if(dp == null)
            return;
        dp.onPlayerDisconnect();
    }
}
