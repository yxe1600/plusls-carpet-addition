package com.plusls.carpet.mixin.rule.avoidAnvilTooExpensive;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {
    @Shadow
    private String itemName;

    public MixinAnvilMenu(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }

    @ModifyVariable(
            method = "createResult",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/inventory/DataSlot;get()I",
                            ordinal = 1
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private ItemStack setItemStack(ItemStack itemStack) {
        ItemStack inputStack = this.inputSlots.getItem(0);
        if (PluslsCarpetAdditionSettings.avoidAnvilTooExpensive && itemStack.isEmpty() && !inputStack.isEmpty() &&
                (!this.inputSlots.getItem(1).isEmpty() ||
                        (StringUtils.isBlank(this.itemName) && inputStack.hasCustomHoverName()) ||
                        (!StringUtils.isBlank(this.itemName) && !this.itemName.equals(inputStack.getHoverName().getString())))) {
            return inputStack.copy();
        } else {
            return itemStack;
        }
    }
}
