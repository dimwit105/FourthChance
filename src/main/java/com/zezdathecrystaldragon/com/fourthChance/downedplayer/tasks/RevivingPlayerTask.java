package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class RevivingPlayerTask extends CancellableRunnable
{
    Player reviver;
    DownedPlayer revivee;
    Player pRevivee;
    double maxRange;
    public RevivingPlayerTask(Player reviver, DownedPlayer revivee)
    {
        this.reviver = reviver;
        this.revivee = revivee;
        this.pRevivee = revivee.getPlayer();
        this.maxRange = FourthChance.CONFIG.getConfig().getDouble("ReviveOptions.MaxRange");

    }
    @Override
    public void run()
    {
        //double dist = reviver.getEyeLocation().distance(pRevivee.getLocation());
        Bukkit.broadcastMessage("Shooting a ray of range " + maxRange);

        RayTraceResult result = reviver.getWorld().rayTraceEntities(
                reviver.getEyeLocation(),
                reviver.getEyeLocation().getDirection(),
                maxRange,
                0.3D,
                entity -> entity instanceof Player && entity != reviver
        );

        if(result == null || result.getHitEntity() != pRevivee)
        {
            Bukkit.broadcastMessage("Ray miss!");
            revivee.stopRevivingTask();
            pRevivee.removePotionEffect(PotionEffectType.REGENERATION);
            return;
        }
        if(!pRevivee.hasPotionEffect(PotionEffectType.REGENERATION))
            pRevivee.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60*20, 0, false, true));
    }
}
