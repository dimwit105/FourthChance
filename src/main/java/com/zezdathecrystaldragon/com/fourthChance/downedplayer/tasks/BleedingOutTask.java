package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.DamageUtil;
import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.util.ReviveReason;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class BleedingOutTask extends CancellableRunnable
{
    private final DownedPlayer downedPlayer;
    private final Player player;
    Location lastTickLocation;
    double accumulatedDamage = 0D;
    double dirtyDamagePerSecond;
    double dirtyDamagePerTick;


    /**
     * The bleeding out task, created when a player is downed. This will tick 5 times per second, or every 4 minecraft ticks.
     * @param downedPlayer
     */
    public BleedingOutTask(DownedPlayer downedPlayer)
    {
        this.downedPlayer = downedPlayer;
        this.player = downedPlayer.getPlayer();
        lastTickLocation = player.getLocation();
        dirtyDamagePerSecond = FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "BleedingOptions.Health.DamageFormula");
        Bukkit.broadcastMessage("Bleeding DPS: " + dirtyDamagePerSecond);

    }

    @Override
    public void run()
    {
        dirtyDamagePerSecond = FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "BleedingOptions.Health.DamageFormula");
        dirtyDamagePerTick = dirtyDamagePerSecond / 5.0D;
        if(!player.hasPotionEffect(PotionEffectType.REGENERATION))
        {

            accumulatedDamage += dirtyDamagePerTick;
            Bukkit.broadcastMessage("Damage this tick: " + dirtyDamagePerTick + " Accumulated Damage: " + accumulatedDamage);
            if(accumulatedDamage >= 1.0D)
            {
                double damageToDeal = Math.floor(accumulatedDamage);
                player.damage(DamageUtil.getPureDamage(player, damageToDeal));
                accumulatedDamage -= damageToDeal;
            }
        }
        else if(player.hasPotionEffect(PotionEffectType.REGENERATION) && player.getHealth() + 0.125D >= player.getAttribute(Attribute.MAX_HEALTH).getValue())
        {
            downedPlayer.revive(ReviveReason.HEAL);
        }
    }

    public boolean isMoving()
    {
        Location current = player.getLocation();
        boolean moving = comparePositions(lastTickLocation, current);
        lastTickLocation = current;
        return moving;
    }
    private boolean comparePositions(Location loc1, Location loc2)
    {
        Location intermediary = loc1.subtract(loc2);
        return Math.abs(intermediary.getX()) > 0.05 || Math.abs(intermediary.getY()) > 0.05 || Math.abs(intermediary.getZ()) > 0.05;
    }

}
