package com.plusls.carpet.mixin.rule.spawnYRange;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.util.Mth;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC > 11802
import net.minecraft.util.RandomSource;
//#else
//$$ import java.util.Random;
//#endif

@Mixin(NaturalSpawner.class)
public class MixinNaturalSpawner {
    @Redirect(
            method = "getRandomPosWithin",
            at = @At(value = "INVOKE",
                    //#if MC > 11802
                    target = "Lnet/minecraft/util/Mth;randomBetweenInclusive(Lnet/minecraft/util/RandomSource;II)I",
                    //#else
                    //$$ target = "Lnet/minecraft/util/Mth;randomBetweenInclusive(Ljava/util/Random;II)I",
                    //#endif
                    ordinal = 0
            )
    )
    //#if MC > 11802
    private static int modifySpawnY(RandomSource random, int min, int max) {
    //#else
    //$$ private static int modifySpawnY(Random random, int min, int max) {
    //#endif
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
