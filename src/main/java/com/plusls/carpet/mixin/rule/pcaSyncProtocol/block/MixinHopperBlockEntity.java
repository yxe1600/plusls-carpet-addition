package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

//#if MC > 11605
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;
//#endif

@Mixin(HopperBlockEntity.class)
public abstract class MixinHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    //#if MC > 11605
    protected MixinHopperBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    //#else
    //$$ protected MixinHopperBlockEntity(BlockEntityType<?> blockEntityType) {
    //$$     super(blockEntityType);
    //$$ }
    //#endif

    //#if MC > 11605
    @Inject(
            method = "tryMoveItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setChanged(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private static void onSetChanged(Level world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(blockEntity)) {
            PluslsCarpetAdditionReference.getLogger().debug("update HopperBlockEntity: {}", pos);
        }
    }
    //#endif

    @Override
    public void setChanged() {
        super.setChanged();
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update HopperBlockEntity: {}", this.worldPosition);
        }
    }
}