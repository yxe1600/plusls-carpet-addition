package com.plusls.carpet.mixin.rule.avoidAnvilTooExpensive;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

//#if MC > 11502
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
//#else
//$$ import net.minecraft.world.Container;
//$$ import org.spongepowered.asm.mixin.Final;
//#endif

@Mixin(AnvilMenu.class)
//#if MC > 11502
public abstract class MixinAnvilMenu extends ItemCombinerMenu {
    public MixinAnvilMenu(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }
//#else
//$$ public abstract class MixinAnvilMenu {
//#endif

    @Shadow
    private String itemName;

    //#if MC <= 11502
    //$$ @Shadow
    //$$ @Final
    //$$ private Container repairSlots;
    //#endif

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
        //#if MC > 11502
        ItemStack inputStack = this.inputSlots.getItem(0);
        //#else
        //$$ ItemStack inputStack = this.repairSlots.getItem(0);
        //#endif
        if (PluslsCarpetAdditionSettings.avoidAnvilTooExpensive && itemStack.isEmpty() && !inputStack.isEmpty() &&
                //#if MC > 11502
                (!this.inputSlots.getItem(1).isEmpty() ||
                //#else
                //$$ (!this.repairSlots.getItem(1).isEmpty() ||
                //#endif
                        (StringUtils.isBlank(this.itemName) && inputStack.hasCustomHoverName()) ||
                        (!StringUtils.isBlank(this.itemName) && !this.itemName.equals(inputStack.getHoverName().getString())))) {
            return inputStack.copy();
        } else {
            return itemStack;
        }
    }
}
