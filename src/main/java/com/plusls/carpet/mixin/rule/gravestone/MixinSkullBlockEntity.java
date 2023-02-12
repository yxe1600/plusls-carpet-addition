package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.util.rule.gravestone.DeathInfo;
import com.plusls.carpet.util.rule.gravestone.MySkullBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC <= 11701
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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
    private void postLoad(@NotNull CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("DeathInfo", Tag.TAG_COMPOUND)) {
            this.pca$deathInfo = DeathInfo.fromTag(nbt.getCompound("DeathInfo"));
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
