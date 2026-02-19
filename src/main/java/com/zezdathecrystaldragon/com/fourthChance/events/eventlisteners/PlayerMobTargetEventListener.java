package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class PlayerMobTargetEventListener implements Listener
{
    @EventHandler
    public void onPlayerTargeted(EntityTargetLivingEntityEvent event)
    {
        if(event.getTarget() == null)
            return;
        if(event.getTarget().getType() != EntityType.PLAYER)
            return;
        Player p = (Player)event.getTarget();
        if(FourthChance.DOWNED_PLAYERS.isDowned(p) && FourthChance.CONFIG.getConfig().getBoolean("DownedOptions.MobInvisibility")) {
            event.setCancelled(true);
            event.setTarget(null);
        }
    }
}
