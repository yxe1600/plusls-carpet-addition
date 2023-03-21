package com.plusls.carpet.mixin.rule.emptyShulkerBoxStack;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;
//#if MC > 11605
import com.plusls.carpet.util.rule.emptyShulkerBoxStack.ShulkerBoxItemUtil;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//#endif

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = ">1.16.5"))
@Mixin(Slot.class)
public class MixinSlot {
    //#if MC > 11605
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
    //#endif
}
