package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.util.ReviveReason;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

import java.util.logging.Level;

public class PlayerRessurrectEventListener implements Listener
{
    @EventHandler
    public void onTotem(EntityResurrectEvent event)
    {
        FourthChance.PLUGIN.getLogger().log(Level.WARNING, event.getEntity().getName() + " has a resurrection call! Cancelled: " + event.isCancelled() );

        if(event.getEntity() instanceof Player p && !event.isCancelled())
        {
            if(FourthChance.DOWNED_PLAYERS.isDowned(p))
                FourthChance.DOWNED_PLAYERS.downedPlayers.get(p).revive(ReviveReason.HEAL);
        }
    }
}
