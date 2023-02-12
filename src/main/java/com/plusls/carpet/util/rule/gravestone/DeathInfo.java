package com.plusls.carpet.util.rule.gravestone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import top.hendrixshen.magiclib.compat.minecraft.nbt.TagCompatApi;

public class DeathInfo {
    public final long deathTime;
    public final int xp;
    public final SimpleContainer inventory;

    public DeathInfo(long deathTime, int xp, SimpleContainer inv) {
        this.deathTime = deathTime;
        this.xp = xp;
        this.inventory = inv;
    }

    public static DeathInfo fromTag(CompoundTag tag) {
        long deathTime = tag.getLong("DeathTime");
        int xp = tag.getInt("XP");
        SimpleContainer inventory = new SimpleContainer(GravestoneUtil.PLAYER_INVENTORY_SIZE);
        inventory.fromTag(tag.getList("Items", TagCompatApi.TAG_COMPOUND));
        return new DeathInfo(deathTime, xp, inventory);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DeathTime", this.deathTime);
        tag.putInt("XP", this.xp);
        tag.put("Items", this.inventory.createTag());
        return tag;
    }
}