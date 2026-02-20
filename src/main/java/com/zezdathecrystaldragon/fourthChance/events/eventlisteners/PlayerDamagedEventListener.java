package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DuplicateDataException;
import com.zezdathecrystaldragon.fourthChance.events.PlayerDownedEvent;

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
        
        if(FourthChance.CONFIG.getConfig().getString("GeneralOptions.TotemPriority").equals("BEFORE"))
        {
            EntityResurrectEvent totemMain = new EntityResurrectEvent(p, EquipmentSlot.HAND);
            EntityResurrectEvent totemOff = new EntityResurrectEvent(p, EquipmentSlot.OFF_HAND);
            Bukkit.getPluginManager().callEvent(totemMain);
            Bukkit.getPluginManager().callEvent(totemOff);
            if(!totemMain.isCancelled() || !totemOff.isCancelled())
                return;
        }

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
        if(!(event instanceof EntityDamageByEntityEvent))
        {
            event.setDamage(FourthChance.CONFIG.getConfig().getDouble("DownedOptions.Damage.Environmental") * event.getDamage());
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
        if(event.getDamager() instanceof LivingEntity le && FourthChance.CONFIG.isPartyMode())
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
