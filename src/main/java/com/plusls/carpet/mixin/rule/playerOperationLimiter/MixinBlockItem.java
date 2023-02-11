package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem extends Item {
    public MixinBlockItem(Properties settings) {
        super(settings);
    }

    @Shadow
    protected abstract BlockState getPlacementState(BlockPlaceContext context);

    @Shadow
    public abstract @Nullable BlockPlaceContext updatePlacementContext(BlockPlaceContext blockPlaceContext);

    @Inject(
            method = "place",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void checkOperationCountPerTick(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!PluslsCarpetAdditionSettings.playerOperationLimiter || context.getLevel().isClientSide()) {
            return;
        }

        if (context.canPlace()) {
            BlockPlaceContext itemPlacementContext = this.updatePlacementContext(context);
            SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity) context.getPlayer();
            if (safeServerPlayerEntity != null && itemPlacementContext != null && this.getPlacementState(itemPlacementContext) != null) {
                safeServerPlayerEntity.pca$addPlaceBlockCountPerTick();
                if (!safeServerPlayerEntity.pca$allowOperation()) {
                    cir.setReturnValue(InteractionResult.FAIL);
                }
            }
        }
    }
}
