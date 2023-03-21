package com.plusls.carpet.mixin.rule.sleepingDuringTheDay;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;
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
    //#if MC > 11404
    //$$                 target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZ)V",
    //#else
    //$$                 target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZZ)V",
    //#endif
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //#if MC > 11404
    //$$ void redirectWakeUp(Player player, boolean updateSleepTimer, boolean updateSleepingPlayers) {
    //#else
    //$$ void redirectWakeUp(Player player, boolean updateSleepTimer, boolean updateSleepingPlayers, boolean setSpawnPoint) {
    //#endif
    //$$     if (!PluslsCarpetAdditionSettings.sleepingDuringTheDay) {
    //#if MC > 11404
    //$$         player.stopSleepInBed(updateSleepTimer, updateSleepingPlayers);
    //#else
    //$$         player.stopSleepInBed(updateSleepTimer, updateSleepingPlayers, setSpawnPoint);
    //#endif
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
