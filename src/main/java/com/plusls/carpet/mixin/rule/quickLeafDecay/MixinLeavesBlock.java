package com.plusls.carpet.mixin.rule.quickLeafDecay;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public abstract class MixinLeavesBlock extends Block {
    public MixinLeavesBlock(Properties settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Inject(
            method = "tick",
            at = @At(
                    "RETURN"
            )
    )
    private void postScheduledTick(BlockState state, ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.quickLeafDecay) {
            this.randomTick(state, world, pos, random);
        }
    }
}
