package net.onixary.shapeShifterCurseFabric.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AltarBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


// 渲染先用透明方案吧 BlockEntity类方块由BlockEntity动态渲染
public class AltarBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected AltarBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarBlockEntity(pos, state);
    }


    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.openScreen(world, pos, player);
            return InteractionResult.CONSUME;
        }
    }

    protected void openScreen(Level world, BlockPos pos, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AltarBlockEntity altarBlockEntity) {
            altarBlockEntity.lastUser = player.getUUID();
            player.openMenu(altarBlockEntity);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // 1.21 BaseEntityBlock 要求实现抽象的 codec()（用于 Block 的注册/网络同步）
    @Override
    protected @NotNull MapCodec<AltarBlock> codec() {
        return Block.simpleCodec(AltarBlock::new);
    }

    // 1.21.1 mojmap 里对应的工具是 BaseEntityBlock.createTickerHelper（原先那个本地 checkType 是 1.20.1 yarn 的
    // BlockWithEntity.checkType 遗骸，两个重载会互相打转，编译不过）
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide ? null : createTickerHelper(type, RegCustomBlock.ALTAR_BLOCK_ENTITY, (world1, pos, blockState, blockEntity) -> blockEntity.tick(world1, pos, blockState, blockEntity));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AltarBlockEntity altarBlockEntity) {
                if (world instanceof ServerLevel) {
                    Containers.dropContents(world, pos, altarBlockEntity);
                }
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }
}
