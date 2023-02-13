package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.util.rule.gravestone.DeathInfo;
import com.plusls.carpet.util.rule.gravestone.MySkullBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.compat.minecraft.nbt.TagCompatApi;

//#if MC <= 11701
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif
//#if MC <= 11605
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

@Mixin(SkullBlockEntity.class)
public class MixinSkullBlockEntity implements MySkullBlockEntity {
    private DeathInfo pca$deathInfo;

    @Override
    public DeathInfo getDeathInfo() {
        return this.pca$deathInfo;
    }

    @Override
    public void setDeathInfo(DeathInfo deathInfo) {
        this.pca$deathInfo = deathInfo;
    }

    @Inject(
            method = "load",
            at = @At(
                    value = "RETURN"
            )
    )
    //#if MC > 11605
    private void postLoad(@NotNull CompoundTag compoundTag, CallbackInfo ci) {
    //#elseif MC > 11502
    //$$ private void postLoad(BlockState blockState, @NotNull CompoundTag compoundTag, CallbackInfo ci) {
    //#else
    //$$ private void postLoad(@NotNull CompoundTag compoundTag, CallbackInfo ci) {
    //#endif
        if (compoundTag.contains("DeathInfo", TagCompatApi.TAG_COMPOUND)) {
            this.pca$deathInfo = DeathInfo.fromTag(compoundTag.getCompound("DeathInfo"));
        }
    }

    @Inject(
            //#if MC > 11701
            method = "saveAdditional",
            //#else
            //$$ method = "save",
            //#endif
            at = @At(
                    value = "RETURN"
            )
    )
    //#if MC > 11701
    private void postSaveAdditional(CompoundTag compoundTag, CallbackInfo ci) {
    //#else
    //$$ private void postSave(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
    //#endif
        if (this.pca$deathInfo != null) {
            compoundTag.put("DeathInfo", this.pca$deathInfo.toTag());
        }
    }
}
