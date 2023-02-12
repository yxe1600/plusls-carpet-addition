package com.plusls.carpet.mixin.rule.sleepingDuringTheDay;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;
//#if MC <= 11502
//$$ import com.plusls.carpet.PluslsCarpetAdditionSettings;
//$$ import net.minecraft.world.level.Level;
//$$ import org.jetbrains.annotations.NotNull;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//#endif

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = "<=1.15.2"))
@Mixin(Player.class)
public class MixinPlayer {
    //#if MC <= 11502
    //$$ // 白天不会被叫醒
    //$$ @Redirect(
    //$$         method = "tick",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZ)V",
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //$$ void redirectWakeUp(Player player, boolean updateSleepTimer, boolean updateSleepingPlayers) {
    //$$     if (!PluslsCarpetAdditionSettings.sleepingDuringTheDay) {
    //$$         player.stopSleepInBed(updateSleepTimer, updateSleepingPlayers);
    //$$     }
    //$$ }
    //$$
    //$$ // 在白天也能睡觉
    //$$ @Redirect(
    //$$         method = "startSleepInBed",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/level/Level;isDay()Z",
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //$$ boolean redirectIsDay(@NotNull Level level) {
    //$$     boolean ret = level.isDay();
    //$$     if (ret && PluslsCarpetAdditionSettings.sleepingDuringTheDay) {
    //$$         ret = false;
    //$$     }
    //$$     return ret;
    //$$ }
    //#endif
}
