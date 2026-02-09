package com.zezdathecrystaldragon.com.fourthChance.events.eventlisteners;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.util.PDCUtil;
import com.zezdathecrystaldragon.com.fourthChance.util.ReviveReason;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerRevivingPlayerEventListener implements Listener
{
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event)
    {
        if (event.getHand() != EquipmentSlot.OFF_HAND)
            return;
        if(event.getRightClicked().getType() != EntityType.PLAYER)
            return;
        Player rightClicker = event.getPlayer();
        if(rightClicker.getGameMode() == GameMode.SPECTATOR)
            return;

        Player rightClicked = (Player) event.getRightClicked();
        DownedPlayer reviver = FourthChance.DOWNED_PLAYERS.downedPlayers.get(rightClicker);
        DownedPlayer revivee = FourthChance.DOWNED_PLAYERS.downedPlayers.get(rightClicked);
        if(revivee == null || !revivee.isDowned()) {
            Bukkit.broadcastMessage("Revivee has no data, or is not down, returning");
            return;
        }

        if(reviver != null && reviver.isDowned())
        {
            Bukkit.broadcastMessage("Reviver is down, returning");
            return;
        }
        Bukkit.broadcastMessage("Task should be starting!");

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
