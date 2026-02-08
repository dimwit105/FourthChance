package com.zezdathecrystaldragon.com.fourthChance.events;

import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DuplicateDataException;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamagedEventListener implements Listener
{
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getEntity();
        if(p.getHealth() - event.getFinalDamage() > 0)
            return;

        PlayerDownedEvent pde = new PlayerDownedEvent(p);
        Bukkit.getPluginManager().callEvent(pde);
        if(pde.isCancelled())
            return;

        event.setCancelled(true);
        DownedPlayer dp = PDCUtil.getDownedPlayerData(p);
        if(dp != null && !dp.isDowned())
        {
            dp.incapacitate(event);
        }
        else if (dp == null)
        {
            try {
                dp = new DownedPlayer(p.getUniqueId(), event);
            } catch (DuplicateDataException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
