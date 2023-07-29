package com.plusls.carpet.mixin.rule.playerSit;

import com.mojang.authlib.GameProfile;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.playerSit.SitEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC > 11802 && MC < 11903
//$$ import net.minecraft.world.entity.player.ProfilePublicKey;
//$$ import org.jetbrains.annotations.Nullable;
//#endif

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {
    //#if MC > 11902
    public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }
    //#elseif MC > 11802
    //$$ public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
    //$$     super(level, blockPos, f, gameProfile, profilePublicKey);
    //$$ }
    //#elseif MC > 11502
    //$$ public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
    //$$     super(level, blockPos, f, gameProfile);
    //$$ }
    //#else
    //$$ public MixinServerPlayer(Level level, GameProfile gameProfile) {
    //$$     super(level, gameProfile);
    //$$ }
    //#endif

    @Shadow
    public ServerGamePacketListenerImpl connection;
    @Unique
    private int pca$sneakTimes = 0;
    @Unique
    private long pca$lastSneakTime = 0;

    @Override
    @Intrinsic
    public void setShiftKeyDown(boolean sneaking) {
        super.setShiftKeyDown(sneaking);
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "target"})
    @Inject(
            //#if MC > 11404
            method = "setShiftKeyDown(Z)V",
            //#else
            //$$ method = "setSneaking(Z)V",
            //#endif
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void customSetShiftKeyDownCheck(boolean sneaking, CallbackInfo ci) {
        if (!PluslsCarpetAdditionSettings.playerSit) {
            return;
        }

        if (sneaking) {
            long nowTime = Util.getMillis();

            // Every sneak interval must not be over 0.2s
            if (nowTime - this.pca$lastSneakTime > 200) {
                this.pca$sneakTimes = 0;
            } else {
                // Block input update for 0.2s after player sit
                ci.cancel();
            }

            if (this.onGround()) {
                this.pca$sneakTimes++;
            }

            this.pca$lastSneakTime = nowTime;

            if (this.pca$sneakTimes > 2) {
                ArmorStand armorStandEntity = new ArmorStand(this.getLevelCompat(), this.getX(), this.getY() - 0.16, this.getZ());
                ((SitEntity) armorStandEntity).pca$setSitEntity(true);
                this.getLevelCompat().addFreshEntity(armorStandEntity);
                this.setShiftKeyDown(false);

                if (this.connection != null) {
                    this.connection.send(new ClientboundSetEntityDataPacket(
                            this.getId(),
                            //#if MC > 11902
                            this.getEntityData().getNonDefaultValues()
                            //#else
                            //$$ this.getEntityData(),
                            //$$ true
                            //#endif
                    ));
                }

                this.startRiding(armorStandEntity);
                this.pca$sneakTimes = 0;
                ci.cancel();
            }
        }
    }
}
