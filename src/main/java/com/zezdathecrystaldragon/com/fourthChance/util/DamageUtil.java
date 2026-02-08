package com.zezdathecrystaldragon.com.fourthChance.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class DamageUtil
{
    public static double getPureDamage(Player player, double intendedDamage)
    {
        if(player.hasPotionEffect(PotionEffectType.RESISTANCE))
        {
            double divisor = 0.2*(player.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() + 1);
            intendedDamage = intendedDamage / Math.max(0.2, 1-divisor);
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
        return intendedDamage / enchantmentDivisor;
    }
}
