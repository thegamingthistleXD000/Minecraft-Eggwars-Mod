package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeArrowEntity extends ArrowEntity {

    private static final int MAX_ABSORPTION = 64;

    public SpongeArrowEntity(
            EntityType<? extends SpongeArrowEntity> type,
            World world
    ) {
        super(type, world);
    }

    public SpongeArrowEntity(World world, LivingEntity owner) {
        super(world, owner, ItemStack.EMPTY, null);
    }



    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);

        if (getWorld().isClient()) {
            return;
        }

        absorbWater(hit.getBlockPos(), 3);
    }

    private void absorbWater(BlockPos origin, float range) {
        World world = getWorld();

        if (world.isClient()) return;

        int radius = (int) Math.ceil(range);

        BlockPos.iterate(
                origin.add(-radius, -radius, -radius),
                origin.add(radius, radius, radius)
        ).forEach(pos -> {
            if (pos.getSquaredDistance(origin) > range * range) return;

            BlockState state = world.getBlockState(pos);

            if (state.contains(Properties.WATERLOGGED)
                    && state.get(Properties.WATERLOGGED)) {

                world.setBlockState(
                        pos,
                        state.with(Properties.WATERLOGGED, false),
                        Block.NOTIFY_ALL
                );

            } else if (state.getFluidState().isOf(Fluids.WATER)) {

                world.setBlockState(
                        pos,
                        Blocks.AIR.getDefaultState(),
                        Block.NOTIFY_ALL
                );
            }
        });
    }
    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isClient()) {
            absorbWater(getBlockPos(), 1);
        }
    }
}