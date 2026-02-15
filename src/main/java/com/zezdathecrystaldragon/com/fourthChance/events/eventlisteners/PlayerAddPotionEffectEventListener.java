package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.DamageUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerAddPotionEffectEventListener implements Listener
{
    @EventHandler
    public void onPlayerPotionEffect(EntityPotionEffectEvent event)
    {
        if(event.getEntityType() != EntityType.PLAYER || event.getCause() == EntityPotionEffectEvent.Cause.PLUGIN)
            return;
        if(event.getAction() != EntityPotionEffectEvent.Action.ADDED && event.getAction() != EntityPotionEffectEvent.Action.CHANGED)
            return;
        if(event.getNewEffect() == null || event.getNewEffect().getType() != PotionEffectType.REGENERATION)
            return;
        Player p = (Player) event.getEntity();

        DownedPlayer dp = FourthChance.DOWNED_PLAYERS.downedPlayers.get(p);
        if(event.getCause() == EntityPotionEffectEvent.Cause.BEACON && !FourthChance.DOWNED_PLAYERS.isDowned(p))
            DamageUtil.removeOneRevivePenaltyAttributeDebuff(p);
        if(dp == null)
            return;
        if(dp.hasRevivingTask() && event.getOldEffect()!= null && event.getOldEffect().getDuration() == PotionEffect.INFINITE_DURATION)
            event.setOverride(true);

    }
}
