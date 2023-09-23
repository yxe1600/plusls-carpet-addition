package com.plusls.carpet.util.rule.dispenserFixIronGolem;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import com.plusls.carpet.util.dispenser.MyFallibleItemDispenserBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

//#if MC > 12001
//$$ import net.minecraft.core.dispenser.BlockSource;
//#else
import net.minecraft.core.BlockSource;
//#endif

public class IronIngotDispenserBehavior extends MyFallibleItemDispenserBehavior {
    public IronIngotDispenserBehavior(DispenseItemBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    public static void init() {
        DispenserBlock.registerBehavior(Items.IRON_INGOT,
                new IronIngotDispenserBehavior(DispenserBlock.DISPENSER_REGISTRY.get(Items.IRON_INGOT)));
    }

    @Override
    public ItemStack dispenseSilently(BlockSource pointer, ItemStack itemStack) {
        if (!PluslsCarpetAdditionSettings.dispenserFixIronGolem) {
            return itemStack;
        }
        BlockPos faceBlockPos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));

        List<IronGolem> ironGolemEntityList = pointer.getLevel().getEntitiesOfClass(IronGolem.class,
                new AABB(faceBlockPos), LivingEntity::isAlive);

        for (IronGolem ironGolemEntity : ironGolemEntityList) {
            float oldHealth = ironGolemEntity.getHealth();
            ironGolemEntity.heal(25.0F);
            if (ironGolemEntity.getHealth() == oldHealth) {
                continue;
            }
            float g = 1.0F + (ironGolemEntity.getRandom().nextFloat() - ironGolemEntity.getRandom().nextFloat()) * 0.2F;
            //#if MC > 11404
            ironGolemEntity.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, g);
            //#endif
            itemStack.shrink(1);
            setSuccess(true);
            return itemStack;
        }
        return itemStack;
    }
}