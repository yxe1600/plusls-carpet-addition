package com.plusls.carpet.mixin.rule.sleepingDuringTheDay;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level implements WorldGenLevel {
    protected MixinServerLevel(WritableLevelData properties, ResourceKey<Level> registryRef, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        //#if MC > 11802
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
        //#else
        //$$ super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed);
        //#endif
    }

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
