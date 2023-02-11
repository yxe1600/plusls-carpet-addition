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

//#if MC > 11802
import net.minecraft.util.RandomSource;
//#else
//$$ import java.util.Random;
//#endif

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
    //#if MC > 11802
    private void postScheduledTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
    //#else
    //$$ private void postScheduledTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci) {
    //#endif
        if (PluslsCarpetAdditionSettings.quickLeafDecay) {
            this.randomTick(state, level, pos, random);
        }
    }
}
