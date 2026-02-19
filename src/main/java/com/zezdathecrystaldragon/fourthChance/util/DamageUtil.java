package com.zezdathecrystaldragon.fourthChance.util;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.tasks.AbsorptionReviveTask;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffectType;

import static com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayer.REVIVED;

public class DamageUtil
{
    public static double getPureDamage(Player player, double intendedDamage)
    {
        double damagetoDeal = intendedDamage;
        if(player.hasPotionEffect(PotionEffectType.RESISTANCE))
        {
            double divisor = 0.2*(player.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() + 1);
            damagetoDeal = intendedDamage / Math.max(0.2, 1-divisor);
        }
        int protectionLevels = 0;
        for(int i = 0; i < player.getInventory().getArmorContents().length; i++)
        {
            if(player.getInventory().getArmorContents()[i] != null && player.getInventory().getArmorContents()[i].getEnchantments().containsKey(Enchantment.PROTECTION))
            {
                protectionLevels = protectionLevels + player.getInventory().getArmorContents()[i].getEnchantmentLevel(Enchantment.PROTECTION);
            }
        }
        double enchantmentDivisor = 1.0 - Math.min(20, protectionLevels)*0.04;
        return damagetoDeal / enchantmentDivisor;
    }
    public static void removeOneRevivePenaltyAttributeDebuff(Player player)
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(REVIVED))
            {
                AttributeModifier reduced = new AttributeModifier(REVIVED,
                        am.getAmount() - FourthChance.CONFIG.getEvaluatedDouble("ReviveOptions.MaxHealthPenalty"),
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.ANY);
                instance.removeModifier(am);
                if(reduced.getAmount() < 0D)
                    instance.addModifier(reduced);
            }
        }
        player.sendHealthUpdate();
    }

    public static void scrubAbsorptionBuffs(Player p)
    {
        AttributeInstance instance = p.getAttribute(Attribute.MAX_ABSORPTION);
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(AbsorptionReviveTask.ABSORPTION_BUFF))
                instance.removeModifier(am);
        }
    }
}
