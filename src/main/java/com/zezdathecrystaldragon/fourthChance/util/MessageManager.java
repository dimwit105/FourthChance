package com.zezdathecrystaldragon.fourthChance.util;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageManager
{
    public static void onRevive(DownedPlayer dp, ReviveReason reason)
    {
        switch (reason)
        {
            case CHANCE -> Bukkit.broadcastMessage(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.SelfRevived", dp.getPlayer()));
            case HEAL -> Bukkit.broadcastMessage(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.Revived", dp.getPlayer()));
            case FRIEND -> Bukkit.broadcastMessage(FourthChance.CONFIG.prepareMessagePlayerReviverVariable("Announcements.Messages.ReviveOther", dp.getPlayer(), dp.getReviver()));
        }
    }
    public static void onDown(Player player)
    {
        Bukkit.broadcastMessage(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.Downed", player));
    }
}
