package com.zezdathecrystaldragon.com.fourthChance.downedplayer;

import com.zezdathecrystaldragon.com.fourthChance.FourthChance;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.BleedingOutTask;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.HealingDownsTask;
import com.zezdathecrystaldragon.com.fourthChance.downedplayer.tasks.RevivingPlayerTask;
import com.zezdathecrystaldragon.com.fourthChance.util.ReviveReason;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.UUID;

public class DownedPlayer
{
    static Attribute[] zeroedAttributes = {Attribute.ATTACK_DAMAGE, Attribute.ATTACK_KNOCKBACK, Attribute.JUMP_STRENGTH, Attribute.BLOCK_INTERACTION_RANGE};
    transient private Player player;
    UUID id;
    int reviveCount = 0;
    int reviveForgiveProgress = 0;
    double minimumDownedHealth;

    protected boolean downed;
    @Nullable
    transient Entity downer = null;
    transient private BleedingOutTask bleeding;
    transient private HealingDownsTask healing;
    transient private RevivingPlayerTask reviving = null;
    public static final NamespacedKey BLEEDING_DEBUFF =  new NamespacedKey(FourthChance.PLUGIN, "bleeding_debuff");
    public static final NamespacedKey DOWNED_DATA = new NamespacedKey(FourthChance.PLUGIN, "downed");
    public static final NamespacedKey REVIVED = new NamespacedKey(FourthChance.PLUGIN, "revived");

    /**
     * This constructor is for freshly downed players, will automatically call the internal incapacitate function, and
     * will handle attaching the data to the player.
     * @param id The player's UUID
     */
    public DownedPlayer(UUID id, EntityDamageEvent blow) throws DuplicateDataException {
        this.id = id;
        this.player = Bukkit.getPlayer(id);
        if(player.getPersistentDataContainer().has(DOWNED_DATA))
        {
            throw new DuplicateDataException("Tried to add data to a player that already had some! We should be updating that instead!");
        }
        this.minimumDownedHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent event)
        {
            if(event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Entity entity)
            {
                this.downer = entity;
            }
            else
            {
                this.downer = event.getDamager();
            }
        }
        incapacitate(blow);

