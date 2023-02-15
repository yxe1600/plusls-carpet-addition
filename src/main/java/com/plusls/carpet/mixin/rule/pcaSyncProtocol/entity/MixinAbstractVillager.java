package com.plusls.carpet.mixin.rule.pcaSyncProtocol.entity;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public abstract class MixinAbstractVillager extends AgeableMob implements ContainerListener {
    @Final
    @Shadow
    private SimpleContainer inventory;

    protected MixinAbstractVillager(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void addInventoryListener(EntityType<? extends AbstractVillager> entityType, Level world, CallbackInfo info) {
        if (this.level.isClientSide()) {
            return;
        }
        this.inventory.addListener(this);
    }

    @Override
    @Intrinsic
    public void containerChanged(Container inventory) {
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Inject(
            method = "containerChanged(Lnet/minecraft/world/Container;)V",
            at = @At(
                    value = "RETURN"
            )
    )
    public void postContainerChanged(Container inventory, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update villager inventory: onInventoryChanged.");
        }
    }
}