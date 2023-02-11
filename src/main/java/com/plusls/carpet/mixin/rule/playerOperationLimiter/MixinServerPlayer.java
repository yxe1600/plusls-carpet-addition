package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer implements SafeServerPlayerEntity {
    private int pca$instaBreakCountPerTick = 0;
    private int pca$placeBlockCountPerTick = 0;

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            )
    )
    private void resetOperationCountPerTick(CallbackInfo ci) {
        this.pca$instaBreakCountPerTick = 0;
        this.pca$placeBlockCountPerTick = 0;
    }

    @Override
    public int pca$getInstaBreakCountPerTick() {
        return this.pca$instaBreakCountPerTick;
    }

    @Override
    public int pca$getPlaceBlockCountPerTick() {
        return this.pca$placeBlockCountPerTick;
    }

    @Override
    public void pca$addInstaBreakCountPerTick() {
        ++this.pca$instaBreakCountPerTick;
    }

    @Override
    public void pca$addPlaceBlockCountPerTick() {
        ++this.pca$placeBlockCountPerTick;
    }

    @Override
    public boolean pca$allowOperation() {
        return (this.pca$instaBreakCountPerTick == 0 || this.pca$placeBlockCountPerTick == 0) &&
                (this.pca$instaBreakCountPerTick <= 1 && this.pca$placeBlockCountPerTick <= 2);
    }
}
