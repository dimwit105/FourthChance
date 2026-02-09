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
import org.bukkit.entity.*;
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
    static Attribute[] zeroedAttributes = {Attribute.ATTACK_DAMAGE, Attribute.ATTACK_KNOCKBACK, Attribute.JUMP_STRENGTH, Attribute.BLOCK_INTERACTION_RANGE, Attribute.WATER_MOVEMENT_EFFICIENCY};
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
        Bukkit.broadcastMessage("New DownedPlayer!");
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
        FourthChance.DOWNED_PLAYERS.downedPlayers.put(player, this);
        incapacitate(blow);
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
    }

    public void incapacitate(EntityDamageEvent blow)
    {
        Bukkit.broadcastMessage("incapacitate called! " + downed);
        if(downed)
            return;
        double downedHealth = FourthChance.CONFIG.getFormulaicDouble(this, "BleedingOptions.Health.DownedHealthFormula");
        double bleedthroughDamage = player.getHealth() - blow.getFinalDamage();
        if(downedHealth + bleedthroughDamage <= 0)
            return;

        downed = true;
        if(player.hasPotionEffect(PotionEffectType.REGENERATION))
            player.removePotionEffect(PotionEffectType.REGENERATION);
        blow.setCancelled(true);

        startBleedoutTask();
        applyBleedingAttributeDebuffs();
        stopHealingTask();
        hideFromMobs();
        player.setHealth(Math.max(downedHealth + bleedthroughDamage, 0));
    }
    public void revive(ReviveReason reason)
    {
        if(!downed)
            return;


        downed = false;
        reviveCount++;
        player.setHealth(FourthChance.CONFIG.getFormulaicDouble(this, "ReviveOptions.HealthFormula"));
        player.removePotionEffect(PotionEffectType.REGENERATION);

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
        if(reviving != null)
            stopRevivingTask();
        pdc.remove(DOWNED_DATA);
        FourthChance.DOWNED_PLAYERS.downedPlayers.remove(player);
    }
    public void resetPlayer()
    {
        fullyHealed();
        removeBleedingAttributeDebuffs();
        removeAllRevivePenaltyAttributeDebuffs();
    }
    public void onPluginDisable()
    {
        stopHealingTask();
        stopRevivingTask();
        stopBleedoutTask();
        player.getPersistentDataContainer().set(DOWNED_DATA, new DownedPlayerDataType(), this);
    }

    public void onPlayerReconnect()
    {
        FourthChance.DOWNED_PLAYERS.downedPlayers.put(player, this);
        //Should already have attributes applied, and those are saved!
        if(downed)
            startBleedoutTask();
        else
            startHealingTask();
    }

    public void onPlayerDisconnect()
    {
        if(downed)
        {
            player.setHealth(0D);
            resetPlayer();
        }
        else
        {
            player.getPersistentDataContainer().set(DOWNED_DATA, new DownedPlayerDataType(), this);
        }
    }


    //Boring Utility Methods past this point
    private void addZeroModifier(Attribute attribute) {
        player.getAttribute(attribute).addModifier(
                new AttributeModifier(
                        BLEEDING_DEBUFF,
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
    public int getBleedingTaskIsMoving()
    {
        if(bleeding == null) {return 0;}
        return bleeding.isMoving();
    }
    private void startHealingTask()
    {
        if(healing != null)
            return;
        this.healing = new HealingDownsTask(this);
        FourthChance.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(player, healing, 4, 4);
    }
    private void stopHealingTask()
    {
        if(healing == null)
            return;
        this.healing.cancel();
        this.healing = null;
    }
    private void startBleedoutTask()
    {
        if(bleeding != null)
            return;
        this.bleeding = new BleedingOutTask(this);
        FourthChance.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(player, bleeding, 4, 4);
    }

    private void stopBleedoutTask()
    {
        if(bleeding == null)
            return;
        Bukkit.broadcastMessage("Cancelling Bleed");
        this.bleeding.cancel();
        this.bleeding = null;
    }

    public void startRevivingTask(Player reviver)
    {
        Bukkit.broadcastMessage("Starting the revive task");
        if(reviving != null)
            return;
        this.reviving = new RevivingPlayerTask(reviver, this);
        FourthChance.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(reviver, reviving, 0, 10);
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

    private void hideFromMobs()
    {
        for(Entity e : player.getNearbyEntities(21, 21, 21))
        {
            if(e instanceof Mob)
            {
                Mob le = (Mob) e;
                if(le.getTarget() == player)
                    le.setTarget(null);
            }
        }
    }

    private void applyBleedingAttributeDebuffs()
    {
        Bukkit.broadcastMessage(BLEEDING_DEBUFF.toString());
        player.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(new AttributeModifier(BLEEDING_DEBUFF, -1*FourthChance.PLUGIN.getConfig().getDouble("DownedOptions.CrawlSpeedPenalty"), AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        for(Attribute attribute : zeroedAttributes)
        {
            addZeroModifier(attribute);
        }
    }

    private void removeBleedingAttributeDebuffs()
    {
        Bukkit.broadcastMessage("Trying to remove debuffs!");
        AttributeInstance movement = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (movement != null) {
            for (AttributeModifier modifier : movement.getModifiers()) {
                Bukkit.broadcastMessage(modifier.getKey().toString());
                if (modifier.getKey().equals(BLEEDING_DEBUFF)) {
                    movement.removeModifier(modifier);
                }
            }
        }
        for(Attribute attribute : zeroedAttributes)
        {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            for(AttributeModifier modifier : instance.getModifiers())
            {
                if (modifier.getKey().equals(BLEEDING_DEBUFF))
                {
                    instance.removeModifier(modifier);
                }
            }
        }
    }


    public void incrementReviveForgiveProgress() { reviveForgiveProgress++; }
    public int getReviveForgiveProgress() { return reviveForgiveProgress; }

    public void decrementReviveCount(int secondsToForgive)
    {
        reviveCount--;
        reviveForgiveProgress = Math.max(0, reviveForgiveProgress - secondsToForgive);}

    public void healMinimumDownedHealth(double amountToHeal)
    {
        minimumDownedHealth = Math.min(player.getAttribute(Attribute.MAX_HEALTH).getValue(), minimumDownedHealth + amountToHeal);
    }
    public double getMinimumDownedHealth() { return minimumDownedHealth; }

    private void applyRevivePenaltyAttributeDebuff()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        boolean modifierFound = false;
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(REVIVED))
            {
                AttributeModifier intensified = new AttributeModifier(REVIVED, am.getAmount() + FourthChance.CONFIG.getFormulaicDouble(this, "ReviveOptions.MaxHealthPenalty"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
                instance.removeModifier(am);
                instance.addModifier(intensified);
                modifierFound = true;
            }
        }
        if (!modifierFound)
        {
            instance.addModifier(new AttributeModifier(REVIVED, FourthChance.CONFIG.getFormulaicDouble(this, "ReviveOptions.MaxHealthPenalty"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        }
    }

    private void removeAllRevivePenaltyAttributeDebuffs()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        boolean modifierFound = false;
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(REVIVED))
            {
                instance.removeModifier(am);
            }
        }
    }
    private void removeOneRevivePenaltyAttributeDebuff()
    {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        for (AttributeModifier am : instance.getModifiers())
        {
            if(am.getKey().equals(REVIVED))
            {
                AttributeModifier reduced = new AttributeModifier(REVIVED, am.getAmount() - FourthChance.CONFIG.getFormulaicDouble(this, "ReviveOptions.MaxHealthPenalty"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
                instance.removeModifier(am);
                if(reduced.getAmount() < 0D)
                    instance.addModifier(reduced);
            }
        }
    }
}
