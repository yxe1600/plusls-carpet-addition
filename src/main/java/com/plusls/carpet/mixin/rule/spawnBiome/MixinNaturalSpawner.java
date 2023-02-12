package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC > 11701
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
//#endif
//#if MC > 11902
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
//#else
//$$ import net.minecraft.data.BuiltinRegistries;
//#endif

@Mixin(NaturalSpawner.class)
public class MixinNaturalSpawner {
    //#if MC > 11902
    private static final HolderGetter<Biome> pca$lookup = VanillaRegistries.createLookup().asGetterLookup().lookupOrThrow(Registries.BIOME);
    //#endif

    @ModifyVariable(
            method = "mobsAt",
            at = @At(
                    value = "HEAD"
            ),
            ordinal = 0,
            argsOnly = true
    )
    //#if MC > 11701
    private static Holder<Biome> modifyBiome(Holder<Biome> biome) {
    //#else
    //$$ private static Biome modifyBiome(Biome biome) {
    //#endif
        if (PluslsCarpetAdditionSettings.spawnBiome != PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DEFAULT) {
            if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DESERT) {
                biome = pca$getBiome(Biomes.DESERT);
            } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.PLAINS) {
                biome = pca$getBiome(Biomes.PLAINS);
            } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.THE_END) {
                biome = pca$getBiome(Biomes.THE_END);
            } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.NETHER_WASTES) {
                biome = pca$getBiome(Biomes.NETHER_WASTES);
            }
        }
        return biome;
    }

    //#if MC > 11701
    private static @NotNull Holder<Biome> pca$getBiome(ResourceKey<Biome> biome) {
    //#else
    //$$ private static Biome pca$getBiome(ResourceKey<Biome> biome) {
    //#endif
        //#if MC > 11903
        return pca$lookup.getOrThrow(biome);
        //#elseif MC > 11701
        //$$ return Holder.direct(BuiltinRegistries.BIOME.get(biome));
        //#else
        //$$ return BuiltinRegistries.BIOME.get(biome);
        //#endif
    }
}
