package com.plusls.carpet.mixin.rule.flippingTotemOfUndying;

import carpet.CarpetSettings;
import carpet.helpers.BlockRotator;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.rule.flippingTotemOfUndying.FlipCooldown;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRotator.class)
public class MixinBlockRotator {
    private static boolean pca$playerHoldsTotemOfUndyingMainHand(@NotNull Player player) {
        return player.getMainHandItem().getItem() == Items.TOTEM_OF_UNDYING;
    }

    @Inject(
            method = "flipBlockWithCactus",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true,
            remap = false
    )
    private static void postFlipBlockWithCactus(BlockState state, Level level, Player player, InteractionHand hand, BlockHitResult hit, @NotNull CallbackInfoReturnable<Boolean> cir) {
        // 不知道为什么 同一 gt 内会收到 2 个包
        // it works
        if (!cir.getReturnValue() && PluslsCarpetAdditionSettings.flippingTotemOfUndying &&
                level.getGameTime() != FlipCooldown.getCoolDown(player)) {
            // 能修改世界且副手为空
            if (!player.getAbilitiesCompat().mayBuild ||
                    !pca$playerHoldsTotemOfUndyingMainHand(player) ||
                    !player.getOffhandItem().isEmpty()) {
                return;
            }
            //#if MC > 11502
            CarpetSettings.impendingFillSkipUpdates.set(true);
            //#else
            //$$ CarpetSettings.impendingFillSkipUpdates = true;
            //#endif
            boolean ret = BlockRotator.flipBlock(state, level, player, hand, hit);
            //#if MC > 11502
            CarpetSettings.impendingFillSkipUpdates.set(false);
            //#else
            //$$ CarpetSettings.impendingFillSkipUpdates = false;
            //#endif
            if (ret) {
                FlipCooldown.setCoolDown(player, level.getGameTime());
            }
            cir.setReturnValue(ret);
        }
    }

    @Inject(
            method = "flippinEligibility",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true,
            remap = false
    )
    private static void postFlippinEligibility(Entity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && PluslsCarpetAdditionSettings.flippingTotemOfUndying && (entity instanceof Player)) {
            Player player = (Player) entity;
            // 副手不为空，主手为图腾
            boolean ret = !player.getOffhandItem().isEmpty() && pca$playerHoldsTotemOfUndyingMainHand(player);
            cir.setReturnValue(ret);
        }
    }
}