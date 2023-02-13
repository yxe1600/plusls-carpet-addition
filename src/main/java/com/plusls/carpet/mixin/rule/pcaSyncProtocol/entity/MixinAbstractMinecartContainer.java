package com.plusls.carpet.mixin.rule.pcaSyncProtocol.entity;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartContainer.class)
public abstract class MixinAbstractMinecartContainer extends AbstractMinecart {
    protected MixinAbstractMinecartContainer(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "setChanged",
            at = @At(
                    value = "RETURN"
            )
    )
    private void updateInventory(CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update StorageMinecartEntity inventory.");
        }
    }
}
