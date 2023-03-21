package com.plusls.carpet.mixin.rule.sleepingDuringTheDay;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC > 11502
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.WritableLevelData;
import java.util.function.Supplier;
//#else
//$$ import net.minecraft.world.level.chunk.ChunkSource;
//$$ import net.minecraft.world.level.dimension.Dimension;
//$$ import net.minecraft.world.level.storage.LevelData;
//$$
//$$ import java.util.function.BiFunction;
//#endif
//#if MC > 11701
import net.minecraft.core.Holder;
//#endif

//#if MC > 11903
import net.minecraft.core.RegistryAccess;
//#endif

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level {
    //#if MC > 11903
    protected MixinServerLevel(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryAccess, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryAccess, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }
    //#elseif MC > 11802
    //$$ protected MixinServerLevel(WritableLevelData properties, ResourceKey<Level> registryRef, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
    //$$     super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    //$$ }
    //#elseif MC > 11701
    //$$ protected MixinServerLevel(WritableLevelData properties, ResourceKey<Level> registryRef, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed) {
    //$$     super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed);
    //$$ }
    //#elseif MC > 11502
    //$$ protected MixinServerLevel(WritableLevelData properties, ResourceKey<Level> registryRef, DimensionType dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed) {
    //$$     super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed);
    //$$ }
    //#else
    //$$ protected MixinServerLevel(LevelData levelData, DimensionType dimensionType, BiFunction<Level, Dimension, ChunkSource> biFunction, ProfilerFiller profilerFiller, boolean bl) {
    //$$     super(levelData, dimensionType, biFunction, profilerFiller, bl);
    //$$ }
    //#endif

    // 根据当前时间设置夜晚和白天
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V",
                    ordinal = 0
            )
    )
    void onSetTimeOfDay(ServerLevel world, long timeOfDay) {
        if (this.isDay() && PluslsCarpetAdditionSettings.sleepingDuringTheDay) {
            long currentTime = this.levelData.getDayTime();
            long currentDayTime = this.levelData.getDayTime() % 24000L;
            world.setDayTime(currentTime + 13000L - currentDayTime);
        } else {
            world.setDayTime(timeOfDay);
        }
    }
}
