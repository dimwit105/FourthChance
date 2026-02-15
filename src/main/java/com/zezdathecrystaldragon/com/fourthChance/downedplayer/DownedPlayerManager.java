package com.zezdathecrystaldragon.com.fourthChance.downedplayer;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class DownedPlayerManager
{
    public HashMap<Player, DownedPlayer> downedPlayers = new HashMap<>();
    public DownedPlayerManager()
    {
    }

    public boolean isDowned(Player p)
    {
        DownedPlayer dp = downedPlayers.get(p);
        if(dp == null)
            return false;
        return dp.isDowned();
    }
}
