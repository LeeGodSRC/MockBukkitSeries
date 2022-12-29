package be.seeseemelk.mockbukkit.entity;

import be.seeseemelk.mockbukkit.potion.ActivePotionEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class LivingEntityMock extends EntityMock implements LivingEntity {

    private final Set<ActivePotionEffect> activeEffects = new HashSet<>();

    public LivingEntityMock(UUID uuid) {
        super(uuid);
    }

    @Override
    public double getEyeHeight() {
        return 0;
    }

    @Override
    public double getEyeHeight(boolean b) {
        return 0;
    }

    @Override
    public Location getEyeLocation() {
        return null;
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
        return null;
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return null;
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
        return null;
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        return null;
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
        return null;
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return null;
    }

    @Override
    public Egg throwEgg() {
        return null;
    }

    @Override
    public Snowball throwSnowball() {
        return null;
    }

    @Override
    public Arrow shootArrow() {
        return null;
    }

    @Override
    public int getRemainingAir() {
        return 0;
    }

    @Override
    public void setRemainingAir(int i) {

    }

    @Override
    public int getMaximumAir() {
        return 0;
    }

    @Override
    public void setMaximumAir(int i) {

    }

    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {

    }

    @Override
    public double getLastDamage() {
        return 0;
    }

    @Override
    public int _INVALID_getLastDamage() {
        return 0;
    }

    @Override
    public void setLastDamage(double v) {

    }

    @Override
    public void _INVALID_setLastDamage(int i) {

    }

    @Override
    public int getNoDamageTicks() {
        return 0;
    }

    @Override
    public void setNoDamageTicks(int i) {

    }

    @Override
    public Player getKiller() {
        return null;
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect)
    {
        return addPotionEffect(effect, false);
    }

    @Override
    @Deprecated
    public boolean addPotionEffect(PotionEffect effect, boolean force)
    {
        if (effect != null)
        {
            // Bukkit now allows multiple effects of the same type,
            // the force/success attributes are now obsolete
            activeEffects.add(new ActivePotionEffect(effect));
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects)
    {
        boolean successful = true;

        for (PotionEffect effect : effects)
        {
            if (!addPotionEffect(effect))
            {
                successful = false;
            }
        }

        return successful;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type)
    {
        return getPotionEffect(type) != null;
    }

    public PotionEffect getPotionEffect(PotionEffectType type)
    {
        for (PotionEffect effect : getActivePotionEffects())
        {
            if (effect.getType().equals(type))
            {
                return effect;
            }
        }

        return null;
    }

    @Override
    public void removePotionEffect(PotionEffectType type)
    {

        activeEffects.removeIf(effect -> effect.hasExpired() || effect.getPotionEffect().getType().equals(type));
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects()
    {
        Set<PotionEffect> effects = new HashSet<>();
        Iterator<ActivePotionEffect> iterator = activeEffects.iterator();

        while (iterator.hasNext())
        {
            ActivePotionEffect effect = iterator.next();

            if (effect.hasExpired())
            {
                iterator.remove();
            }
            else
            {
                effects.add(effect.getPotionEffect());
            }
        }

        return effects;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return false;
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return false;
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {

    }

    @Override
    public EntityEquipment getEquipment() {
        return null;
    }

    @Override
    public void setCanPickupItems(boolean b) {

    }

    @Override
    public boolean getCanPickupItems() {
        return false;
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return null;
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return false;
    }

    @Override
    public void damage(double v) {

    }

    @Override
    public void _INVALID_damage(int i) {

    }

    @Override
    public void damage(double v, Entity entity) {

    }

    @Override
    public void _INVALID_damage(int i, Entity entity) {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public int _INVALID_getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double v) {

    }

    @Override
    public void _INVALID_setHealth(int i) {

    }

    @Override
    public double getMaxHealth() {
        return 0;
    }

    @Override
    public int _INVALID_getMaxHealth() {
        return 0;
    }

    @Override
    public void setMaxHealth(double v) {

    }

    @Override
    public void _INVALID_setMaxHealth(int i) {

    }

    @Override
    public void resetMaxHealth() {

    }

    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return null;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return null;
    }
}
