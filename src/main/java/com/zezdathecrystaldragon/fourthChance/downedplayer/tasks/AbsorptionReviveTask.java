package com.zezdathecrystaldragon.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

public class AbsorptionReviveTask extends CancellableRunnable
{
    public static final NamespacedKey ABSORPTION_BUFF =  new NamespacedKey(FourthChance.PLUGIN, "recently_revived");
    public final Player player;
    double amount;

    public AbsorptionReviveTask(Player p, int amount)
    {
        this.player = p;
        this.amount = amount;
        p.getAttribute(Attribute.MAX_ABSORPTION).addModifier(new AttributeModifier(ABSORPTION_BUFF, this.amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        p.setAbsorptionAmount(this.amount);
    }
    @Override
    public void cancel()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_ABSORPTION);
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(ABSORPTION_BUFF))
            {
                player.setAbsorptionAmount(player.getAbsorptionAmount() - am.getAmount());
                instance.removeModifier(am);
            }
        }
        //FourthChance.PLUGIN.getLogger().log(Level.WARNING, "Cancelling Absorption Revive!");
        super.cancel();
    }

    @Override
    public void run()
    {
        boolean found = false;
        AttributeInstance instance = player.getAttribute(Attribute.MAX_ABSORPTION);
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(ABSORPTION_BUFF))
            {
                found = true;
                AttributeModifier reduced = new AttributeModifier(ABSORPTION_BUFF, am.getAmount() - 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
                instance.removeModifier(am);
                if(reduced.getAmount() > 0)
                {
                    instance.addModifier(reduced);
                    player.setAbsorptionAmount(Math.max(player.getAbsorptionAmount() - 1, 0));
                }
                else
                    cancel();
            }
        }
        if(!found)
            cancel();
    }

    public static void onDisable()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            AttributeInstance instance = p.getAttribute(Attribute.MAX_ABSORPTION);
            for (AttributeModifier am : instance.getModifiers())
            {
                if(am.getKey().equals(ABSORPTION_BUFF))
                {
                    p.setAbsorptionAmount(p.getAbsorptionAmount() - am.getAmount());
                    instance.removeModifier(am);
                }
            }
        }
    }
}
