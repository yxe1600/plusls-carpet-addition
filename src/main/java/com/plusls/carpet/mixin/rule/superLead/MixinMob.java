package com.plusls.carpet.mixin.rule.superLead;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MixinMob {
    // 因为村民本身就可以交互，所以原版客户端可以直接用绳子拴住村民
    // 但是怪物本身是不可交互的，因此要想拴住怪物需要在客户端安装 PCA
    @Inject(
            method = "canBeLeashed",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    private void postCanBeLeashed(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (PluslsCarpetAdditionSettings.superLead) {
            cir.setReturnValue(!((Mob) (Object) this).isLeashed());
        }
    }
}
