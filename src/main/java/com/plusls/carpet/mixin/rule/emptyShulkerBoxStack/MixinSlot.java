package com.plusls.carpet.mixin.rule.emptyShulkerBoxStack;

import com.plusls.carpet.util.rule.emptyShulkerBoxStack.ShulkerBoxItemUtil;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Slot.class)
public class MixinSlot {
    @Redirect(
            method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I",
                    ordinal = 0
            )
    )
    private int getMaxItemCountGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }
}
