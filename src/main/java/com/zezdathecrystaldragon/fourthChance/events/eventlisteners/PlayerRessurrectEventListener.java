package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.util.ReviveReason;

public class PlayerRessurrectEventListener implements Listener
{
    @EventHandler
    public void onTotem(EntityResurrectEvent event)
    {
        if(event.getEntity() instanceof Player p && !event.isCancelled())
        {
            if(FourthChance.DOWNED_PLAYERS.isDowned(p))
                FourthChance.DOWNED_PLAYERS.downedPlayers.get(p).revive(ReviveReason.TOTEM);
        }
    }
}
