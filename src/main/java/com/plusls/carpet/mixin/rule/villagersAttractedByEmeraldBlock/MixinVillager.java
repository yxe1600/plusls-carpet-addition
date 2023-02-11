package com.plusls.carpet.mixin.rule.villagersAttractedByEmeraldBlock;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class MixinVillager extends AbstractVillager {
    boolean pca$VillagersAttractedByEmeraldBlock;
    private TemptGoal pca$villagersAttractedByEmeraldBlockGoal;

    public MixinVillager(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/npc/VillagerType;)V",
            at = @At(
                    value = "RETURN"
            )
    )
    private void init(EntityType<? extends Villager> entityType, Level world, VillagerType type, CallbackInfo ci) {
        if (this.level.isClientSide()) {
            return;
        }
        this.pca$villagersAttractedByEmeraldBlockGoal = new TemptGoal(this, 1.0D, Ingredient.of(Items.EMERALD_BLOCK), false);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "RETURN"
            )
    )
    private void checkVillagersAttractedByEmeraldBlock(CallbackInfo ci) {
        if (this.level.isClientSide()) {
            return;
        }
        if (!this.pca$VillagersAttractedByEmeraldBlock && PluslsCarpetAdditionSettings.villagersAttractedByEmeraldBlock) {
            if (!this.pca$villagersAttractedByEmeraldBlockGoal.canUse()) {
                this.pca$villagersAttractedByEmeraldBlockGoal = new TemptGoal(this, 1.0D, Ingredient.of(Items.EMERALD_BLOCK), false);
            }
            this.goalSelector.addGoal(0, this.pca$villagersAttractedByEmeraldBlockGoal);
            this.pca$VillagersAttractedByEmeraldBlock = PluslsCarpetAdditionSettings.villagersAttractedByEmeraldBlock;
        } else if (this.pca$VillagersAttractedByEmeraldBlock && !PluslsCarpetAdditionSettings.villagersAttractedByEmeraldBlock) {
            this.goalSelector.removeGoal(this.pca$villagersAttractedByEmeraldBlockGoal);
            this.pca$VillagersAttractedByEmeraldBlock = PluslsCarpetAdditionSettings.villagersAttractedByEmeraldBlock;
        }
    }
}