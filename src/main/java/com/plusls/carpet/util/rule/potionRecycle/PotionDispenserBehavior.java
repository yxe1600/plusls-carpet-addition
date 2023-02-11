package com.plusls.carpet.util.rule.potionRecycle;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.dispenser.MyFallibleItemDispenserBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionDispenserBehavior extends MyFallibleItemDispenserBehavior {

    public PotionDispenserBehavior(DispenseItemBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    public static void init() {
        DispenserBlock.registerBehavior(Items.POTION,
                new PotionDispenserBehavior(DispenserBlock.DISPENSER_REGISTRY.get(Items.POTION)));
        DispenserBlock.registerBehavior(Items.SPLASH_POTION,
                new PotionDispenserBehavior(DispenserBlock.DISPENSER_REGISTRY.get(Items.SPLASH_POTION)));
        DispenserBlock.registerBehavior(Items.LINGERING_POTION,
                new PotionDispenserBehavior(DispenserBlock.DISPENSER_REGISTRY.get(Items.LINGERING_POTION)));
    }

    @Override
    public ItemStack dispenseSilently(BlockSource pointer, ItemStack itemStack) {
        if (!PluslsCarpetAdditionSettings.potionRecycle) {
            return itemStack;
        }
        BlockPos faceBlockPos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));
        ServerLevel world = pointer.getLevel();
        BlockState faceBlockState = world.getBlockState(faceBlockPos);
        if (faceBlockState.getBlock() instanceof AbstractCauldronBlock) {
            setSuccess(true);
            if (faceBlockState.getBlock() == Blocks.WATER_CAULDRON) {
                int level = faceBlockState.getValue(LayeredCauldronBlock.LEVEL);
                if (level == 3) {
                    return itemStack;
                } else {
                    world.setBlockAndUpdate(faceBlockPos, faceBlockState.setValue(LayeredCauldronBlock.LEVEL, level + 1));
                    world.playSound(null, faceBlockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.gameEvent(null, GameEvent.FLUID_PLACE, faceBlockPos);
                    return new ItemStack(Items.GLASS_BOTTLE);

                }
            } else if (faceBlockState.getBlock() == Blocks.CAULDRON) {
                world.setBlockAndUpdate(faceBlockPos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1));
                world.playSound(null, faceBlockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(null, GameEvent.FLUID_PLACE, faceBlockPos);
                return new ItemStack(Items.GLASS_BOTTLE);
            }
        }
        return itemStack;
    }
}
