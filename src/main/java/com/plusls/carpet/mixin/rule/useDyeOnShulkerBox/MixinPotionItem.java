package com.plusls.carpet.mixin.rule.useDyeOnShulkerBox;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PotionItem.class)
public abstract class MixinPotionItem extends Item {
    public MixinPotionItem(Properties settings) {
        super(settings);
    }

    @Inject(
            method = "useOn",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void useOnBlock(@NotNull UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();
        if (!PluslsCarpetAdditionSettings.useDyeOnShulkerBox ||
                player == null ||
                itemStack.getItem() != Items.POTION ||
                PotionUtils.getPotion(itemStack) != Potions.WATER) {
            return;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof ShulkerBoxBlock &&
                ((ShulkerBoxBlock) block).getColor() != null) {
            if (!level.isClientSide()) {
                ShulkerBoxBlockEntity blockEntity = (ShulkerBoxBlockEntity) level.getBlockEntity(pos);
                BlockState newBlockState = Blocks.SHULKER_BOX.defaultBlockState().
                        setValue(ShulkerBoxBlock.FACING, blockState.getValue(ShulkerBoxBlock.FACING));

                if (level.setBlockAndUpdate(pos, newBlockState)) {
                    ShulkerBoxBlockEntity newBlockEntity = (ShulkerBoxBlockEntity) level.getBlockEntity(pos);
                    assert blockEntity != null;
                    assert newBlockEntity != null;
                    newBlockEntity.loadFromTag(blockEntity.saveWithoutMetadata());
                    newBlockEntity.setCustomName(blockEntity.getCustomName());
                    newBlockEntity.setChanged();
                    if (!player.isCreative()) {
                        context.getItemInHand().shrink(1);
                        Objects.requireNonNull(context.getPlayer()).getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
        }
    }
}
