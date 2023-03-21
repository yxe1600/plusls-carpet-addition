package com.plusls.carpet;

import carpet.CarpetServer;
import com.plusls.carpet.util.rule.dispenserCollectXp.GlassBottleDispenserBehavior;
import com.plusls.carpet.util.rule.dispenserFixIronGolem.IronIngotDispenserBehavior;
import com.plusls.carpet.util.rule.gravestone.GravestoneUtil;
import com.plusls.carpet.util.rule.potionRecycle.PotionDispenserBehavior;
import com.plusls.carpet.util.rule.sleepingDuringTheDay.SleepUtil;
import net.fabricmc.api.ModInitializer;
import top.hendrixshen.magiclib.carpet.impl.WrappedSettingManager;

public class PluslsCarpetAddition implements ModInitializer {
    @Override
    public void onInitialize() {
        WrappedSettingManager.register(PluslsCarpetAdditionReference.getModIdentifier(),
                PluslsCarpetAdditionExtension.getSettingsManager(), new PluslsCarpetAdditionExtension());
        //#if MC > 11502
        GravestoneUtil.init();
        SleepUtil.init();
        //#endif
        IronIngotDispenserBehavior.init();
        GlassBottleDispenserBehavior.init();
        PotionDispenserBehavior.init();
    }
}
