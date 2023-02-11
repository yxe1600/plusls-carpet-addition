package com.plusls.carpet.mixin.rule.renewableNetherite;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    public MixinItemEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Nullable
    private static ItemStack pca$getNetheriteResult(@NotNull ItemStack stack) {
        Item newItem;
        if (Items.DIAMOND_SWORD.equals(stack.getItem())) {
            newItem = Items.NETHERITE_SWORD;
        } else if (Items.DIAMOND_SHOVEL.equals(stack.getItem())) {
            newItem = Items.NETHERITE_SHOVEL;
        } else if (Items.DIAMOND_PICKAXE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_PICKAXE;
        } else if (Items.DIAMOND_AXE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_AXE;
        } else if (Items.DIAMOND_HOE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_HOE;
        } else if (Items.DIAMOND_HELMET.equals(stack.getItem())) {
            newItem = Items.NETHERITE_HELMET;
        } else if (Items.DIAMOND_CHESTPLATE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_CHESTPLATE;
        } else if (Items.DIAMOND_LEGGINGS.equals(stack.getItem())) {
            newItem = Items.NETHERITE_LEGGINGS;
        } else if (Items.DIAMOND_BOOTS.equals(stack.getItem())) {
            newItem = Items.NETHERITE_BOOTS;
        } else {
            newItem = null;
        }
        if (newItem == null) {
            return null;
        }
        ItemStack ret = new ItemStack(newItem);
        CompoundTag compoundTag = stack.getTag();

        if (compoundTag != null) {
            ret.setTag(compoundTag.copy());
            ret.setDamageValue(ret.getMaxDamage() - 1);
        }
        return ret;
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V",
                    ordinal = 0
            )
    )
    private void checkDiamondEquip(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!PluslsCarpetAdditionSettings.renewableNetheriteEquip || this.level.isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) this.level;
        if (source == DamageSource.LAVA && level.dimension() == Level.NETHER) {
            ItemStack stack = this.getItem();
            if (!stack.isEmpty() && stack.getMaxDamage() - stack.getDamageValue() == 1) {
                Item item = stack.getItem();
                if ((item instanceof ArmorItem && ((ArmorItem) item).getMaterial() == ArmorMaterials.DIAMOND) ||
                        item instanceof TieredItem && ((TieredItem) item).getTier() == Tiers.DIAMOND) {
                    ItemStack newItemStack = pca$getNetheriteResult(stack);
                    if (newItemStack != null) {
                        level.addFreshEntity(new ItemEntity(level, this.getX(), this.getY(), this.getZ(), newItemStack));
                    }
                }
            }
        }
    }
}
