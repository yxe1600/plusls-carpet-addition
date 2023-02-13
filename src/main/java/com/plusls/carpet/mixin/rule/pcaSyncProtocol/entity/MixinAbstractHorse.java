package com.plusls.carpet.mixin.rule.pcaSyncProtocol.entity;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends Animal implements ContainerListener {
    protected MixinAbstractHorse(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "containerChanged",
            at = @At(
                    value = "HEAD"
            )
    )
    private void updateEntity(Container sender, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update HorseBaseEntity inventory: onInventoryChanged.");
        }
    }
}
