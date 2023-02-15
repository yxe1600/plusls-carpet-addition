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

//#if MC <= 11802
//$$ import org.spongepowered.asm.mixin.Intrinsic;
//#endif
//#if MC <= 11701
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

@Mixin(PotionItem.class)
public abstract class MixinPotionItem extends Item {
    public MixinPotionItem(Properties settings) {
        super(settings);
    }

    //#if MC <= 11802
    //$$ @Override
    //$$ @Intrinsic
    //$$ public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
    //$$     return super.useOn(useOnContext);
    //$$ }
    //#endif

    @Inject(
            method = "useOn",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void preUseOn(@NotNull UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = useOnContext.getItemInHand();
        Player player = useOnContext.getPlayer();
        if (!PluslsCarpetAdditionSettings.useDyeOnShulkerBox ||
                player == null ||
                itemStack.getItem() != Items.POTION ||
                PotionUtils.getPotion(itemStack) != Potions.WATER) {
            return;
        }
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
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
                    //#if MC > 11701
                    newBlockEntity.loadFromTag(blockEntity.saveWithoutMetadata());
                    //#else
                    //$$ newBlockEntity.loadFromTag(new CompoundTag());
                    //#endif
                    newBlockEntity.setCustomName(blockEntity.getCustomName());
                    newBlockEntity.setChanged();
                    if (!player.isCreative()) {
                        useOnContext.getItemInHand().shrink(1);
                        Objects.requireNonNull(useOnContext.getPlayer()).getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
            //#if MC > 11802
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            //#else
            //$$ cir.setReturnValue(level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.PASS);
            //#endif
        }
    }
}
