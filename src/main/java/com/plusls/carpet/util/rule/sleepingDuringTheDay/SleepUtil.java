package com.plusls.carpet.util.rule.sleepingDuringTheDay;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.world.InteractionResult;

public class SleepUtil {
    public static void init() {
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            if (PluslsCarpetAdditionSettings.sleepingDuringTheDay) {
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        });
    }
}
