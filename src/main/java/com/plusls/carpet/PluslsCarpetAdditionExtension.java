package com.plusls.carpet;

import com.mojang.brigadier.CommandDispatcher;
import com.plusls.carpet.network.PcaSyncProtocol;
import com.plusls.carpet.util.rule.flippingTotemOfUndying.FlipCooldown;
import lombok.Getter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import top.hendrixshen.magiclib.api.rule.CarpetExtensionCompatApi;
import top.hendrixshen.magiclib.api.rule.WrapperSettingManager;
import top.hendrixshen.magiclib.tool.doc.CarpetDocumentGenerator;
import top.hendrixshen.magiclib.util.MiscUtil;

@SuppressWarnings("removal")
public class PluslsCarpetAdditionExtension implements CarpetExtensionCompatApi {
    @Getter
    private static final PluslsCarpetAdditionSettingManager settingsManager = new PluslsCarpetAdditionSettingManager(
            PluslsCarpetAdditionReference.getModVersion(),
            PluslsCarpetAdditionReference.getModIdentifier(),
            PluslsCarpetAdditionReference.getCurrentModName());
    @Getter
    private static MinecraftServer server;

    @Override
    public WrapperSettingManager getSettingsManagerCompat() {
        return PluslsCarpetAdditionExtension.settingsManager;
    }

    @Override
    public void registerCommandCompat(CommandDispatcher<CommandSourceStack> dispatcher) {
    }

    @Override
    public void onGameStarted() {
        PluslsCarpetAdditionExtension.settingsManager.parseSettingsClass(PluslsCarpetAdditionSettings.class);
        PluslsCarpetAdditionExtension.settingsManager.registerRuleCallback((source, rule, value) -> {
            if (rule.getName().equals("pcaSyncProtocol")) {
                if (rule.getRule().getBoolValue()) {
                    PcaSyncProtocol.enablePcaSyncProtocolGlobal();
                } else {
                    PcaSyncProtocol.disablePcaSyncProtocolGlobal();
                }
            } else if (rule.getName().equals("pcaDebug")) {
                Configurator.setLevel(PluslsCarpetAdditionReference.getModIdentifier(), MiscUtil.cast(rule.getValue()) ? Level.DEBUG : Level.INFO);
            }
        });
        PcaSyncProtocol.init();
        FlipCooldown.init();
        if (PluslsCarpetAdditionSettings.pcaDebug) {
            PluslsCarpetAdditionReference.getLogger().getName();
            Configurator.setLevel(PluslsCarpetAdditionReference.getModIdentifier(), Level.DEBUG);
        }
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        PluslsCarpetAdditionExtension.server = server;
        CarpetDocumentGenerator doc = new CarpetDocumentGenerator(this.getSettingsManagerCompat().getIdentifier());
        doc.setCurrentLanguageCode("en_us");
        doc.genFile();
        doc.setCurrentLanguageCode("zh_cn");
        doc.genFile();
    }

    @Override
    public void onPlayerLoggedOut(ServerPlayer player) {
        PcaSyncProtocol.clearPlayerWatchData(player);
        FlipCooldown.removePlayer(player);
    }
}