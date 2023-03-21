package com.plusls.carpet;

import top.hendrixshen.magiclib.carpet.api.annotation.Rule;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

public class PluslsCarpetAdditionSettings {
    public static final String CREATIVE = "creative";
    public static final String DEBUG = "debug";
    public static final String DISPENSER = "dispenser";
    public static final String FEATURE = "feature";
    public static final String PROTOCOL = "protocol";
    public static final String NEED_CLIENT = "need_client";
    final public static int INT_DISABLE = 114514;
    public static final String xaeroWorldNameNone = "#none";

    @Rule(
            categories = {
                    DISPENSER,
                    FEATURE
            }
    )
    public static boolean autoTrade = false;


    @Rule(
            categories = {
                    FEATURE,
                    NEED_CLIENT
            }
    )
    public static boolean avoidAnvilTooExpensive = false;

    @Rule(
            categories = {
                    CREATIVE
            }
    )
    public static boolean creativePlayerNoDirectKillArmorStand = false;

    @Rule(
            categories = {
                    FEATURE,
                    DISPENSER
            }
    )
    public static boolean dispenserCollectXp = false;

    @Rule(
            categories = {
                    FEATURE,
                    DISPENSER
            }
    )
    public static boolean dispenserFixIronGolem = false;

    @Rule(
            categories = {
                    FEATURE,
                    NEED_CLIENT
            }
    )
    public static boolean emptyShulkerBoxStack = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean flippingTotemOfUndying = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean forceRestock = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean gravestone = false;

    @Rule(
            categories = {
                    DEBUG
            }
    )
    public static boolean pcaDebug = false;


    @Rule(
            categories = {
                    PROTOCOL
            }
    )
    public static PCA_SYNC_PLAYER_ENTITY_OPTIONS pcaSyncPlayerEntity = PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS;

    public enum PCA_SYNC_PLAYER_ENTITY_OPTIONS {
        NOBODY,
        BOT,
        OPS,
        OPS_AND_SELF,
        EVERYONE
    }

    @Rule(
            categories = {
                    PROTOCOL
            }
    )
    public static boolean pcaSyncProtocol = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean playerOperationLimiter = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean playerSit = false;

    @Rule(
            categories = {
                    FEATURE,
                    DISPENSER
            }
    )
    public static boolean potionRecycle = false;

    @Rule(
            categories = {
                    FEATURE,
                    DISPENSER
            }
    )
    public static boolean powerfulBoneMeal = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean quickLeafDecay = false;

    @Rule(
            categories = {
                    FEATURE
            },
            dependencies = @Dependencies(
                    and = @Dependency(
                            value = "minecraft",
                            versionPredicate = ">1.15.2"
                    )
            )
    )
    public static boolean renewableNetheriteEquip = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean sleepingDuringTheDay = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static PCA_SPAWN_BIOME spawnBiome = PCA_SPAWN_BIOME.DEFAULT;

    public enum PCA_SPAWN_BIOME {
        DEFAULT,
        DESERT,
        PLAINS,
        THE_END,
        NETHER_WASTES
    }

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static int spawnYMax = INT_DISABLE;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static int spawnYMin = INT_DISABLE;

    @Rule(
            categories = {
                    FEATURE,
                    NEED_CLIENT
            }
    )
    public static boolean superLead = false;

    @Rule(
            categories = {
                    CREATIVE
            }
    )
    public static boolean trackItemPickupByPlayer = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean useDyeOnShulkerBox = false;

    @Rule(
            categories = {
                    FEATURE
            }
    )
    public static boolean villagersAttractedByEmeraldBlock = false;

    @Rule(
            categories = {
                    PROTOCOL
            },
            strict = false,
            options = {
                    xaeroWorldNameNone
            }
    )
    public static String xaeroWorldName = xaeroWorldNameNone;
}
