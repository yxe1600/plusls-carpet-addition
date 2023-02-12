package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC > 11502
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.resources.ResourceKey;
//#endif
//#if MC > 11701
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
//#endif
//#if MC > 11902
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
//#elseif MC > 11502
//$$ import net.minecraft.data.BuiltinRegistries;
//#else
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.entity.MobCategory;
//$$ import net.minecraft.world.level.chunk.ChunkGenerator;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import java.util.List;
//#endif
@Mixin(NaturalSpawner.class)
public class MixinNaturalSpawner {
    //#if MC > 11902
    private static final HolderGetter<Biome> pca$lookup = VanillaRegistries.createLookup().asGetterLookup().lookupOrThrow(Registries.BIOME);
    //#endif

    //#if MC > 11502
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
    //#elseif MC > 11502
    //$$ private static Biome pca$getBiome(ResourceKey<Biome> biome) {
    //#else
    //$$ private static Biome pca$getBiome(Biome biome) {
    //#endif
        //#if MC > 11903
        return pca$lookup.getOrThrow(biome);
        //#elseif MC > 11701
        //$$ return Holder.direct(BuiltinRegistries.BIOME.get(biome));
        //#elseif MC > 11502
        //$$ return BuiltinRegistries.BIOME.get(biome);
        //#endif
    }
    //#else
    //$$ @Redirect(
    //$$         method = "getRandomSpawnMobAt",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;getMobsAt(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/core/BlockPos;)Ljava/util/List;",
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //$$ private static List<Biome.SpawnerData> modifyBiome0(ChunkGenerator<?> chunkGenerator, MobCategory category, BlockPos pos) {
    //$$     return modifyBiome(chunkGenerator, category, pos);
    //$$ }
    //$$
    //$$ @Redirect(
    //$$         method = "canSpawnMobAt",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;getMobsAt(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/core/BlockPos;)Ljava/util/List;",
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //$$ private static List<Biome.SpawnerData> modifyBiome1(ChunkGenerator<?> chunkGenerator, MobCategory category, BlockPos pos) {
    //$$     return modifyBiome(chunkGenerator, category, pos);
    //$$ }
    //$$
    //$$ @Redirect(
    //$$         method = "spawnMobsForChunkGeneration",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/level/biome/Biome;getMobs(Lnet/minecraft/world/entity/MobCategory;)Ljava/util/List;",
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //$$ private static List<Biome.SpawnerData> modifyBiome2(Biome biome, MobCategory category) {
    //$$     if (PluslsCarpetAdditionSettings.spawnBiome != PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DEFAULT) {
    //$$         if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DESERT) {
    //$$             biome = Biomes.DESERT;
    //$$         } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.PLAINS) {
    //$$             biome = Biomes.DESERT;
    //$$         } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.THE_END) {
    //$$             biome = Biomes.THE_END;
    //$$         } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.NETHER_WASTES) {
    //$$             biome = Biomes.NETHER;
    //$$         }
    //$$     }
    //$$     return biome.getMobs(category);
    //$$ }
    //$$
    //$$ private static List<Biome.SpawnerData> modifyBiome(ChunkGenerator<?> chunkGenerator, MobCategory category, BlockPos pos) {
    //$$     if (PluslsCarpetAdditionSettings.spawnBiome != PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DEFAULT) {
    //$$         if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.DESERT) {
    //$$             return Biomes.DESERT.getMobs(category);
    //$$         } else if (PluslsCarpetAdditionSettings.spawnBiome == PluslsCarpetAdditionSettings.PCA_SPAWN_BIOME.PLAINS) {
    //$$             return Biomes.PLAINS.getMobs(category);
    //$$         }
    //$$     }
    //$$     return chunkGenerator.getMobsAt(category, pos);
    //$$ }
    //#endif
}
