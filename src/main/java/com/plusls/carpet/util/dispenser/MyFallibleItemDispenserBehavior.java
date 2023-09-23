package com.plusls.carpet.util.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

//#if MC > 12001
//$$ import net.minecraft.core.dispenser.BlockSource;
//#else
import net.minecraft.core.BlockSource;
//#endif

public abstract class MyFallibleItemDispenserBehavior extends MyDispenserBehavior {
    private boolean success = false;

    public MyFallibleItemDispenserBehavior(DispenseItemBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    @Override
    public final ItemStack dispense(BlockSource blockPointer, ItemStack itemStack) {
        setSuccess(false);
        ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
        if (!isSuccess()) {
            return super.dispense(blockPointer, itemStack);
        }
        this.playSound(blockPointer);
        this.spawnParticles(blockPointer, blockPointer.getBlockState().getValue(DispenserBlock.FACING));
        return itemStack2;
    }

    public ItemStack dispenseSilently(BlockSource pointer, ItemStack stack) {
        Direction direction = pointer.getBlockState().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(pointer);
        ItemStack itemStack = stack.split(1);
        DefaultDispenseItemBehavior.spawnItem(pointer.getLevel(), itemStack, 6, direction, position);
        return stack;
    }

    protected void spawnParticles(BlockSource pointer, Direction side) {
        pointer.getLevel().levelEvent(2000, pointer.getPos(), side.get3DDataValue());
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    protected void playSound(BlockSource pointer) {
        pointer.getLevel().levelEvent(this.isSuccess() ? 1000 : 1001, pointer.getPos(), 0);
    }
}