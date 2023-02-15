package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComparatorBlockEntity.class)
public abstract class MixinComparatorBlockEntity extends BlockEntity {
    //#if MC > 11605
    protected MixinComparatorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    //#else
    //$$ protected MixinComparatorBlockEntity(BlockEntityType<?> blockEntityType) {
    //$$     super(blockEntityType);
    //$$ }
    //#endif

    @Override
    @Intrinsic
    public void setChanged() {
        super.setChanged();
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "target"})
    @Inject(
            method = "setChanged()V",
            at = @At(
                    value = "RETURN"
            )
    )
    private void postSetChanged(CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PluslsCarpetAdditionReference.getLogger().debug("update ComparatorBlockEntity: {}", this.worldPosition);
        }
    }
}
