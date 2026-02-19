package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerUseItemEventListener implements Listener
{
    @EventHandler
    public void onPlayerUseItem(PlayerItemConsumeEvent event)
    {
        if(FourthChance.DOWNED_PLAYERS.isDowned(event.getPlayer()))
        {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onUseBucket(PlayerBucketEmptyEvent event)
    {
        if(FourthChance.DOWNED_PLAYERS.isDowned(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onShootArrow(EntityShootBowEvent event)
    {
        if(event.getEntity() instanceof Player p)
        {
            if(FourthChance.DOWNED_PLAYERS.isDowned(p)) {
                if(event.getConsumable() != null)
                {
                    p.getInventory().addItem(event.getConsumable());
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onThrowItem(ProjectileLaunchEvent event)
    {
        if(event.getEntity().getShooter() instanceof Player p)
        {
            if(FourthChance.DOWNED_PLAYERS.isDowned(p))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event)
    {
        if(event.getEntity() instanceof Player p)
        {
            if(FourthChance.DOWNED_PLAYERS.isDowned(p))
                event.setCancelled(true);
        }
    }

}
