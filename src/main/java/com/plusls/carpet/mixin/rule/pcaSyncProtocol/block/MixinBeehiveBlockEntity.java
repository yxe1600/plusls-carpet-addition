package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

//#if MC > 11404
import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

//#if MC > 11605
import net.minecraft.world.level.Level;
import java.util.Objects;
//#endif
//#else
//$$ import net.minecraft.server.MinecraftServer;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = ">1.14.4"))
//#if MC > 11404
@Mixin(BeehiveBlockEntity.class)
public abstract class MixinBeehiveBlockEntity extends BlockEntity {
//#else
//$$ @Mixin(MinecraftServer.class)
//$$ public class MixinBeehiveBlockEntity {
//#endif
    //#if MC > 11404
    //#if MC > 11605
    protected MixinBeehiveBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    //#else
    //$$ protected MixinBeehiveBlockEntity(BlockEntityType<?> blockEntityType) {
    //$$     super(blockEntityType);
    //$$ }
    //#endif

    @Inject(
            method = "tickOccupants",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;remove()V",
                    shift = At.Shift.AFTER
            )
    )
    //#if MC > 11605
    private static void postTickOccupants(Level level, BlockPos blockPos, BlockState blockState, List<BeehiveBlockEntity.BeeData> list, BlockPos blockPos2, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(Objects.requireNonNull(level.getBlockEntity(blockPos)))) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", blockPos);
    //#else
    //$$ private void postTickOccupants(CallbackInfo ci) {
    //$$     if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
    //$$         PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", this.worldPosition);
    //#endif
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
    //#if MC > 11605
    public void postLoad(CompoundTag compoundTag, CallbackInfo ci) {
    //#elseif MC > 11502
    //$$ public void postLoad(BlockState blockState, CompoundTag compoundTag, CallbackInfo ci) {
    //#else
    //$$ public void postLoad(CompoundTag compoundTag, CallbackInfo ci) {
    //#endif
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", this.worldPosition);
        }
    }

    @Inject(
            method = "addOccupantWithPresetTicks",
            at = @At(
                    value = "INVOKE",
                    //#if MC > 11605
                    target = "Lnet/minecraft/world/entity/Entity;discard()V",
                    //#else
                    //$$ target = "Lnet/minecraft/world/entity/Entity;remove()V",
                    //#endif
                    ordinal = 0
            )
    )
    public void postAddOccupantWithPresetTicks(Entity entity, boolean hasNectar, int ticksInHive, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update BeehiveBlockEntity: {}", this.worldPosition);
        }
    }
    //#endif
}