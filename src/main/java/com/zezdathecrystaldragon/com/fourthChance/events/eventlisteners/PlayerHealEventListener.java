package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class PlayerHealEventListener implements Listener
{
    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent event)
    {
        if(event.getEntityType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getEntity();
        if(FourthChance.DOWNED_PLAYERS.isDowned(p))
        {
            switch (event.getRegainReason())
            {
                case MAGIC:
                case MAGIC_REGEN:
                {break;}
                default: {event.setCancelled(true); break;}
            }
        }
    }
}
