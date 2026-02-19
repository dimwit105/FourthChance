package com.zezdathecrystaldragon.fourthChance.util;

import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayerDataType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public class PDCUtil
{
    public static DownedPlayer getDownedPlayerData(Player p)
    {
        PersistentDataContainer container = p.getPersistentDataContainer();
        if (container.has(DownedPlayer.DOWNED_DATA))
        {
            return container.get(DownedPlayer.DOWNED_DATA, new DownedPlayerDataType());
        }
        return null;
    }
}
