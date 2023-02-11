package com.plusls.carpet.mixin.rule.gravestone;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluid extends Fluid {
    @Inject(
            method = "canHoldFluid",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    private void checkRail(BlockGetter blockGetter, BlockPos pos, BlockState state, Fluid fluid, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && state.getBlock() instanceof PlayerHeadBlock) {
            BlockEntity blockEntity = blockGetter.getBlockEntity(pos);
            if (blockEntity != null) {
                CompoundTag nbt = blockEntity.saveWithoutMetadata();
                if (nbt.contains("DeathInfo")) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}