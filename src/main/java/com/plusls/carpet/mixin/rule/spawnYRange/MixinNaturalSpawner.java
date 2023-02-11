package com.plusls.carpet.mixin.rule.spawnYRange;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
public class MixinNaturalSpawner {
    @Redirect(
            method = "getRandomPosWithin",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;randomBetweenInclusive(Lnet/minecraft/util/RandomSource;II)I",
                    ordinal = 0
            )
    )
    private static int modifySpawnY(RandomSource random, int min, int max) {
        if (PluslsCarpetAdditionSettings.spawnYMax != PluslsCarpetAdditionSettings.INT_DISABLE) {
            max = PluslsCarpetAdditionSettings.spawnYMax;
        }
        if (PluslsCarpetAdditionSettings.spawnYMin != PluslsCarpetAdditionSettings.INT_DISABLE) {
            min = PluslsCarpetAdditionSettings.spawnYMin;
        }
        if (min >= max) {
            max = min + 1;
        }
        return Mth.randomBetweenInclusive(random, min, max);
    }
}
