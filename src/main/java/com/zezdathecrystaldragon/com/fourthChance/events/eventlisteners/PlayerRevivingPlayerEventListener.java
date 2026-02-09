package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import com.zezdathecrystaldragon.com.fourthChance.util.ReviveReason;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerRevivingPlayerEventListener implements Listener
{
    public void onPlayerRightClick(PlayerInteractEntityEvent event)
    {
        if(event.getRightClicked().getType() != EntityType.PLAYER)
            return;
        Player rightClicker = event.getPlayer();
        Player rightClicked = (Player) event.getRightClicked();
        DownedPlayer reviver = FourthChance.DOWNED_PLAYERS.downedPlayers.get(rightClicker);
        DownedPlayer revivee = FourthChance.DOWNED_PLAYERS.downedPlayers.get(rightClicked);
        if(revivee == null)
            return;

        if(reviver == null || !reviver.isDowned())
            return;

        //Alive player right clicked a downed player! We need to start a revive task, but ensure no duplicate tasks!
        if(!revivee.hasRevivingTask())
        {
            revivee.startRevivingTask(rightClicker);
        }
        else
        {
            rightClicker.sendMessage(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.ReviveBusy", rightClicked));
        }
    }
}
