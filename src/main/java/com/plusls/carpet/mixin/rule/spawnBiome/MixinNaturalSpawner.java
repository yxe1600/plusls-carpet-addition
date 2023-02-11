package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NaturalSpawner.class)
public class MixinNaturalSpawner {
    private static final HolderGetter<Biome> pca$lookup = VanillaRegistries.createLookup().asGetterLookup().lookupOrThrow(Registries.BIOME);

    @ModifyVariable(
            method = "mobsAt",
            at = @At(
                    value = "HEAD"
            ),
            ordinal = 0,
            argsOnly = true
    )
    private static Holder<Biome> modifyBiome(Holder<Biome> biomeEntry) {
        if (PluslsCarpetAdditionSettings.spawnBiome != PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DEFAULT) {
            if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DESERT) {
                biomeEntry = pca$lookup.getOrThrow(Biomes.DESERT);
            } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.PLAINS) {
                biomeEntry = pca$lookup.getOrThrow(Biomes.PLAINS);
            } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.THE_END) {
                biomeEntry = pca$lookup.getOrThrow(Biomes.THE_END);
            } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.NETHER_WASTES) {
                biomeEntry = pca$lookup.getOrThrow(Biomes.NETHER_WASTES);
            }
        }
        return biomeEntry;
    }
}