        player.getPersistentDataContainer().set(DOWNED_DATA, new DownedPlayerDataType(), this);
    }

    /**
     * This is for loading saved data from file, when a player reconnects.
     * @param bytes The bytes read from the persistent data container.
     */
    public DownedPlayer(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long uuidMost = bb.getLong();
        long uuidLeast = bb.getLong();
        this.id = new UUID(uuidMost, uuidLeast);
        this.player = Bukkit.getPlayer(this.id);
        this.reviveCount = bb.getInt();
        this.reviveForgiveProgress = bb.getInt();
        this.minimumDownedHealth = bb.getDouble();
        this.downed = bb.get() == 1;

        if(downed)
        {
            startBleedoutTask();
        }
        else
        {
            startHealingTask();
        }
    }

    public void incapacitate(EntityDamageEvent blow)
    {
        if(downed)
            return;

        downed = true;
        if(player.hasPotionEffect(PotionEffectType.REGENERATION))
            player.removePotionEffect(PotionEffectType.REGENERATION);

        startBleedoutTask();
        applyBleedingAttributeDebuffs();
        startHealingTask();
        double downedHealth = FourthChance.CONFIG.getFormulaicDouble(this, "BleedingOptions.Health.DownedHealthFormula");
        double bleedthroughDamage = player.getHealth() - blow.getFinalDamage();
        player.setHealth(downedHealth + bleedthroughDamage);
    }
    public void revive(ReviveReason reason)
    {
        if(!downed)
            return;

        downed = false;
        reviveCount++;
        player.setHealth(FourthChance.CONFIG.getFormulaicDouble(this, "ReviveOptions.HealthFormula"));

        stopBleedoutTask();
        stopRevivingTask();
        removeBleedingAttributeDebuffs();
        applyRevivePenaltyAttributeDebuff();
        startHealingTask();
    }

    /**
     * This should be called when the player is completely forgiven of downs, and the data we have on them would be
     * identical to newly created data. No need to track what we can remake!
     */
    public void fullyHealed()
    {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if(bleeding != null)
            stopBleedoutTask();
        if(healing != null)
            stopHealingTask();
        pdc.remove(DOWNED_DATA);
    }
    public void resetPlayer()
    {
        fullyHealed();
        removeBleedingAttributeDebuffs();
    }

    public void onPlayerDisconnect()
    {
        if(downed)
        {
            player.setHealth(0D);
            resetPlayer();
        }
    }


    //Boring Utility Methods past this point
    private void addZeroModifier(Attribute attribute) {
        player.getAttribute(attribute).addModifier(
                new AttributeModifier(
                        new NamespacedKey(FourthChance.PLUGIN, "Downed"),
                        -1.0,
                        AttributeModifier.Operation.ADD_SCALAR,
                        EquipmentSlotGroup.ANY
                )
        );
    }
    public Player getPlayer()
    {
        return player;
    }

    public int getReviveCount()
    {
        return reviveCount;
    }
    public double getLowestDownedHealth()
    {
        return minimumDownedHealth;
    }
    public boolean isDowned()
    {
        return downed;
    }
    @Nullable
    public boolean getBleedingTaskIsMoving()
    {
        if(bleeding == null) {return false;}
        return bleeding.isMoving();
    }
    private void startHealingTask()
    {
        this.healing = new HealingDownsTask(this);
        FourthChance.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(player, healing, 4, 4);
    }
    private void stopHealingTask()
    {
        this.healing.cancel();
        this.healing = null;
    }
    private void startBleedoutTask()
    {
        this.bleeding = new BleedingOutTask(this);
        FourthChance.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(player, bleeding, 4, 4);
    }

    private void stopBleedoutTask()
    {
        this.bleeding.cancel();
        this.bleeding = null;
    }

    public void startRevivingTask(Player reviver)
    {
        this.reviving = new RevivingPlayerTask(reviver, this);
        FourthChance.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(reviver, reviving, 10, 10);
    }
    public void stopRevivingTask()
    {
        if(reviving == null)
            return;
        this.reviving.cancel();
        reviving = null;
    }
    public boolean hasRevivingTask()
    {
        if(reviving == null)
            return false;
        return true;
    }

    private void applyBleedingAttributeDebuffs()
    {
        player.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(new AttributeModifier(BLEEDING_DEBUFF, -1*FourthChance.PLUGIN.getConfig().getDouble(""), AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        for(Attribute attribute : zeroedAttributes)
        {
            addZeroModifier(attribute);
        }
    }

    private void removeBleedingAttributeDebuffs()
    {
        player.getAttribute(Attribute.MOVEMENT_SPEED).getModifiers().removeIf(mod -> mod.getKey().equals(BLEEDING_DEBUFF));
        for(Attribute attribute : zeroedAttributes)
        {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            instance.getModifiers().removeIf(mod ->
                    mod.getKey().equals(BLEEDING_DEBUFF)
            );
        }
    }


    public void incrementReviveForgiveProgress() { reviveForgiveProgress++; }
    public int getReviveForgiveProgress() { return reviveForgiveProgress; }

    public void decrementReviveCount(int secondsToForgive)
    {
        reviveCount--;
        reviveForgiveProgress = Math.max(0, reviveForgiveProgress - secondsToForgive);
    }

    public void healMinimumDownedHealth(double amountToHeal)
    {
        minimumDownedHealth = Math.min(player.getAttribute(Attribute.MAX_HEALTH).getValue(), minimumDownedHealth + amountToHeal);
    }
    public double getMinimumDownedHealth() { return minimumDownedHealth; }

    private void applyRevivePenaltyAttributeDebuff()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        instance.addModifier(new AttributeModifier(REVIVED, FourthChance.CONFIG.getFormulaicDouble(this, "ReviveOptions.MaxHealthPenalty"), AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
    }

    private void removeAllRevivePenaltyAttributeDebuffs()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        instance.getModifiers().removeIf(mod ->
                mod.getKey().equals(REVIVED)
        );
    }
    private void removeOneRevivePenaltyAttributeDebuff()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        AttributeModifier toRemove = instance.getModifiers().stream()
                .filter(mod -> mod.getKey().equals(REVIVED))
                .findFirst()
                .orElse(null);

        if (toRemove != null) instance.removeModifier(toRemove);
    }
}
