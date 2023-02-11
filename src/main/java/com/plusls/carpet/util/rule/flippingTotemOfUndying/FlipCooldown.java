package com.plusls.carpet.util.rule.flippingTotemOfUndying;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class FlipCooldown {
    static private final Map<Player, Long> cooldownMap = new HashMap<>();

    static public void init() {
        cooldownMap.clear();
    }

    static public long getCoolDown(Player player) {
        return cooldownMap.getOrDefault(player, 0L);
    }

    static public void setCoolDown(Player player, long cooldown) {
        cooldownMap.put(player, cooldown);
    }

    static public void removePlayer(Player player) {
        cooldownMap.remove(player);
    }

}
