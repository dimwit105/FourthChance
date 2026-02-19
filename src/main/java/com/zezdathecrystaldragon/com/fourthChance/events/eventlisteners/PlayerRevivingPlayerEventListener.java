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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.ref.WeakReference;
import java.util.logging.Level;

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
        if(!FourthChance.DOWNED_PLAYERS.isDowned(rightClicked)) {
            return;
        }

        if(FourthChance.DOWNED_PLAYERS.isDowned(rightClicker))
        {
            return;
        }
        if(FourthChance.CONFIG.getConfig().getBoolean("ReviveOptions.Teams.RespectTeams") && !shareTeams(rightClicker,rightClicked))
        {
            rightClicker.sendMessage(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.TeamFail", rightClicker));
            return;
        }
        DownedPlayer revivee = FourthChance.DOWNED_PLAYERS.downedPlayers.get(rightClicked);
        WeakReference<DownedPlayer> dpRef = new WeakReference<>(revivee);

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

    public boolean shareTeams(Player rightclicked, Player rightclicker)
    {
        Scoreboard board = FourthChance.PLUGIN.getServer().getScoreboardManager().getMainScoreboard();
		/*
		for(Team teams : board.getTeams())
		{
			Bukkit.broadcastMessage("Name: " + teams.getName());
			Bukkit.broadcastMessage("Entries:");
			for(String entry : teams.getEntries())
			{
				Bukkit.broadcastMessage(entry);
			}
		}
		*/
        Team p = board.getEntryTeam(rightclicked.getName());
        Team ep = board.getEntryTeam(rightclicker.getName());

        if(p != null && ep != null && p.equals(ep))
        {
            return true;
        }
        switch(FourthChance.CONFIG.getConfig().getString("ReviveOptions.Teams.IndependentBehavior"))
        {
            case "ALL":
                if(p == null || ep == null)
                {
                    return true;
                }
                break;

            case "OWN":
                if(p == null  && ep == null)
                {
                    return true;
                }
                break;

            case "NONE":
                break;
            case null:
            default:
                return false;
        }
        return false;
    }
}
