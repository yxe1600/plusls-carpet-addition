package com.plusls.carpet.mixin.rule.autoTrade;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.autoTrade.MyVillagerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {
    private static final DefaultDispenseItemBehavior pca$itemDispenserBehavior = new DefaultDispenseItemBehavior();

    private static void pca$depleteItemInInventory(@NotNull ItemStack itemStack, Container container) {
        Item item = itemStack.getItem();
        for (int i = 0; !itemStack.isEmpty() && i < container.getContainerSize(); ++i) {
            ItemStack tmpItemStack = container.getItem(i);
            if (!tmpItemStack.isEmpty() && tmpItemStack.is(item)) {
                int count = Math.min(itemStack.getCount(), tmpItemStack.getCount());
                itemStack.setCount(itemStack.getCount() - count);
                tmpItemStack.setCount(tmpItemStack.getCount() - count);
            }
        }
    }

    private static ItemStack pca$getItemFromInventory(@NotNull ItemStack itemStack, Container container) {
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = itemStack.getItem();
        ItemStack ret = new ItemStack(item, 0);
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack tmpStack = container.getItem(i);
            if (!tmpStack.isEmpty() && tmpStack.is(item)) {
                ret.setCount(Math.min(tmpStack.getCount() + ret.getCount(), ret.getMaxStackSize()));
                if (ret.getCount() == ret.getMaxStackSize()) {
                    break;
                }
            }
        }
        return ret;
    }

    @Inject(method = "dispenseFrom", at = @At(value = "HEAD"), cancellable = true)
    private void autoTrade(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci) {
        if (!PluslsCarpetAdditionSettings.autoTrade) {
            return;
        }
        BlockState state = serverLevel.getBlockState(blockPos.below());
        boolean tradeAll;
        if (state.is(Blocks.EMERALD_BLOCK)) {
            tradeAll = false;
        } else if (state.is(Blocks.DIAMOND_BLOCK)) {
            tradeAll = true;
        } else {
            return;
        }
        BlockPos faceBlockPos = blockPos.relative(serverLevel.getBlockState(blockPos).getValue(DispenserBlock.FACING));
        List<AbstractVillager> villagerList = serverLevel.getEntitiesOfClass(AbstractVillager.class,
                new AABB(faceBlockPos), Entity::isAlive);
        if (villagerList.size() < 1) {
            return;
        }
        AbstractVillager merchantEntity = villagerList.get(0);
        MerchantOffers offerList = merchantEntity.getOffers();
        if (offerList.size() == 0) {
            return;
        }

        int tradeId = serverLevel.getBestNeighborSignal(blockPos);
        if (tradeId == 0) {
            return;
        }
        MerchantOffer offer = offerList.get(tradeId > offerList.size() ? offerList.size() - 1 : tradeId - 1);
        ItemStack firstItemStack = offer.getCostA();
        ItemStack secondItemStack = offer.getCostB();
        ItemStack firstDepleteItem = firstItemStack.copy();
        ItemStack secondDepleteItem = secondItemStack.copy();

        BlockSourceImpl blockPointerImpl = new BlockSourceImpl(serverLevel, blockPos);
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        if (!(blockEntity instanceof DispenserBlockEntity dispenserBlockEntity)) {
            return;
        }
        boolean success = false;
        while (!offer.isOutOfStock()) {
            ItemStack firstInventoryItemStack = pca$getItemFromInventory(firstItemStack, dispenserBlockEntity);
            ItemStack secondInventoryItemStack = pca$getItemFromInventory(secondItemStack, dispenserBlockEntity);
            int firstItemCount = firstInventoryItemStack.getCount();
            int secondItemCount = secondInventoryItemStack.getCount();
            if (offer.take(firstInventoryItemStack, secondInventoryItemStack)) {
                firstDepleteItem.setCount(firstItemCount - firstInventoryItemStack.getCount());
                secondDepleteItem.setCount(secondItemCount - secondInventoryItemStack.getCount());
                pca$depleteItemInInventory(firstDepleteItem, dispenserBlockEntity);
                pca$depleteItemInInventory(secondDepleteItem, dispenserBlockEntity);
                offer.increaseUses();
                ItemStack outputItemStack = offer.assemble();
                pca$itemDispenserBehavior.dispense(blockPointerImpl, outputItemStack);
                // make villager happy ~
                serverLevel.broadcastEntityEvent(merchantEntity, (byte) 14);
                if (merchantEntity instanceof MyVillagerEntity myVillagerEntity) {
                    myVillagerEntity.pca$tradeWithoutPlayer(offer);
                }
                success = true;
            } else {
                break;
            }
            if (!tradeAll) {
                break;
            }
        }

        if (success) {
            ci.cancel();
        }
    }

}
