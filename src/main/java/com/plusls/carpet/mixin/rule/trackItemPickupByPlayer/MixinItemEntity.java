package com.plusls.carpet.mixin.rule.trackItemPickupByPlayer;

import com.plusls.carpet.PluslsCarpetAdditionExtension;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.compat.minecraft.api.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.util.MessageUtil;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    private boolean pca$pickuped = false;
    private int pca$trackItemPickupByPlayerCooldown = 0;

    public MixinItemEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract void setItem(ItemStack itemStack);

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void prevTick(CallbackInfo ci) {
        if (!this.getLevelCompat().isClientSide() && PluslsCarpetAdditionSettings.trackItemPickupByPlayer && pca$pickuped) {
            ci.cancel();
        }
    }

    @Inject(
            method = "playerTouch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void checkPickup(Player player, CallbackInfo ci) {
        if (!this.getLevelCompat().isClientSide() && PluslsCarpetAdditionSettings.trackItemPickupByPlayer && PluslsCarpetAdditionExtension.getServer() != null) {
            if (pca$trackItemPickupByPlayerCooldown == 0) {
                MessageUtil.sendServerMessage(PluslsCarpetAdditionExtension.getServer(),
                        ComponentCompatApi.literal(StringUtil.tr("pca.message.pickup", player.getName().getString(),
                                this.getX(), this.getY(), this.getZ(),
                                this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z())));
            }
            pca$trackItemPickupByPlayerCooldown = (pca$trackItemPickupByPlayerCooldown + 1) % 100;
            pca$pickuped = true;
            this.setItem(new ItemStack(Items.BARRIER));
            this.setNoGravity(true);
            this.noPhysics = true;
            this.setDeltaMovement(0, 0, 0);
            ci.cancel();
        }
    }
}
