package com.zezdathecrystaldragon.fourthChance.config;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer;

public class ConfigurationManager
{
    File messagesFile = new File("plugins/FourthChance/messages.yml");
    YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

    /**
     * r = number of times revived.
     * m = max health of the player
     * b = base max health of the player, before any modifiers. Should return 20, unless the base value was modified.
     * h = lowest health of the player had while downed.
     * c = current health.
     * f = current player fire ticks. 20 = 1 second of the player being on fire.
     * d = usually 0, increments by 1 if player is moving, and another if they are in water, for a maxmium of 2.
     */
    //Formula section
    public double getFormulaicDouble(DownedPlayer downedPlayer, String expression)
    {
        Player player = downedPlayer.getPlayer();
        BigDecimal distance;
        distance = new BigDecimal(downedPlayer.getBleedingTaskIsMoving());
        //Bukkit.broadcastMessage(String.valueOf(lastHitLoc.getOrDefault(player, player.getLocation()).distance(player.getLocation())))
        try {
            return new Expression(getConfig().getString(expression))
                    .with("r", new BigDecimal(downedPlayer.getReviveCount()))
                    .with("m", new BigDecimal(player.getPlayer().getAttribute(Attribute.MAX_HEALTH).getValue()))
                    .with("b", new BigDecimal(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue()))
                    .with("h", new BigDecimal(downedPlayer.getLowestDownedHealth()))
                    .with("c", new BigDecimal(player.getHealth()))
                    .with("f", new BigDecimal(player.getFireTicks()))
                    .with("d", distance)
                    .evaluate().getNumberValue().doubleValue();
        } catch (EvaluationException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double getFormulaicDoubleNoData(Player player, String expression)
    {
        BigDecimal distance;
        distance = new BigDecimal(0);
        //Bukkit.broadcastMessage(String.valueOf(lastHitLoc.getOrDefault(player, player.getLocation()).distance(player.getLocation())))
        try {
            return new Expression(getConfig().getString(expression))
                    .with("r", new BigDecimal(0))
                    .with("m", new BigDecimal(getConfig().getDouble("GeneralOptions.MaxHealth")))
                    .with("b", new BigDecimal(20))
                    .with("h", new BigDecimal(getConfig().getDouble("GeneralOptions.MaxHealth")))
                    .with("c", new BigDecimal(player.getHealth()))
                    .with("f", new BigDecimal(player.getFireTicks()))
                    .with("d", distance)
                    .evaluate().getNumberValue().doubleValue();
        } catch (EvaluationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double getEvaluatedDouble(String expression)
    {
        try {
            return new Expression(getConfig().getString(expression)).evaluate().getNumberValue().doubleValue();
        } catch (EvaluationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public String prepareMessagePlayerVariable(String path, Player player)
    {
       return messages.getString(path).replace("%p", player.getDisplayName()).replace("&", "§");
    }
    public String prepareMessagePlayerReviverVariable(String path, Player player, Player reviver)
    {
        return prepareMessagePlayerVariable(path, player).replace("%r", reviver.getName());
    }

    public String prepareMessagePlayerKillerVariable(String path, Player player, Entity killer)
    {
        return prepareMessagePlayerVariable(path, player).replace("%k", killer.getName());
    }
        //End formula section
    public void loadConfig()
    {
        getConfig().addDefault("GeneralOptions.DingWhenBledout", true);
        getConfig().addDefault("GeneralOptions.BeaconRegen", true);
        getConfig().addDefault("GeneralOptions.MaxHealth", 20);
        getConfig().addDefault("GeneralOptions.TotemPriority", "AFTER");

        getConfig().addDefault("DownedOptions.CrawlSpeedPenalty", 0.83D);
        getConfig().addDefault("DownedOptions.Glow", true);
        getConfig().addDefault("DownedOptions.MobInvisibility", true);

        getConfig().addDefault("DownedOptions.Damage.Outgoing", 0.0D);
        getConfig().addDefault("DownedOptions.Damage.Incoming", 0.2D);
        getConfig().addDefault("DownedOptions.Damage.Environmental", 1.0D);
        getConfig().addDefault("DownedOptions.Damage.Bleedthrough", 1.0D);

        //getConfig().addDefault("ReviveOptions.ReviveExtinguish", false);
        getConfig().addDefault("ReviveOptions.Absorption.Amount", 4);
        getConfig().addDefault("ReviveOptions.Absorption.Length", 5);
        getConfig().addDefault("ReviveOptions.Absorption.Decay", 2);
        getConfig().addDefault("ReviveOptions.SelfReviveChance", 0.05D);
        getConfig().addDefault("ReviveOptions.MaxHealthPenalty", "-2");
        getConfig().addDefault("ReviveOptions.MaxRange", 3.0D);

        getConfig().addDefault("ReviveOptions.Teams.RespectTeams", false);
        getConfig().addDefault("ReviveOptions.Teams.IndependentBehavior", "ALL");

        getConfig().addDefault("ReviveOptions.HealthFormula", "0.1*m");

        getConfig().addDefault("CooldownOptions.DownResetTime", 15.0D);

        getConfig().addDefault("BleedingOptions.Health.DownedHealthFormula", "1*m");
        getConfig().addDefault("BleedingOptions.Health.DamageFormula", "20/60 * (1+r)");

        messages.addDefault("Announcements.Enabled", true);
        messages.addDefault("Announcements.BleedOutMessagesEnabled", true);
        messages.addDefault("Announcements.DeathAnnounce", false);
        messages.addDefault("Announcements.Range", 256);
        //messages.addDefault("Announcements.Title.Enabled", false);
        //messages.addDefault("Announcements.Title.FadeIn", 0.5);
        //messages.addDefault("Announcements.Title.Time", 5);
        //messages.addDefault("Announcements.Title.FadeOut", 0.5);
        messages.addDefault("Announcements.Messages.Downed", "&4%p has been incapacitated!");
        messages.addDefault("Announcements.Messages.Death", "&8No one revived %p.");

        messages.addDefault("Announcements.Messages.Revived", "&1%p has been revived!");
        messages.addDefault("Announcements.Messages.SelfRevive", "&1%p is too stubborn to die!");
        messages.addDefault("Announcements.Messages.TotemRevive", "&1%r used a Totem of Undying to revive!");
        messages.addDefault("Announcements.Messages.ReviveOther", "%r has revived %p!");

        messages.addDefault("Announcements.Messages.ReviveIncoming", "%p is reviving you!");
        messages.addDefault("Announcements.Messages.SelfRevived", "%p is too stubborn to die!");
        messages.addDefault("Announcements.Messages.ReviveBusy", "%p is already being revived!");
        messages.addDefault("Announcements.Messages.ReviveCancelled", "You have stopped reviving %p!");

        messages.addDefault("Announcements.Messages.TeamFail", "You cannot revive someone not on your team!");
        messages.addDefault("Announcements.Messages.PermissionFail", "You can't fix this!");

        messages.addDefault("Announcements.Messages.BleedOut.CONTACT", "%p bled out after touching something spikey.");
        messages.addDefault("Announcements.Messages.BleedOut.DRAGON_BREATH", "%p bled out after being burnt by the dragon.");
        messages.addDefault("Announcements.Messages.BleedOut.DROWNING", "%p couldn't expel the water from their lungs.");
        messages.addDefault("Announcements.Messages.BleedOut.ENTITY_ATTACK", "%p bled out after being attacked.");
        messages.addDefault("Announcements.Messages.BleedOut.EXPLODE", "%p bled out after an explosion.");
        messages.addDefault("Announcements.Messages.BleedOut.FALL", "%p bled out after falling.");
        messages.addDefault("Announcements.Messages.BleedOut.FALLING_BLOCK", "%p bled out after being crushed.");
        messages.addDefault("Announcements.Messages.BleedOut.FIRE", "%p finally burned away.");
        messages.addDefault("Announcements.Messages.BleedOut.FIRE_TICK", "%p finally burned away.");
        messages.addDefault("Announcements.Messages.BleedOut.FLY_INTO_WALL", "%p bled out after smashing a wall.");
        messages.addDefault("Announcements.Messages.BleedOut.HOT_FLOOR", "%p bled out after stepping on magma.");
        messages.addDefault("Announcements.Messages.BleedOut.LAVA", "%p finally burned away after a lava encounter.");
        messages.addDefault("Announcements.Messages.BleedOut.LIGHTNING", "%p bled out after being struck by lightning.");
        messages.addDefault("Announcements.Messages.BleedOut.MAGIC", "%p bled out after magic happened.");
        messages.addDefault("Announcements.Messages.BleedOut.POISON", "%p succomed to the poison.");
        messages.addDefault("Announcements.Messages.BleedOut.PROJECTILE", "%p bled out after being shot.");
        messages.addDefault("Announcements.Messages.BleedOut.STARVATION", "%p finally starved.");
        messages.addDefault("Announcements.Messages.BleedOut.SUFFOCATION", "%p couldn't expel the blocks from their lungs.");
        messages.addDefault("Announcements.Messages.BleedOut.SUICIDE", "%p finally ended it.");
        messages.addDefault("Announcements.Messages.BleedOut.THORNS", "%p bled out after hitting thorny armor.");
        messages.addDefault("Announcements.Messages.BleedOut.VOID", "%p had the void stare back at it.");
        messages.addDefault("Announcements.Messages.BleedOut.WITHER", "%p finally withered away.");
        messages.addDefault("Announcements.Messages.BleedOut.UNKNOWN", "%p bled out.");
        messages.addDefault("Announcements.Messages.BleedOut.Killer", "%k finally killed %p.");
        messages.addDefault("Announcements.Messages.BleedOut.KILL", "%p was instantly killed.");
        messages.addDefault("Announcements.Messages.BleedOut.WORLD_BORDER", "%p bled out at the world border.");
        messages.addDefault("Announcements.Messages.BleedOut.ENTITY_SWEEP_ATTACK", "%p bled out from a sweeping attack.");
        messages.addDefault("Announcements.Messages.BleedOut.MELTING", "%p melted away.");
        messages.addDefault("Announcements.Messages.BleedOut.CAMPFIRE", "%p bled out on a campfire.");
        messages.addDefault("Announcements.Messages.BleedOut.CRAMMING", "%p was crushed due to entity cramming.");
        messages.addDefault("Announcements.Messages.BleedOut.DRYOUT", "%p bled out from drying out.");
        messages.addDefault("Announcements.Messages.BleedOut.FREEZE", "%p froze to death.");
        messages.addDefault("Announcements.Messages.BleedOut.SONIC_BOOM", "%p bled from their ear's after a warden's boom.");
        if(!messagesFile.exists())
        {
            messages.options().copyDefaults(true);
            try {
                messages.save(messagesFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //FourthChance.PLUGIN.saveResource("messages.yml", false);

        }
        getConfig().options().copyDefaults(true);
        FourthChance.PLUGIN.saveConfig();
    }

    public FileConfiguration getConfig()
    {
        return FourthChance.PLUGIN.getConfig();
    }
    public boolean isPartyMode()
    {
        return getConfig().getBoolean("GeneralOptions.PartyMode", false);
    }
    public double bleedthroughMultiplier()
    {
        return getConfig().getDouble("DownedOptions.Damage.Bleedthrough");
    }
}
