package com.zezdathecrystaldragon.fourthChance.events;

import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRevivedEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;
    private DownedPlayer downedPlayer;

    /**
     * Called AFTER a player is revived.
     * @param player the player being revived
     * @param downedPlayer their data after being revived
     */
    public PlayerRevivedEvent(Player player, DownedPlayer downedPlayer)
    {
        this.player = player;
        this.downedPlayer = downedPlayer;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }
    public Player getPlayerBeingRevived()
    {
        return player;
    }
    public DownedPlayer getDownedPlayerData()
    {
        return downedPlayer;
    }
}
