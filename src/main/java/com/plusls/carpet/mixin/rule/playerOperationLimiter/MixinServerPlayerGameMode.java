package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public abstract class MixinServerPlayerGameMode {
    private static final String pca$instaMineReason = "insta mine";

    @Final
    @Shadow
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

    @Shadow
    protected abstract void debugLogging(BlockPos blockPos, boolean bl, int sequence, String reason);

    @Inject(
            method = "destroyAndAck",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void checkOperationCountPerTick(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
        if (!PluslsCarpetAdditionSettings.playerOperationLimiter || !reason.equals(pca$instaMineReason)) {
            return;
        }
        SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity) player;
        safeServerPlayerEntity.pca$addInstaBreakCountPerTick();
        if (!safeServerPlayerEntity.pca$allowOperation()) {
            this.player.connection.send(new ClientboundBlockUpdatePacket(pos, this.level.getBlockState(pos)));
            this.debugLogging(pos, false, sequence, reason);
            ci.cancel();
        }
    }

}
