package com.zezdathecrystaldragon.com.fourthChance;

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
    public static DownedPlayer getOrCreateDownedPlayerData(Player p, EntityDamageEvent blow)
    {
        PersistentDataContainer container = p.getPersistentDataContainer();
        if (container.has(DownedPlayer.DOWNED_DATA))
        {
            return container.get(DownedPlayer.DOWNED_DATA, new DownedPlayerDataType());
        }
        else
        {
            try {
                return new DownedPlayer(p.getUniqueId(), blow);
            } catch (DuplicateDataException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
