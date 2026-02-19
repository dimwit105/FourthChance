package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.events.PlayerDownedEvent;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DuplicateDataException;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.logging.Level;

public class PlayerDamagedEventListener implements Listener
{
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getEntity();
        FourthChance.PLUGIN.getLogger().log(Level.WARNING, p.getDisplayName() + " was damaged!");
        if(p.getHealth() - event.getFinalDamage() > 0)
            return;

        PlayerDownedEvent pde = new PlayerDownedEvent(p);
        Bukkit.getPluginManager().callEvent(pde);
        if(pde.isCancelled())
            return;

        DownedPlayer dp = FourthChance.DOWNED_PLAYERS.downedPlayers.get(p);
        if (dp == null)
        {
            try {
                dp = new DownedPlayer(p.getUniqueId(), event);
            } catch (DuplicateDataException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            if(!dp.isDowned()) {
                dp.incapacitate(event);
            }
        }
    }

    @EventHandler
    public void onPlayerDamagedWhileDown(EntityDamageByEntityEvent event)
    {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getEntity();

        if(!FourthChance.DOWNED_PLAYERS.isDowned(p))
            return;
        double multiplier = FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Incoming");
        if(multiplier == 0D)
        {
            event.setCancelled(true);
            return;
        }
        if(event.getDamager() instanceof LivingEntity le)
        {
            for (Entity e : p.getNearbyEntities(21,21,21))
            {
                if(e instanceof PigZombie pz)
                {
                    pz.setAngry(true);
                    pz.setTarget(le);
                }
            }
        }

        event.setDamage(event.getDamage() * FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Incoming"));
    }

    @EventHandler
    public void onPlayerAttackingWhileDown(EntityDamageByEntityEvent event)
    {

        if(event.getDamager().getType() != EntityType.PLAYER)
            return;
        Player p = (Player) event.getDamager();

        if(!FourthChance.DOWNED_PLAYERS.isDowned(p))
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
