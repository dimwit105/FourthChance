package com.zezdathecrystaldragon.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;
import com.zezdathecrystaldragon.fourthChance.util.DamageUtil;
import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.util.ReviveReason;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class BleedingOutTask extends CancellableRunnable
{
    private final DownedPlayer downedPlayer;
    private final Player player;
    Location lastTickLocation;
    double accumulatedDamage = 0D;
    double dirtyDamagePerSecond;
    double dirtyDamagePerTick;


    /**
     * The bleeding out task, created when a player is downed. This will tick 5 times per second, or every 4 minecraft ticks.
     * @param downedPlayer
     */
    public BleedingOutTask(DownedPlayer downedPlayer)
    {
        this.downedPlayer = downedPlayer;
        this.player = downedPlayer.getPlayer();
        lastTickLocation = player.getLocation();
        dirtyDamagePerSecond = FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "BleedingOptions.Health.DamageFormula");
    }

    @Override
    public void run()
    {
        if(!downedPlayer.isDowned())
            return;
        dirtyDamagePerSecond = FourthChance.CONFIG.getFormulaicDouble(downedPlayer, "BleedingOptions.Health.DamageFormula");
        dirtyDamagePerTick = dirtyDamagePerSecond / 5.0D;
        if(!player.hasPotionEffect(PotionEffectType.REGENERATION) && !downedPlayer.hasRevivingTask())
        {
            accumulatedDamage += dirtyDamagePerTick;
            if (accumulatedDamage > player.getHealth())
            {
                if(FourthChance.RANDOM.nextDouble() < FourthChance.CONFIG.getConfig().getDouble("ReviveOptions.SelfReviveChance"))
                {
                    downedPlayer.revive(ReviveReason.CHANCE);
                    return;
                }
                player.setNoDamageTicks(0);
                player.damage(DamageUtil.getPureDamage(player, accumulatedDamage));
                sendProgressMessage();
                return;
            }
            if(accumulatedDamage >= 1.0D && player.getNoDamageTicks() <= 0)
            {
                //double damageToDeal = Math.floor(accumulatedDamage);
                player.damage(DamageUtil.getPureDamage(player, accumulatedDamage));
                accumulatedDamage = 0;
            }
            sendProgressMessage();
        }
        else if(player.hasPotionEffect(PotionEffectType.REGENERATION) && player.getHealth() + 0.125D >= player.getAttribute(Attribute.MAX_HEALTH).getValue())
        {
            if(downedPlayer.hasRevivingTask())
                downedPlayer.revive(ReviveReason.FRIEND);
            else
                downedPlayer.revive(ReviveReason.HEAL);
        }
        downedPlayer.setMinimumDownedHealth(player.getHealth());
    }

    public int isMoving()
    {
        int total = 0;
        Location current = player.getLocation();
        if (comparePositions(lastTickLocation, current))
            total++;
        if (player.isSwimming() || current.getBlock().getType() == Material.WATER)
            total++;

        lastTickLocation = current;

        return total;
    }
    private boolean comparePositions(Location loc1, Location loc2)
    {
        Location intermediary = loc1.subtract(loc2);
        return Math.abs(intermediary.getX()) > 0.05 || Math.abs(intermediary.getY()) > 0.05 || Math.abs(intermediary.getZ()) > 0.05;
    }
    private void sendProgressMessage()
    {
        double baseDamage = FourthChance.CONFIG.getFormulaicDoubleNoData(player, "BleedingOptions.Health.DamageFormula");
        double maxTime = player.getAttribute(Attribute.MAX_HEALTH).getValue() / baseDamage;
        double timeRemaining = Math.max((player.getHealth() - accumulatedDamage) / dirtyDamagePerSecond, 0);
        double pct = timeRemaining / maxTime;

        boolean flashing = pct <= 0.10;
        ChatColor color;

        if (flashing) {
            color = (player.getTicksLived() / 4 % 2 == 0)
                    ? ChatColor.RED
                    : ChatColor.DARK_RED;
        } else {
            color = interpolateColor(pct);
        }

        StringBuilder progressMessage = new StringBuilder();
        progressMessage.append(color);
        if(timeRemaining > 0)
            progressMessage.append(String.format("%.1f", timeRemaining)).append("s");
        else
            progressMessage.append(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.Death", player));

        BaseComponent tc = TextComponent.fromLegacy(progressMessage.toString());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, tc);
    }

    private ChatColor interpolateColor(double pct) {
        int[] darkRed = {139, 0, 0};
        int[] red     = {255, 0, 0};
        int[] orange  = {255, 170, 0};
        int[] yellow  = {255, 255, 0};
        int[] green   = {0, 255, 0}; // bright green

        if (pct >= 2.0) {
            return ChatColor.of("#00FF00"); // cap at bright green
        }

        if (pct > 1.0) {
            // yellow → green (1.0–2.0)
            double t = pct - 1.0;         // 0 → 1 over this range
            return lerpColor(yellow, green, t);
        }

        if (pct > 0.5) {
            // yellow → orange (0.5–1.0)
            double t = (pct - 0.5) * 2;
            return lerpColor(yellow, orange, t);
        }

        if (pct > 0.1) {
            // orange → red (0.1–0.5)
            double t = (pct - 0.1) / 0.4;
            return lerpColor(orange, red, t);
        }

        // red → dark red (0–0.1). Kinda pointless since we're flashing here.
        double t = pct / 0.1;
        return lerpColor(red, darkRed, t);
    }

    private ChatColor lerpColor(int[] a, int[] b, double t) {
        int r = (int)(a[0] + (b[0] - a[0]) * t);
        int g = (int)(a[1] + (b[1] - a[1]) * t);
        int bl = (int)(a[2] + (b[2] - a[2]) * t);

        return ChatColor.of(String.format("#%02X%02X%02X", r, g, bl));
    }
}
