package com.plusls.carpet.mixin.rule.emptyShulkerBoxStack;

import com.plusls.carpet.util.rule.emptyShulkerBoxStack.ShulkerBoxItemUtil;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 从地上捡起盒子时可堆叠
@Mixin(Inventory.class)
public abstract class MixinInventory implements Container, Nameable {
    @Redirect(
            method = "hasRemainingSpaceForItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isStackable()Z",
                    ordinal = 0
            )
    )
    private boolean canStackAddMoreIsStackable(ItemStack itemStack) {
        return ShulkerBoxItemUtil.isStackable(itemStack);
    }

    @Redirect(
            method = "hasRemainingSpaceForItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I",
                    ordinal = 0
            )
    )
    private int canStackAddMoreGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }

    @Redirect(
            method = "addResource(ILnet/minecraft/world/item/ItemStack;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I",
                    ordinal = -1
            )
    )
    private int addStackGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }

    // 避免死循环
    @Redirect(
            //#if MC > 11605
            method = "placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;Z)V",
            //#else
            //$$ method = "placeItemBackInInventory",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I",
                    ordinal = 0
            )
    )
    private int offerGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }
}
