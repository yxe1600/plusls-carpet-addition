package com.plusls.carpet.mixin.rule.spawnYRange;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC > 11605
import net.minecraft.util.Mth;
//#endif
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
                    ordinal = 0
                    //#elseif MC > 11605
                    //$$ target = "Lnet/minecraft/util/Mth;randomBetweenInclusive(Ljava/util/Random;II)I",
                    //$$ ordinal = 0
                    //#else
                    //$$ target = "Ljava/util/Random;nextInt(I)I",
                    //$$ ordinal = 2
                    //#endif
            )
    )
    //#if MC > 11802
    private static int modifySpawnY(RandomSource random, int min, int max) {
    //#elseif MC > 11605
    //$$ private static int modifySpawnY(Random random, int min, int max) {
    //#else
    //$$ private static int modifySpawnY(Random random, int bound) {
    //$$     int max = bound, min = 0;
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
        //#if MC > 11605
        return Mth.randomBetweenInclusive(random, min, max);
        //#else
        //$$ int newBound = max - min;
        //$$ return random.nextInt(newBound) + min;
        //#endif
    }
}
