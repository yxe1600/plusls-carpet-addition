package com.plusls.carpet.mixin.rule.autoTrade;

import com.plusls.carpet.util.rule.autoTrade.MyVillagerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Villager.class)
public abstract class MixinVillager extends AbstractVillager implements MyVillagerEntity {
    @Shadow
    private int villagerXp;

    @Shadow
    protected abstract boolean shouldIncreaseLevel();

    @Shadow
    private int updateMerchantTimer;

    @Shadow
    private boolean increaseProfessionLevelOnUpdate;

    public MixinVillager(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    public void pca$tradeWithoutPlayer(@NotNull MerchantOffer offer) {
        this.villagerXp += offer.getXp();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
        }
    }
}
