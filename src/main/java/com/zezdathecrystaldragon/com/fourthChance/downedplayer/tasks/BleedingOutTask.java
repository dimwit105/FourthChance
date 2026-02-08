package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.DamageUtil;
import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
        dirtyDamagePerSecond = FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "BleedingOptions.Health.DownedHealthFormula");
        dirtyDamagePerTick = dirtyDamagePerSecond / 5.0D;
    }

    @Override
    public void run()
    {
        accumulatedDamage += dirtyDamagePerTick;
        if(accumulatedDamage >= 1.0D)
        {
            player.damage(DamageUtil.getPureDamage(player, 1.0D));
            accumulatedDamage--;
        }
    }

    public boolean isMoving() {

        Location current = player.getLocation();
        try {
            double distSq = current.distanceSquared(lastTickLocation);
            lastTickLocation = current;
            return distSq > 0.0025F;
        } catch (IllegalArgumentException e) {
            lastTickLocation = current;
            return true;
        }
    }

}
