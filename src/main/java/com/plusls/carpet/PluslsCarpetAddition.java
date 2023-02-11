package com.plusls.carpet;

import carpet.CarpetServer;
import com.plusls.carpet.util.rule.dispenserCollectXp.GlassBottleDispenserBehavior;
import com.plusls.carpet.util.rule.dispenserFixIronGolem.IronIngotDispenserBehavior;
import com.plusls.carpet.util.rule.gravestone.GravestoneUtil;
import com.plusls.carpet.util.rule.potionRecycle.PotionDispenserBehavior;
import com.plusls.carpet.util.rule.sleepingDuringTheDay.SleepUtil;
import net.fabricmc.api.ModInitializer;
import top.hendrixshen.magiclib.api.rule.WrapperSettingManager;

public class PluslsCarpetAddition implements ModInitializer {
    @Override
    public void onInitialize() {
        WrapperSettingManager.register(PluslsCarpetAdditionReference.getModIdentifier(), PluslsCarpetAdditionExtension.getSettingsManager());
        CarpetServer.manageExtension(new PluslsCarpetAdditionExtension());
        GravestoneUtil.init();
        SleepUtil.init();
        IronIngotDispenserBehavior.init();
        GlassBottleDispenserBehavior.init();
        PotionDispenserBehavior.init();
    }
}
