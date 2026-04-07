package com.zezdathecrystaldragon.fourthChance.downedplayer;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class DownedPlayerManager
{
    private final HashMap<Player, DownedPlayer> downedPlayers = new HashMap<>();
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
    public void addDownedPlayer(Player p, DownedPlayer dp)
    {
        downedPlayers.put(p, dp);
    }
    public DownedPlayer removeDownedPlayer(Player p)
    {
        return downedPlayers.remove(p);
    }
    public DownedPlayer get(Player p)
    {
        return downedPlayers.get(p);
    }
    public double getTotalHealth(Player p)
    {
        if(downedPlayers.containsKey(p) && !isDowned(p))
        {
            double downedHealth = FourthChance.CONFIG.getFormulaicDouble(downedPlayers.get(p), "BleedingOptions.Health.DownedHealthFormula");
            return downedHealth + p.getHealth();
        }
        return FourthChance.CONFIG.getFormulaicDoubleNoData(p, "BleedingOptions.Health.DownedHealthFormula") + p.getHealth();
    }
    public void addStabilizer(Player p, Entity stabilizer)
    {
        if(!isDowned(p))
            return;
        downedPlayers.get(p).addStabilizer(stabilizer);
    }
}
