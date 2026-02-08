/**This package is intentionally mismatching the folder structure, to access variables from the DownedPlayerClass.
 * The folders are kept separate for organization purposes. Please forgive me!
 */
package com.zezdathecrystaldragon.com.fourthChance.downedplayer;

import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.CancellableRunnable;
import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
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
        //"CooldownOptions.DownResetTime"
        downedPlayer.reviveForgiveProgress++;
        if(downedPlayer.reviveForgiveProgress >= secondsToForgive)
        {
            downedPlayer.reviveCount--;
            downedPlayer.reviveForgiveProgress -= secondsToForgive;
        }
        downedPlayer.minimumDownedHealth = Math.min(playerMaxHealth, minHealthRegainedPerResetTime);

        if(downedPlayer.reviveCount == 0 && downedPlayer.minimumDownedHealth == playerMaxHealth)
        {
            downedPlayer.fullyHealed();
        }
    }
}
