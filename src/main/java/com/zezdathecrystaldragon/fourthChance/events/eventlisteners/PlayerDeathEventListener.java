package com.zezdathecrystaldragon.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.logging.Level;

public class PlayerDeathEventListener implements Listener
{
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        FourthChance.PLUGIN.getLogger().log(Level.WARNING, event.getEntity().getDisplayName() + " has died!");

        DownedPlayer dp = FourthChance.DOWNED_PLAYERS.downedPlayers.get(event.getEntity());
        if(dp == null)
            return;

        if(FourthChance.DOWNED_PLAYERS.isDowned(event.getEntity()))
        {
            if(event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM)
            {
                if(dp.getDowningEntity() != null) {
                    event.setDeathMessage(FourthChance.CONFIG.prepareMessagePlayerKillerVariable("Announcements.Messages.BleedOut.Killer", event.getEntity(), dp.getDowningEntity()));
                    if(dp.getDowningEntity() instanceof Player p && FourthChance.CONFIG.getConfig().getBoolean("GeneralOptions.DingWhenBledout"))
                        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                }
                else
                    event.setDeathMessage(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.BleedOut." + dp.getDowningCause().toString(), event.getEntity()));
            }
        }
        dp.resetPlayer();
    }
}
