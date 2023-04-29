package com.plusls.carpet.network;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.plusls.carpet.PluslsCarpetAdditionExtension;
import com.plusls.carpet.PluslsCarpetAdditionReference;
import com.plusls.carpet.PluslsCarpetAdditionSettings;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

//#if MC <= 11502
//$$ import net.minecraft.world.level.dimension.DimensionType;
//#endif

//#if MC > 11802
@SuppressWarnings("removal")
//#endif
public class PcaSyncProtocol {
    public static final ReentrantLock lock = new ReentrantLock(true);
    public static final ReentrantLock pairLock = new ReentrantLock(true);
    // 发送包
    private static final ResourceLocation ENABLE_PCA_SYNC_PROTOCOL = PluslsCarpetAdditionReference.identifier("enable_pca_sync_protocol");
    private static final ResourceLocation DISABLE_PCA_SYNC_PROTOCOL = PluslsCarpetAdditionReference.identifier("disable_pca_sync_protocol");
    private static final ResourceLocation UPDATE_ENTITY = PluslsCarpetAdditionReference.identifier("update_entity");
    private static final ResourceLocation UPDATE_BLOCK_ENTITY = PluslsCarpetAdditionReference.identifier("update_block_entity");
    // 响应包
    private static final ResourceLocation SYNC_BLOCK_ENTITY = PluslsCarpetAdditionReference.identifier("sync_block_entity");
    private static final ResourceLocation SYNC_ENTITY = PluslsCarpetAdditionReference.identifier("sync_entity");
    private static final ResourceLocation CANCEL_SYNC_BLOCK_ENTITY = PluslsCarpetAdditionReference.identifier("cancel_sync_block_entity");
    private static final ResourceLocation CANCEL_SYNC_ENTITY = PluslsCarpetAdditionReference.identifier("cancel_sync_entity");
    private static final Map<ServerPlayer, Pair<ResourceLocation, BlockPos>> playerWatchBlockPos = new HashMap<>();
    private static final Map<ServerPlayer, Pair<ResourceLocation, Entity>> playerWatchEntity = new HashMap<>();
    private static final Map<Pair<ResourceLocation, BlockPos>, Set<ServerPlayer>> blockPosWatchPlayerSet = new HashMap<>();
    private static final Map<Pair<ResourceLocation, Entity>, Set<ServerPlayer>> entityWatchPlayerSet = new HashMap<>();
    private static final MutablePair<ResourceLocation, Entity> identifierEntityPair = new MutablePair<>();
    private static final MutablePair<ResourceLocation, BlockPos> identifierBlockPosPair = new MutablePair<>();

    // 通知客户端服务器已启用 PcaSyncProtocol
    public static void enablePcaSyncProtocol(@NotNull ServerPlayer player) {
        // 在这写如果是在 BC 端的情况下，ServerPlayNetworking.canSend 在这个时机调用会出现错误
        PluslsCarpetAdditionReference.getLogger().debug("Try enablePcaSyncProtocol: {}", player.getName().getString());
        // bc 端比较奇怪，canSend 工作不正常
        // if (ServerPlayNetworking.canSend(player, ENABLE_PCA_SYNC_PROTOCOL)) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, ENABLE_PCA_SYNC_PROTOCOL, buf);
        PluslsCarpetAdditionReference.getLogger().debug("send enablePcaSyncProtocol to {}!", player.getName().getString());
        lock.lock();
        lock.unlock();
    }

