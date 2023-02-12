package com.plusls.carpet.util.rule.sleepingDuringTheDay;

//#if MC > 11502
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.world.InteractionResult;
//#endif

public class SleepUtil {
    //#if MC > 11502
    public static void init() {
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            if (PluslsCarpetAdditionSettings.sleepingDuringTheDay) {
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        });
    }
    //#endif
}
