package net.minecraft.enchantment;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment
{
    public final ProtectionEnchantment.Type protectionType;

    public ProtectionEnchantment(Enchantment.Rarity rarityIn, ProtectionEnchantment.Type protectionTypeIn, EquipmentSlotType... slots)
    {
        super(rarityIn, protectionTypeIn == ProtectionEnchantment.Type.FALL ? EnchantmentType.ARMOR_FEET : EnchantmentType.ARMOR, slots);
        this.protectionType = protectionTypeIn;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return this.protectionType.getMinimalEnchantability() + (enchantmentLevel - 1) * this.protectionType.getEnchantIncreasePerLevel();
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + this.protectionType.getEnchantIncreasePerLevel();
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 4;
    }

    /**
     * Calculates the damage protection of the enchantment based on level and damage source passed.
     */
    public int calcModifierDamage(int level, DamageSource source)
    {
        if (source.canHarmInCreative())
        {
            return 0;
        }
        else if (this.protectionType == ProtectionEnchantment.Type.ALL)
        {
            return level;
        }
        else if (this.protectionType == ProtectionEnchantment.Type.FIRE && source.isFireDamage())
        {
            return level * 2;
        }
        else if (this.protectionType == ProtectionEnchantment.Type.FALL && source == DamageSource.FALL)
        {
            return level * 3;
        }
        else if (this.protectionType == ProtectionEnchantment.Type.EXPLOSION && source.isExplosion())
        {
            return level * 2;
        }
        else
        {
            return this.protectionType == ProtectionEnchantment.Type.PROJECTILE && source.isProjectile() ? level * 2 : 0;
        }
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(Enchantment ench)
    {
        if (ench instanceof ProtectionEnchantment)
        {
            ProtectionEnchantment protectionenchantment = (ProtectionEnchantment)ench;

            if (this.protectionType == protectionenchantment.protectionType)
            {
                return false;
            }
            else
            {
                return this.protectionType == ProtectionEnchantment.Type.FALL || protectionenchantment.protectionType == ProtectionEnchantment.Type.FALL;
            }
        }
        else
        {
            return super.canApplyTogether(ench);
        }
    }

    /**
     * Gets the amount of ticks an entity should be set fire, adjusted for fire protection.
     */
    public static int getFireTimeForEntity(LivingEntity livingEntity, int level)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FIRE_PROTECTION, livingEntity);

        if (i > 0)
        {
            level -= MathHelper.floor((float)level * (float)i * 0.15F);
        }

        return level;
    }

    public static double getBlastDamageReduction(LivingEntity LivingEntityIn, double damage)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.BLAST_PROTECTION, LivingEntityIn);

        if (i > 0)
        {
            damage -= (double)MathHelper.floor(damage * (double)((float)i * 0.15F));
        }

        return damage;
    }

    public static enum Type
    {
        ALL("all", 1, 11),
        FIRE("fire", 10, 8),
        FALL("fall", 5, 6),
        EXPLOSION("explosion", 5, 8),
        PROJECTILE("projectile", 3, 6);

        private final String typeName;
        private final int minEnchantability;
        private final int levelCost;

        private Type(String typeName, int minEnchantability, int levelCost)
        {
            this.typeName = typeName;
            this.minEnchantability = minEnchantability;
            this.levelCost = levelCost;
        }

        public int getMinimalEnchantability()
        {
            return this.minEnchantability;
        }

        public int getEnchantIncreasePerLevel()
        {
            return this.levelCost;
        }
    }
}
