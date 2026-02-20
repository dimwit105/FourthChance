package com.zezdathecrystaldragon.fourthChance.downedplayer.tasks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class RevivingPlayerTask extends CancellableRunnable
{    
    ArrayList<Player> revivers = new ArrayList<>();
    DownedPlayer revivee;
    Player pRevivee;
    double maxRange;
    public RevivingPlayerTask(Player reviver, DownedPlayer revivee)
    {
        this.revivers.add(reviver);
        this.revivee = revivee;
        this.pRevivee = revivee.getPlayer();
        this.maxRange = FourthChance.CONFIG.getConfig().getDouble("ReviveOptions.MaxRange");

    }
    @Override
    public void cancel()
    {
        if(pRevivee.hasPotionEffect(PotionEffectType.REGENERATION) && pRevivee.getPotionEffect(PotionEffectType.REGENERATION).getDuration() == PotionEffect.INFINITE_DURATION)
            pRevivee.removePotionEffect(PotionEffectType.REGENERATION);
            
        super.cancel();
    }

    public void stopReviving(boolean revived)
    {
        if(revived)
        {
            revivers.forEach(reviver -> sendRevivedMessage(reviver));
        }
        this.cancel();
    }

    @Override
    public void run()
    {
        //double dist = reviver.getEyeLocation().distance(pRevivee.getLocation());
        Iterator<Player> iter = revivers.iterator();
        while(iter.hasNext())
        {
            Player reviver = iter.next();
            RayTraceResult result = reviver.getWorld().rayTraceEntities(
                reviver.getEyeLocation(),
                reviver.getEyeLocation().getDirection(),
                maxRange,
                0.3D,
                entity -> entity instanceof LivingEntity && entity != reviver
            );

            if(result == null || result.getHitEntity() != pRevivee || FourthChance.DOWNED_PLAYERS.isDowned(reviver))
            {
                iter.remove();
                sendCancelMessage(reviver);
                continue;
            }
            sendProgressMessage(reviver);
        }

        if(revivers.isEmpty())
        {
            revivee.stopRevivingTask(false);
            return;
        }
        PotionEffect effect = pRevivee.getPotionEffect(PotionEffectType.REGENERATION);
        //base amplifer is 32 to distinguish from vanilla regen effects. We use range 32-63 for reviving. Reapply new amplifier each tick, so new revivers can contribute.
        int currentAmplifier = 32 + Math.min(revivers.size() - 1, 31);
        if(effect == null || (effect.getAmplifier() != currentAmplifier && effect.getDuration() == PotionEffect.INFINITE_DURATION))
        {
            pRevivee.removePotionEffect(PotionEffectType.REGENERATION);
            pRevivee.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, currentAmplifier, true));
        }
        BaseComponent reviveeMessage = TextComponent.fromLegacy(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.ReviveIncoming", getRevivers().get(0)));
        pRevivee.spigot().sendMessage(ChatMessageType.ACTION_BAR, reviveeMessage);
    }

    public boolean addReviver(Player reviver)
    {
        if(revivers.contains(reviver))
            return false;
        revivers.add(reviver);
        return true;
    }

    public boolean removeReviver(Player reviver)
    {
        boolean removed = revivers.remove(reviver);
        if(revivers.isEmpty())
        {
            revivee.stopRevivingTask(false);
        }
        sendCancelMessage(reviver);
        return removed;
    }
    private void sendRevivedMessage(Player reviver)
    {
        BaseComponent tc = TextComponent.fromLegacy(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.Revived", pRevivee));
        reviver.spigot().sendMessage(ChatMessageType.ACTION_BAR, tc);
    }
    private void sendCancelMessage(Player reviver)
    {
        BaseComponent tc = TextComponent.fromLegacy(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.ReviveCancelled", pRevivee));
        reviver.spigot().sendMessage(ChatMessageType.ACTION_BAR, tc);
    }
    private void sendProgressMessage(Player reviver)
    {
        double reviveeMaxHealth = pRevivee.getAttribute(Attribute.MAX_HEALTH).getValue();
        double reviveProgress = pRevivee.getHealth() / reviveeMaxHealth;

        int scaleLength = 20;
        int filled = (int) Math.round(reviveProgress * scaleLength);

        StringBuilder progressMessage = new StringBuilder();
        progressMessage.append(ChatColor.RESET).append(String.format("Reviving %s ", pRevivee.getDisplayName()));

        for (int i = 0; i < scaleLength; i++) {
            double sectionProgress = (double) i / (scaleLength - 1);
            ChatColor c = ChatColor.of(progressToColor(sectionProgress));

            if (i < filled)
                progressMessage.append(c).append("■");
            else
                progressMessage.append(ChatColor.RESET).append("□");
        }

        // Reset color, then append numeric text
        progressMessage.append(ChatColor.RESET)
                .append(" ")
                .append(String.format("%.1f", pRevivee.getHealth()))
                .append("/")
                .append(String.format("%.1f", reviveeMaxHealth));

        BaseComponent tc = TextComponent.fromLegacy(progressMessage.toString());
        reviver.spigot().sendMessage(ChatMessageType.ACTION_BAR, tc);
    }


    private Color progressToColor(double progress) {
        progress = Math.max(0, Math.min(progress, 1)); // clamp

        int r, g;

        if (progress < 0.5) {
            // red → yellow
            double t = progress / 0.5;
            r = 255;
            g = (int) (255 * t);
        } else {
            // yellow → green
            double t = (progress - 0.5) / 0.5;
            r = (int) (255 * (1 - t));
            g = 255;
        }

        return new Color(r, g, 0);
    }
    public List<Player> getRevivers()
    {
        return Collections.unmodifiableList(revivers);
    }
    public static void onDisable()
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(p.hasPotionEffect(PotionEffectType.REGENERATION) && p.getPotionEffect(PotionEffectType.REGENERATION).getDuration() == PotionEffect.INFINITE_DURATION)
                p.removePotionEffect(PotionEffectType.REGENERATION);
        }
    }
}
