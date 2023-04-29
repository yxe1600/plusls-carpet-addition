package com.plusls.carpet.util.rule.gravestone;

import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

//#if MC > 11502
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
//#else
//$$ import net.minecraft.world.level.dimension.DimensionType;
//$$
//$$ import java.util.ArrayList;
//#endif

public class GravestoneUtil {
    public static final int NETHER_BEDROCK_MAX_Y = 127;
    public static final int SEARCH_RANGE = 5;
    public static final int PLAYER_INVENTORY_SIZE = 41;

    //#if MC > 11502
    public static void init() {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            deathHandle(player);
            return true;
        });
    }
    //#endif

    public static void deathHandle(@NotNull ServerPlayer player) {
        Level world = player.getLevelCompat();
        if (PluslsCarpetAdditionSettings.gravestone && !world.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack itemStack = player.getItemInHand(hand);
                if (itemStack.is(Items.TOTEM_OF_UNDYING)) {
                    return;
                }
            }
            player.destroyVanishingCursedItems();
            //#if MC > 11502
            SimpleContainer inventory = new SimpleContainer(PLAYER_INVENTORY_SIZE);
            for (ItemStack itemStack : player.getInventory().items) {
                inventory.addItem(itemStack);
            }

            for (ItemStack itemStack : player.getInventory().armor) {
                inventory.addItem(itemStack);
            }

            for (ItemStack itemStack : player.getInventory().offhand) {
                inventory.addItem(itemStack);
            }
            //#else
            //$$ ArrayList<ItemStack> inventory = new ArrayList<>();
            //$$ inventory.addAll(player.inventory.items);
            //$$ inventory.addAll(player.inventory.armor);
            //$$ inventory.addAll(player.inventory.offhand);
            //#endif
            int xp = player.totalExperience / 2;
            player.getInventory().clearContent();

            // only need clear experienceLevel
            player.experienceLevel = 0;
            BlockPos gravePos = findGravePos(player);
            Objects.requireNonNull(world.getServer()).tell(new TickTask(world.getServer().getTickCount(),
                    placeGraveRunnable(world,
                            gravePos,
                            new DeathInfo(System.currentTimeMillis(), xp, inventory),
                            player)));
        }
    }

    // find pos to place gravestone
    public static BlockPos findGravePos(@NotNull ServerPlayer player) {
        //#if MC > 11502
        BlockPos.MutableBlockPos playerPos = player.blockPositionCompat().mutable();
        //#else
        //$$ BlockPos.MutableBlockPos playerPos = new BlockPos.MutableBlockPos(player.blockPositionCompat());
        //#endif
        playerPos.setY(clampY(player, playerPos.getY()));
        if (canPlaceGrave(player, playerPos)) {
            return playerPos;
        }
        BlockPos.MutableBlockPos gravePos = new BlockPos.MutableBlockPos();
        for (int x = playerPos.getX() + SEARCH_RANGE; x >= playerPos.getX() - SEARCH_RANGE; x--) {
            gravePos.setX(x);
            int minY = clampY(player, playerPos.getY() - SEARCH_RANGE);
            for (int y = clampY(player, playerPos.getY() + SEARCH_RANGE); y >= minY; y--) {
                gravePos.setY(y);
                for (int z = playerPos.getZ() + SEARCH_RANGE; z >= playerPos.getZ() - SEARCH_RANGE; z--) {
                    gravePos.setZ(z);
                    if (canPlaceGrave(player, gravePos)) {
                        return drop(player, gravePos);
                    }
                }
            }
        }

        // search up
        gravePos.set(playerPos);
        while (player.getLevelCompat().getBlockState(gravePos).getBlock() == Blocks.BEDROCK) {
            gravePos.setY(gravePos.getY() + 1);
        }
        return gravePos;
    }

    // make sure to spawn graves on the suitable place
    public static int clampY(@NotNull ServerPlayer player, int y) {
        //don't spawn on nether ceiling, unless the player is already there.
        //#if MC > 11502
        if (player.getLevelCompat().dimension() == Level.NETHER && y < NETHER_BEDROCK_MAX_Y) {
        //#else
        //$$ if (player.dimension == DimensionType.NETHER && y < NETHER_BEDROCK_MAX_Y) {
        //#endif
            //clamp to 1 -- don't spawn graves the layer right above the void, so players can actually recover their items.
            return Mth.clamp(y, player.getLevelCompat().getMinBuildHeight() + 1, NETHER_BEDROCK_MAX_Y - 1);
        } else {
            return Mth.clamp(y, player.getLevelCompat().getMinBuildHeight() + 1, player.getLevelCompat().getMaxBuildHeight() - 1);
        }
    }


    public static boolean canPlaceGrave(@NotNull ServerPlayer player, BlockPos pos) {

        BlockState state = player.getLevelCompat().getBlockState(pos);
        if (pos.getY() <= player.getLevelCompat().getMinBuildHeight() + 1 || pos.getY() >= player.getLevelCompat().getMaxBuildHeight() - 1) {
            return false;
        } else if (state.isAir()) {
            return true;
        }
        // block can replace
        else return state.canBeReplaced(
                    new DirectionalPlaceContext(player.getLevelCompat(), pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
    }

    // players are blown up
    // reduce y pos
    public static BlockPos drop(@NotNull ServerPlayer player, BlockPos pos) {
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos().set(pos);
        int i = 0;
        for (int y = pos.getY() - 1; y > player.getLevelCompat().getMinBuildHeight() + 1 && i < 10; y--) {
            i++;
            searchPos.setY(clampY(player, y));
            if (!player.getLevelCompat().getBlockState(searchPos).isAir()) {
                searchPos.setY(clampY(player, y + 1));
                return searchPos;
            }
        }
        return pos;
    }

    @Contract(pure = true)
    public static @NotNull Runnable placeGraveRunnable(Level world, BlockPos pos, DeathInfo deathInfo, ServerPlayer player) {
        return () -> {
            BlockState graveBlock = Blocks.PLAYER_HEAD.defaultBlockState();

            // avoid setblockstate fail.
            while (!world.setBlockAndUpdate(pos, graveBlock)) {
                PluslsCarpetAdditionReference.getLogger().warn(String.format("set gravestone at %d %d %d fail, try again.",
                        pos.getX(), pos.getY(), pos.getZ()));
            }
            SkullBlockEntity graveEntity = (SkullBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos));
            graveEntity.setOwner(player.getGameProfile());
            ((MySkullBlockEntity) graveEntity).setDeathInfo(deathInfo);
            graveEntity.setChanged();
        };
    }
}