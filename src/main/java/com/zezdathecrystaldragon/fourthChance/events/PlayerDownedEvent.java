package com.zezdathecrystaldragon.fourthChance.events;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

public class PlayerDownedEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private Player playerBeingDowned;
    @Nullable
    private DownedPlayer downedPlayerData;

    public PlayerDownedEvent(Player p)
    {
        this.playerBeingDowned = p;
        this.downedPlayerData = FourthChance.DOWNED_PLAYERS.downedPlayers.get(p);
    }
    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }

    public Player getPlayerBeingDowned()
    {
        return playerBeingDowned;
    }
    @Nullable
    public DownedPlayer getDownedPlayerData()
    {
        return downedPlayerData;
    }
}
