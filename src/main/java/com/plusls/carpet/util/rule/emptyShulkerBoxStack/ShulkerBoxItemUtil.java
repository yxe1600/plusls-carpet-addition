package com.plusls.carpet.util.rule.emptyShulkerBoxStack;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxItemUtil {
    public static final int SHULKERBOX_MAX_STACK_AMOUNT = 64;

    public static boolean isEmptyShulkerBoxItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem &&
                ((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
                CompoundTag tag = nbt.getCompound("BlockEntityTag");
                if (tag.contains("Items", 9)) {
                    ListTag tagList = tag.getList("Items", 10);
                    return tagList.size() <= 0;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static int getMaxCount(ItemStack itemStack) {
        if (PluslsCarpetAdditionSettings.emptyShulkerBoxStack && ShulkerBoxItemUtil.isEmptyShulkerBoxItem(itemStack)) {
            return ShulkerBoxItemUtil.SHULKERBOX_MAX_STACK_AMOUNT;
        } else {
            return itemStack.getMaxStackSize();
        }
    }

    public static boolean isStackable(ItemStack itemStack) {
        return getMaxCount(itemStack) > 1 && (!itemStack.isDamageableItem() || !itemStack.isDamaged());
    }
}