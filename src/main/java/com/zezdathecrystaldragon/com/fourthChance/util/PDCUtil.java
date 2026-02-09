package com.zezdathecrystaldragon.com.fourthChance.util;

import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayerDataType;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DuplicateDataException;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
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
