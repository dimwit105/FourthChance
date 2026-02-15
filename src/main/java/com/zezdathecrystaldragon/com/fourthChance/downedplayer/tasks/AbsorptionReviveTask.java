package com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
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
    public void cancel()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_ABSORPTION);
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(ABSORPTION_BUFF))
                instance.removeModifier(am);
        }
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
                    player.setAbsorptionAmount(player.getAbsorptionAmount() - 1);
                }
                else
                    cancel();
            }
        }
        if(!found)
            cancel();
    }
}
