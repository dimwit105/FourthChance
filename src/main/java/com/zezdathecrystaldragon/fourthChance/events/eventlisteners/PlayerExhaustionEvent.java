package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;

public class PlayerExhaustionEvent implements Listener
{
    @EventHandler
    public void onPlayerExhaustion(EntityExhaustionEvent event)
    {
        if(event.getEntity() instanceof Player p && FourthChance.DOWNED_PLAYERS.isDowned(p))
        {
            event.setCancelled(true);
        }
    }
}
