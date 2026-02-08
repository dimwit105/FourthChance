package com.zezdathecrystaldragon.com.fourthChance;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.DownedPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.ezylang.evalex.Expression;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;

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
     * d = distance traveled since last instance of damage
     */
    //Formula section

    public double getFormulaicDouble(DownedPlayer downedPlayer, String expression)
    {
        Player player = downedPlayer.getPlayer();
        BigDecimal distance;
        if(downedPlayer.getBleedingTaskIsMoving())
        {
            distance = new BigDecimal(1);
        }
        else {distance = new BigDecimal(0);}
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
        //End formula section
    public void loadConfig()
    {
        getConfig().addDefault("GeneralOptions.OverKill", -20);
        getConfig().addDefault("GeneralOptions.BleedThrough", false);
        getConfig().addDefault("GeneralOptions.DingWhenBledout", true);
        getConfig().addDefault("GeneralOptions.BeaconRegen", true);

        getConfig().addDefault("DownedOptions.CrawlSpeedPenalty", 0.33D);
        getConfig().addDefault("DownedOptions.Glow", true);
        getConfig().addDefault("DownedOptions.Resistance", 3.1D);
        getConfig().addDefault("DownedOptions.RemoveRegeneration", true);
        getConfig().addDefault("DownedOptions.MobInvisibility", false);
        getConfig().addDefault("DownedOptions.TotallyAloneRange", 0D);

        getConfig().addDefault("DownedOptions.Burnouts.AllowBurnOuts", false);
        getConfig().addDefault("DownedOptions.Burnouts.BurnoutDamageFormula", "-2");

        getConfig().addDefault("DownedOptions.Damage.Outgoing", 0.0D);
        getConfig().addDefault("DownedOptions.Damage.Incoming", 0.5D);
        getConfig().addDefault("DownedOptions.Damage.Environmental", 1.0D);
        getConfig().addDefault("DownedOptions.Damage.Bleedthrough", 1.0D);

        getConfig().addDefault("ReviveOptions.ReviveExtinguish", true);
        getConfig().addDefault("ReviveOptions.Absorption.Enabled", true);
        getConfig().addDefault("ReviveOptions.Absorption.Length", 7);
        getConfig().addDefault("ReviveOptions.Absorption.Power", 1);
        getConfig().addDefault("ReviveOptions.ThrowRevive", false);
        getConfig().addDefault("ReviveOptions.KillRevive", false);
        getConfig().addDefault("ReviveOptions.SelfReviveChance", 0.05D);

        getConfig().addDefault("ReviveOptions.Teams.RespectTeams", false);
        getConfig().addDefault("ReviveOptions.Teams.IndependentBehavior", "ALL");

        getConfig().addDefault("ReviveOptions.HealthFormula", "0.1*m");

        getConfig().addDefault("ReviveOptions.Delay.Type", "REGENERATION");
        getConfig().addDefault("ReviveOptions.Delay.TimeFormula", "20");
        getConfig().addDefault("ReviveOptions.Delay.Power", 1);

        getConfig().addDefault("CooldownOptions.Cooldown.Enabled", false);
        getConfig().addDefault("CooldownOptions.Cooldown.Time", 2.0D);
        getConfig().addDefault("CooldownOptions.Cooldown.CanceledByHeal", false);
        getConfig().addDefault("CooldownOptions.DownResetTime", 15.0D);


        getConfig().addDefault("BleedingOptions.Health.DownedHealthFormula", "1*m");
        getConfig().addDefault("BleedingOptions.Health.Decay", 3.0D);

        getConfig().addDefault("BleedingOptions.Health.DamageFormula", "1 + (1*r)");

        messages.addDefault("Announcements.Enabled", true);
        messages.addDefault("Announcements.BleedOutMessagesEnabled", true);
        messages.addDefault("Announcements.DeathAnnounce", false);
        messages.addDefault("Announcements.Range", 256);
        messages.addDefault("Announcements.Title.Enabled", false);
        messages.addDefault("Announcements.Title.FadeIn", 0.5);
        messages.addDefault("Announcements.Title.Time", 5);
        messages.addDefault("Announcements.Title.FadeOut", 0.5);
        messages.addDefault("Announcements.Messages.Downed", "$4%p has been incapacitated!");
        messages.addDefault("Announcements.Messages.Revived", "$1%p has been revived!");
        messages.addDefault("Announcements.Messages.SelfRevive", "$1%p is too stubborn to die!");
        messages.addDefault("Announcements.Messages.Death", "$8No one revived %p.");
        messages.addDefault("Announcements.Messages.ReviveIncoming", "You will be revived in %s seconds");
        messages.addDefault("Announcements.Messages.ReviveHeal", "You will be revived when you reach full health");
        messages.addDefault("Announcements.Messages.ReviveCancelled", "Damage taken, revive has been cancelled");
        messages.addDefault("Announcements.Messages.ReviveOther", "%p is being revived!");
        messages.addDefault("Announcements.Messages.TeamFail", "You cannot revive someone not on your team!");
        messages.addDefault("Announcements.Messages.PermissionFail", "You can't fix this!");

        messages.addDefault("Announcements.Messages.BleedOut.CONTACT", "%p bled out after hugging a cactus.");
        messages.addDefault("Announcements.Messages.BleedOut.DRAGON_BREATH", "%p bled out after being burnt by the dragon.");
        messages.addDefault("Announcements.Messages.BleedOut.DROWNING", "%p couldn't expel the water from their lungs.");
        messages.addDefault("Announcements.Messages.BleedOut.ENTITY_ATTACK", "%p bled out after being attacked.");
        messages.addDefault("Announcements.Messages.BleedOut.EXPLODE", "%p bled out after an explosion.");
        messages.addDefault("Announcements.Messages.BleedOut.FALL", "%p bled out after falling.");
        messages.addDefault("Announcements.Messages.BleedOut.FALLING_BLOCK", "%p bled out after being crushed.");
        messages.addDefault("Announcements.Messages.BleedOut.FIRE", "%p finally burned away.");
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

        if(!messagesFile.exists())
        {
            FourthChance.PLUGIN.saveResource("messages.yml", false);
        }
        FourthChance.PLUGIN.saveDefaultConfig();
    }

    private FileConfiguration getConfig()
    {
        return FourthChance.PLUGIN.getConfig();
    }
    public double bleedthroughMultiplier()
    {
        return getConfig().getDouble("DownedOptions.Damage.Bleedthrough");
    }
}
