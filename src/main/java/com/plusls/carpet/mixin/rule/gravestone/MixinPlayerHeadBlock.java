package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.util.rule.gravestone.DeathInfo;
import com.plusls.carpet.util.rule.gravestone.MySkullBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerHeadBlock.class)
public abstract class MixinPlayerHeadBlock extends SkullBlock {
    protected MixinPlayerHeadBlock(Type skullType, Properties settings) {
        super(skullType, settings);
    }

    @Override
    public void playerDestroy(@NotNull Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        if (blockEntity instanceof MySkullBlockEntity ) {
            DeathInfo deathInfo = ((MySkullBlockEntity) blockEntity).getDeathInfo();
            if (deathInfo == null) {
                super.playerDestroy(level, player, pos, state, blockEntity, stack);
            } else {
                player.awardStat(Stats.BLOCK_MINED.get(this));
                player.causeFoodExhaustion(0.005F);
                // Drop item
                //#if MC > 11502
                for (ItemStack itemStack : deathInfo.inventory.removeAllItems()) {
                //#else
                //$$ for (ItemStack itemStack : deathInfo.inventory) {
                //#endif
                    Block.popResource(level, pos, itemStack);
                }

                // Drop xp
                int xp = deathInfo.xp;
                while (xp > 0) {
                    int spawnedXp = ExperienceOrb.getExperienceValue(xp);
                    xp -= spawnedXp;
                    level.addFreshEntity(new ExperienceOrb(level, pos.getX(), pos.getY(), pos.getZ(), spawnedXp));
                }
            }
        }
    }
}
