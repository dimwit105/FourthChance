package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealingDownsTask extends CancellableRunnable
{
    DownedPlayer downedPlayer;
    Player player;
    int secondsToForgive;
    double minHealthRegainedPerResetTime;
    double playerMaxHealth;
    public HealingDownsTask(DownedPlayer dp)
    {
        this.downedPlayer = dp;
        this.player = dp.getPlayer();
        playerMaxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        secondsToForgive = (int) Math.floor(FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "CooldownOptions.DownResetTime") * 60D);
        minHealthRegainedPerResetTime =  playerMaxHealth / (FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "CooldownOptions.DownResetTime")*60D);
    }

    @Override
    public void run()
    {
        playerMaxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        downedPlayer.incrementReviveForgiveProgress();
        if(downedPlayer.getReviveForgiveProgress() >= secondsToForgive)
        {
            downedPlayer.decrementReviveCount(secondsToForgive);
        }
        downedPlayer.healMinimumDownedHealth(minHealthRegainedPerResetTime);

        if(downedPlayer.getReviveCount() == 0 && downedPlayer.getMinimumDownedHealth() == playerMaxHealth)
        {
            downedPlayer.fullyHealed();
        }
    }
}
