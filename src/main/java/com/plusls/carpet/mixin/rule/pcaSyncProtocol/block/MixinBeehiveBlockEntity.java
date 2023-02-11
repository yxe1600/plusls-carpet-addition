package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(BeehiveBlockEntity.class)
public abstract class MixinBeehiveBlockEntity extends BlockEntity {
    public MixinBeehiveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "tickOccupants",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;remove()V",
                    shift = At.Shift.AFTER
            )
    )
    private static void postTickOccupants(Level level, BlockPos blockPos, BlockState blockState, List<BeehiveBlockEntity.BeeData> list, BlockPos blockPos2, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(Objects.requireNonNull(level.getBlockEntity(blockPos)))) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", blockPos);
        }
    }

    @Inject(
            method = "releaseAllOccupants",
            at = @At(
                    value = "RETURN"
            )
    )
    public void postReleaseAllOccupants(BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState, CallbackInfoReturnable<List<Entity>> cir) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this) && cir.getReturnValue() != null) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", this.worldPosition);
        }
    }

    @Inject(
            method = "load",
            at = @At(
                    value = "RETURN"
            )
    )
    public void postLoad(CompoundTag compoundTag, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", this.worldPosition);
        }
    }

    @Inject(
            method = "addOccupantWithPresetTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;discard()V",
                    ordinal = 0
            )
    )
    public void postAddOccupantWithPresetTicks(Entity entity, boolean hasNectar, int ticksInHive, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", this.worldPosition);
        }
    }
}