package com.plusls.carpet.mixin.rule.powerBoneMeal;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.powerfulBoneMeal.Grow;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class MixinBoneMealItem {
    @Inject(
            method = "growCrop",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    private static void postGrowCrop(ItemStack stack, Level level, BlockPos pos, @NotNull CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValueZ() && level instanceof ServerLevel && PluslsCarpetAdditionSettings.powerfulBoneMeal) {
            BlockState blockState = level.getBlockState(pos);
            info.setReturnValue(Grow.grow(stack, level, pos, blockState.getBlock()));
        }
    }
}
