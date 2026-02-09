package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class RevivingPlayerTask extends CancellableRunnable
{
    Player reviver;
    DownedPlayer revivee;
    Player pRevivee;
    public RevivingPlayerTask(Player reviver, DownedPlayer revivee)
    {
        this.reviver = reviver;
        this.revivee = revivee;
        this. pRevivee = revivee.getPlayer();

    }
    @Override
    public void run()
    {
        //double dist = reviver.getEyeLocation().distance(pRevivee.getLocation());

        RayTraceResult result = reviver.getWorld().rayTraceEntities(
                reviver.getEyeLocation(),
                reviver.getEyeLocation().getDirection(),
                FourthChance.CONFIG.getConfig().getDouble("ReviveOptions.MaxRange"),
                0.0625D,
                entity -> entity instanceof Player
        );
        if(result == null || result.getHitEntity() != pRevivee)
        {
            revivee.stopRevivingTask();
        }
        pRevivee.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 55, 0, false, true));
    }
}
