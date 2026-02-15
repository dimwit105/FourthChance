package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import java.awt.*;

public class RevivingPlayerTask extends CancellableRunnable
{
    Player reviver;
    PotionEffect existingRegen = null;
    DownedPlayer revivee;
    Player pRevivee;
    double maxRange;
    public RevivingPlayerTask(Player reviver, DownedPlayer revivee)
    {
        this.reviver = reviver;
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
            reviver.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(FourthChance.CONFIG.prepareMessagePlayerVariable("Announcements.Messages.Revived", pRevivee)));
        }
        this.cancel();
    }

    @Override
    public void run()
    {
        //double dist = reviver.getEyeLocation().distance(pRevivee.getLocation());

        RayTraceResult result = reviver.getWorld().rayTraceEntities(
                reviver.getEyeLocation(),
                reviver.getEyeLocation().getDirection(),
                maxRange,
                0.3D,
                entity -> entity instanceof Player && entity != reviver
        );

        if(result == null || result.getHitEntity() != pRevivee)
        {
            PotionEffect effect = pRevivee.getPotionEffect(PotionEffectType.REGENERATION);

            if(effect != null && effect.getDuration() == PotionEffect.INFINITE_DURATION)
                pRevivee.removePotionEffect(PotionEffectType.REGENERATION);
            revivee.stopRevivingTask(false);
            return;
        }
        if(!pRevivee.hasPotionEffect(PotionEffectType.REGENERATION))
            pRevivee.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 32, false, true));
        sendProgressMessage();


    }
    private void sendProgressMessage()
    {
        double reviveeMaxHealth = pRevivee.getAttribute(Attribute.MAX_HEALTH).getValue();
        double reviveProgress = pRevivee.getHealth() / reviveeMaxHealth;

        int scaleLength = 20;
        int filled = (int) Math.round(reviveProgress * scaleLength);

        StringBuilder progressMessage = new StringBuilder();
        progressMessage.append(ChatColor.RESET).append(String.format("Reviving %s", pRevivee.getDisplayName()));

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


}
