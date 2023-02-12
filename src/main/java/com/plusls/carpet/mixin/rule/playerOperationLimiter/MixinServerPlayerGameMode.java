package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC > 11502
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
//#else
//$$ import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
//#endif
//#if MC <= 11802
//$$ import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
//#endif

@Mixin(ServerPlayerGameMode.class)
public abstract class MixinServerPlayerGameMode {
    private static final String pca$instaMineReason = "insta mine";

    @Final
    @Shadow
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

    //#if MC > 11802
    @Shadow
    protected abstract void debugLogging(BlockPos blockPos, boolean bl, int sequence, String reason);
    //#endif

    @Inject(
            method = "destroyAndAck",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    //#if MC > 11802
    private void checkOperationCountPerTick(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
    //#else
    //$$ private void checkOperationCountPerTick(BlockPos pos, ServerboundPlayerActionPacket.Action action, String reason, CallbackInfo ci) {
    //#endif
        if (!PluslsCarpetAdditionSettings.playerOperationLimiter || !reason.equals(pca$instaMineReason)) {
            return;
        }
        SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity) player;
        safeServerPlayerEntity.pca$addInstaBreakCountPerTick();
        if (!safeServerPlayerEntity.pca$allowOperation()) {
            //#if MC > 11502
            this.player.connection.send(new ClientboundBlockUpdatePacket(pos, this.level.getBlockState(pos)));
            //#else
            //$$ this.player.connection.send(new ClientboundBlockBreakAckPacket(pos, this.level.getBlockState(pos), action, false, reason));
            //#endif
            //#if MC > 11802
            this.debugLogging(pos, false, sequence, reason);
            //#endif
            ci.cancel();
        }
    }

}
