package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.events.PlayerDownedEvent;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DuplicateDataException;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    @EventHandler
    public void onPlayerDamagedWhileDown(EntityDamageByEntityEvent event)
    {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getEntity();

        DownedPlayer dp = PDCUtil.getDownedPlayerData(p);
        if(dp == null)
            return;
        double multiplier = FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Incoming");
        if(multiplier == 0D)
        {
            event.setCancelled(true);
            return;
        }
        event.setDamage(event.getDamage() * FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Incoming"));
    }

    @EventHandler
    public void onPlayerAttackingWhileDown(EntityDamageByEntityEvent event)
    {

        if(event.getDamager().getType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getDamager();

        DownedPlayer dp = PDCUtil.getDownedPlayerData(p);
        if(dp == null)
            return;
        double multiplier = FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Outgoing");
        if(multiplier == 0D)
        {
            event.setCancelled(true);
            return;
        }
        event.setDamage(event.getDamage() * FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Outgoing"));
    }
}
