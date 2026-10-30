package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class ForceFieldBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;

    public ForceFieldBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(
            StateManager.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(
                FACING,
                context.getSide()
        );
    }
}