    // 通知客户端服务器已停用 PcaSyncProtocol
    public static void disablePcaSyncProtocol(@NotNull ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, DISABLE_PCA_SYNC_PROTOCOL, buf);
        PluslsCarpetAdditionReference.getLogger().debug("send disablePcaSyncProtocol to {}!", player.getName().getString());
    }

    // 通知客户端更新 Entity
    // 包内包含 World 的 Identifier, entityId, entity 的 nbt 数据
    // 传输 World 是为了通知客户端该 Entity 属于哪个 World
    public static void updateEntity(@NotNull ServerPlayer player, @NotNull Entity entity) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        //#if MC > 11502
        buf.writeResourceLocation(entity.getCommandSenderWorld().dimension().location());
        //#else
        //$$ buf.writeResourceLocation(DimensionType.getName(entity.getCommandSenderWorld().dimension.getType()));
        //#endif
        buf.writeInt(entity.getId());
        buf.writeNbt(entity.saveWithoutId(new CompoundTag()));
        ServerPlayNetworking.send(player, UPDATE_ENTITY, buf);
    }

    // 通知客户端更新 BlockEntity
    // 包内包含 World 的 Identifier, pos, blockEntity 的 nbt 数据
    // 传输 World 是为了通知客户端该 BlockEntity 属于哪个世界
    public static void updateBlockEntity(@NotNull ServerPlayer player, @NotNull BlockEntity blockEntity) {
        Level world = blockEntity.getLevel();

        // 在生成世界时可能会产生空指针
        if (world == null) {
            return;
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        //#if MC > 11502
        buf.writeResourceLocation(world.dimension().location());
        //#else
        //$$ buf.writeResourceLocation(DimensionType.getName(world.dimension.getType()));
        //#endif
        buf.writeBlockPos(blockEntity.getBlockPos());
        //#if MC > 11701
        buf.writeNbt(blockEntity.saveWithoutMetadata());
        //#else
        //$$ buf.writeNbt(new CompoundTag());
        //#endif
        ServerPlayNetworking.send(player, UPDATE_BLOCK_ENTITY, buf);
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_BLOCK_ENTITY, PcaSyncProtocol::syncBlockEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_ENTITY, PcaSyncProtocol::syncEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(CANCEL_SYNC_BLOCK_ENTITY, PcaSyncProtocol::cancelSyncBlockEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(CANCEL_SYNC_ENTITY, PcaSyncProtocol::cancelSyncEntityHandler);
        ServerPlayConnectionEvents.JOIN.register(PcaSyncProtocol::onJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(PcaSyncProtocol::onDisconnect);
    }

    private static void onDisconnect(ServerGamePacketListenerImpl serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol) {
            PluslsCarpetAdditionReference.getLogger().debug("onDisconnect remove: {}", serverPlayNetworkHandler.player.getName().getString());
        }
    }

    private static void onJoin(ServerGamePacketListenerImpl serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        if (PluslsCarpetAdditionSettings.pcaSyncProtocol) {
            enablePcaSyncProtocol(serverPlayNetworkHandler.player);
        }
    }

    // 客户端通知服务端取消 BlockEntity 同步
    private static void cancelSyncBlockEntityHandler(MinecraftServer server, ServerPlayer player,
                                                     ServerGamePacketListenerImpl handler, FriendlyByteBuf buf,
                                                     PacketSender responseSender) {
        if (!PluslsCarpetAdditionSettings.pcaSyncProtocol) {
            return;
        }
        PluslsCarpetAdditionReference.getLogger().debug("{} cancel watch blockEntity.", player.getName().getString());
        PcaSyncProtocol.clearPlayerWatchBlock(player);
    }

    // 客户端通知服务端取消 Entity 同步
    private static void cancelSyncEntityHandler(MinecraftServer server, ServerPlayer player,
                                                ServerGamePacketListenerImpl handler, FriendlyByteBuf buf,
                                                PacketSender responseSender) {
        if (!PluslsCarpetAdditionSettings.pcaSyncProtocol) {
            return;
        }
        PluslsCarpetAdditionReference.getLogger().debug("{} cancel watch entity.", player.getName().getString());
        PcaSyncProtocol.clearPlayerWatchEntity(player);
    }

    // 客户端请求同步 BlockEntity
    // 包内包含 pos
    // 由于正常的场景一般不会跨世界请求数据，因此包内并不包含 World，以玩家所在的 World 为准
    private static void syncBlockEntityHandler(MinecraftServer server, ServerPlayer player,
                                               ServerGamePacketListenerImpl handler, FriendlyByteBuf buf,
                                               PacketSender responseSender) {
        if (!PluslsCarpetAdditionSettings.pcaSyncProtocol) {
            return;
        }
        BlockPos pos = buf.readBlockPos();
        ServerLevel world = (ServerLevel) player.getLevelCompat();
        BlockState blockState = world.getBlockState(pos);
        clearPlayerWatchData(player);
        PluslsCarpetAdditionReference.getLogger().debug("{} watch blockpos {}: {}", player.getName().getString(), pos, blockState);

        BlockEntity blockEntityAdj = null;
        // 不是单个箱子则需要更新隔壁箱子
        if (blockState.getBlock() instanceof ChestBlock) {
            if (blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos posAdj = pos.relative(ChestBlock.getConnectedDirection(blockState));
                // The method in World now checks that the caller is from the same thread...
                blockEntityAdj = world.getChunkAt(posAdj).getBlockEntity(posAdj);
            }
        } else if (PluslsCarpetAdditionReference.tisCarpetLoaded && blockState.is(Blocks.BARREL) && CarpetServer.settingsManager.getRule("largeBarrel").getBoolValue()) {
            Direction directionOpposite = blockState.getValue(BarrelBlock.FACING).getOpposite();
            BlockPos posAdj = pos.relative(directionOpposite);
            BlockState blockStateAdj = world.getBlockState(posAdj);
            if (blockStateAdj.is(Blocks.BARREL) && blockStateAdj.getValue(BarrelBlock.FACING) == directionOpposite) {
                blockEntityAdj = world.getChunkAt(posAdj).getBlockEntity(posAdj);
            }
        }

        if (blockEntityAdj != null) {
            updateBlockEntity(player, blockEntityAdj);
        }

        // 本来想判断一下 blockState 类型做个白名单的，考虑到 client 已经做了判断就不在服务端做判断了
        // 就算被恶意攻击应该不会造成什么损失
        // 大不了 op 直接拉黑
        // The method in World now checks that the caller is from the same thread...
        BlockEntity blockEntity = world.getChunkAt(pos).getBlockEntity(pos);
        if (blockEntity != null) {
            updateBlockEntity(player, blockEntity);
        }

        //#if MC > 11502
        Pair<ResourceLocation, BlockPos> pair = new ImmutablePair<>(player.getCommandSenderWorld().dimension().location(), pos);
        //#else
        //$$ Pair<ResourceLocation, BlockPos> pair = new ImmutablePair<>(DimensionType.getName(player.getCommandSenderWorld().dimension.getType()), pos);
        //#endif
        lock.lock();
        playerWatchBlockPos.put(player, pair);
        if (!blockPosWatchPlayerSet.containsKey(pair)) {
            blockPosWatchPlayerSet.put(pair, new HashSet<>());
        }
        blockPosWatchPlayerSet.get(pair).add(player);
        lock.unlock();
    }

    // 客户端请求同步 Entity
    // 包内包含 entityId
    // 由于正常的场景一般不会跨世界请求数据，因此包内并不包含 World，以玩家所在的 World 为准
    private static void syncEntityHandler(MinecraftServer server, ServerPlayer player,
                                          ServerGamePacketListenerImpl handler, FriendlyByteBuf buf,
                                          PacketSender responseSender) {
        if (!PluslsCarpetAdditionSettings.pcaSyncProtocol) {
            return;
        }
        int entityId = buf.readInt();
        ServerLevel world = (ServerLevel) player.getLevelCompat();
        Entity entity = world.getEntity(entityId);
        if (entity == null) {
            PluslsCarpetAdditionReference.getLogger().debug("Can't find entity {}.", entityId);
        } else {
            clearPlayerWatchData(player);
            if (entity instanceof Player) {
                if (PluslsCarpetAdditionSettings.pcaSyncPlayerEntity == PluslsCarpetAdditionSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.NOBODY) {
                    return;
                } else if (PluslsCarpetAdditionSettings.pcaSyncPlayerEntity == PluslsCarpetAdditionSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.BOT) {
                    if (!(entity instanceof EntityPlayerMPFake)) {
                        return;
                    }
                } else if (PluslsCarpetAdditionSettings.pcaSyncPlayerEntity == PluslsCarpetAdditionSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS) {
                    if (!(entity instanceof EntityPlayerMPFake) && server.getProfilePermissions(player.getGameProfile()) < 2) {
                        return;
                    }
                } else if (PluslsCarpetAdditionSettings.pcaSyncPlayerEntity == PluslsCarpetAdditionSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS_AND_SELF) {
                    if (!(entity instanceof EntityPlayerMPFake) &&
                            server.getProfilePermissions(player.getGameProfile()) < 2 &&
                            entity != player) {
                        return;
                    }
                } else if (PluslsCarpetAdditionSettings.pcaSyncPlayerEntity != PluslsCarpetAdditionSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.EVERYONE) {
                    // wtf????
                    PluslsCarpetAdditionReference.getLogger().warn("syncEntityHandler wtf???");
                    return;
                }
            }
            PluslsCarpetAdditionReference.getLogger().debug("{} watch entity {}: {}", player.getName().getString(), entityId, entity);
            updateEntity(player, entity);

            //#if MC > 11502
            Pair<ResourceLocation, Entity> pair = new ImmutablePair<>(entity.getCommandSenderWorld().dimension().location(), entity);
            //#else
            //$$ Pair<ResourceLocation, Entity> pair = new ImmutablePair<>(DimensionType.getName(entity.getCommandSenderWorld().dimension.getType()), entity);
            //#endif
            lock.lock();
            playerWatchEntity.put(player, pair);
            if (!entityWatchPlayerSet.containsKey(pair)) {
                entityWatchPlayerSet.put(pair, new HashSet<>());
            }
            entityWatchPlayerSet.get(pair).add(player);
            lock.unlock();
        }
    }

    private static MutablePair<ResourceLocation, Entity> getIdentifierEntityPair(ResourceLocation identifier, Entity entity) {
        pairLock.lock();
        identifierEntityPair.setLeft(identifier);
        identifierEntityPair.setRight(entity);
        pairLock.unlock();
        return identifierEntityPair;
    }

    private static MutablePair<ResourceLocation, BlockPos> getIdentifierBlockPosPair(ResourceLocation identifier, BlockPos pos) {
        pairLock.lock();
        identifierBlockPosPair.setLeft(identifier);
        identifierBlockPosPair.setRight(pos);
        pairLock.unlock();
        return identifierBlockPosPair;
    }

    // 工具
    private static @Nullable Set<ServerPlayer> getWatchPlayerList(@NotNull Entity entity) {
        //#if MC > 11502
        return entityWatchPlayerSet.get(getIdentifierEntityPair(entity.getCommandSenderWorld().dimension().location(), entity));
        //#else
        //$$ return entityWatchPlayerSet.get(getIdentifierEntityPair(DimensionType.getName(entity.getCommandSenderWorld().dimension.getType()), entity));
        //#endif
    }

    private static @Nullable Set<ServerPlayer> getWatchPlayerList(@NotNull Level world, @NotNull BlockPos blockPos) {
        //#if MC > 11502
        return blockPosWatchPlayerSet.get(getIdentifierBlockPosPair(world.dimension().location(), blockPos));
        //#else
        //$$ return blockPosWatchPlayerSet.get(getIdentifierBlockPosPair(DimensionType.getName(world.dimension.getType()), blockPos));
        //#endif
    }

    public static boolean syncEntityToClient(@NotNull Entity entity) {
        if (entity.getCommandSenderWorld().isClientSide()) {
            return false;
        }
        lock.lock();
        Set<ServerPlayer> playerList = getWatchPlayerList(entity);
        boolean ret = false;
        if (playerList != null) {
            for (ServerPlayer player : playerList) {
                updateEntity(player, entity);
                ret = true;
            }
        }
        lock.unlock();
        return ret;
    }

    public static boolean syncBlockEntityToClient(@NotNull BlockEntity blockEntity) {
        boolean ret = false;
        Level world = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        // 在生成世界时可能会产生空指针
        if (world != null) {
            if (world.isClientSide()) {
                return false;
            }
            BlockState blockState = world.getBlockState(pos);
            lock.lock();
            Set<ServerPlayer> playerList = getWatchPlayerList(world, blockEntity.getBlockPos());

            Set<ServerPlayer> playerListAdj = null;

            if (blockState.getBlock() instanceof ChestBlock) {
                if (blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                    // 如果是一个大箱子需要特殊处理
                    // 上面不用 isOf 是为了考虑到陷阱箱的情况，陷阱箱继承自箱子
                    BlockPos posAdj = pos.relative(ChestBlock.getConnectedDirection(blockState));
                    playerListAdj = getWatchPlayerList(world, posAdj);
                }
            } else if (PluslsCarpetAdditionReference.tisCarpetLoaded && blockState.is(Blocks.BARREL) && CarpetServer.settingsManager.getRule("largeBarrel").getBoolValue()) {
                Direction directionOpposite = blockState.getValue(BarrelBlock.FACING).getOpposite();
                BlockPos posAdj = pos.relative(directionOpposite);
                BlockState blockStateAdj = world.getBlockState(posAdj);
                if (blockStateAdj.is(Blocks.BARREL) && blockStateAdj.getValue(BarrelBlock.FACING) == directionOpposite) {
                    playerListAdj = getWatchPlayerList(world, posAdj);
                }
            }
            if (playerListAdj != null) {
                if (playerList == null) {
                    playerList = playerListAdj;
                } else {
                    playerList.addAll(playerListAdj);
                }
            }

            if (playerList != null) {
                for (ServerPlayer player : playerList) {
                    updateBlockEntity(player, blockEntity);
                    ret = true;
                }
            }
            lock.unlock();
        }
        return ret;
    }

    private static void clearPlayerWatchEntity(ServerPlayer player) {
        lock.lock();
        Pair<ResourceLocation, Entity> pair = playerWatchEntity.get(player);
        if (pair != null) {
            Set<ServerPlayer> playerSet = entityWatchPlayerSet.get(pair);
            playerSet.remove(player);
            if (playerSet.isEmpty()) {
                entityWatchPlayerSet.remove(pair);
            }
            playerWatchEntity.remove(player);
        }
        lock.unlock();
    }

    private static void clearPlayerWatchBlock(ServerPlayer player) {
        lock.lock();
        Pair<ResourceLocation, BlockPos> pair = playerWatchBlockPos.get(player);
        if (pair != null) {
            Set<ServerPlayer> playerSet = blockPosWatchPlayerSet.get(pair);
            playerSet.remove(player);
            if (playerSet.isEmpty()) {
                blockPosWatchPlayerSet.remove(pair);
            }
            playerWatchBlockPos.remove(player);
        }
        lock.unlock();
    }

    // 停用 PcaSyncProtocol
    public static void disablePcaSyncProtocolGlobal() {
        lock.lock();
        playerWatchBlockPos.clear();
        playerWatchEntity.clear();
        blockPosWatchPlayerSet.clear();
        entityWatchPlayerSet.clear();
        lock.unlock();
        if (PluslsCarpetAdditionExtension.getServer() != null) {
            for (ServerPlayer player : PluslsCarpetAdditionExtension.getServer().getPlayerList().getPlayers()) {
                disablePcaSyncProtocol(player);
            }
        }
    }

    // 启用 PcaSyncProtocol
    public static void enablePcaSyncProtocolGlobal() {
        if (PluslsCarpetAdditionExtension.getServer() == null) {
            return;
        }
        for (ServerPlayer player : PluslsCarpetAdditionExtension.getServer().getPlayerList().getPlayers()) {
            enablePcaSyncProtocol(player);
        }
    }


    // 删除玩家数据
    public static void clearPlayerWatchData(ServerPlayer player) {
        PcaSyncProtocol.clearPlayerWatchBlock(player);
        PcaSyncProtocol.clearPlayerWatchEntity(player);
    }
}
