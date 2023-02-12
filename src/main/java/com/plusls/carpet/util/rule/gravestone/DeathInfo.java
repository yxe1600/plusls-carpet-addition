package com.plusls.carpet.util.rule.gravestone;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.compat.minecraft.nbt.TagCompatApi;

//#if MC > 11502
import net.minecraft.world.SimpleContainer;
//#else
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.nbt.ListTag;
//$$
//$$ import java.util.ArrayList;
//#endif

public class DeathInfo {
    public final long deathTime;
    public final int xp;
    //#if MC > 11502
    public final SimpleContainer inventory;
    //#else
    //$$ public final ArrayList<ItemStack> inventory;
    //#endif

    //#if MC > 11502
    public DeathInfo(long deathTime, int xp, SimpleContainer inv) {
    //#else
    //$$ public DeathInfo(long deathTime, int xp, ArrayList<ItemStack> inv) {
    //#endif
        this.deathTime = deathTime;
        this.xp = xp;
        this.inventory = inv;
    }

    public static @NotNull DeathInfo fromTag(@NotNull CompoundTag tag) {
        long deathTime = tag.getLong("DeathTime");
        int xp = tag.getInt("XP");
        //#if MC > 11502
        SimpleContainer inventory = new SimpleContainer(GravestoneUtil.PLAYER_INVENTORY_SIZE);
        inventory.fromTag(tag.getList("Items", TagCompatApi.TAG_COMPOUND));
        //#else
        //$$ ArrayList<ItemStack> inventory = new ArrayList<>();
        //$$ DeathInfo.readTagList(inventory, tag.getList("Items", 10));
        //#endif
        return new DeathInfo(deathTime, xp, inventory);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DeathTime", this.deathTime);
        tag.putInt("XP", this.xp);
        //#if MC > 11502
        tag.put("Items", this.inventory.createTag());
        //#else
        //$$ tag.put("Items", DeathInfo.toTagList(this.inventory));
        //#endif
        return tag;
    }

    //#if MC <= 11502
    //$$ public static void readTagList(ArrayList<ItemStack> inventory, @NotNull ListTag listTag) {
    //$$     for (int i = 0; i < listTag.size(); ++i) {
    //$$         ItemStack itemStack = ItemStack.of(listTag.getCompound(i));
    //$$         if (!itemStack.isEmpty()) {
    //$$             inventory.add(itemStack);
    //$$         }
    //$$     }
    //$$ }
    //$$
    //$$ public static @NotNull ListTag toTagList(@NotNull ArrayList<ItemStack> inventory) {
    //$$     ListTag listTag = new ListTag();
    //$$
    //$$     for (ItemStack itemStack : inventory) {
    //$$         if (!itemStack.isEmpty()) {
    //$$             listTag.add(itemStack.save(new CompoundTag()));
    //$$         }
    //$$     }
    //$$
    //$$     return listTag;
    //$$ }
    //#endif
}