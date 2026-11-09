package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CobwebArrowEntity extends ArrowEntity {

    public CobwebArrowEntity(
            EntityType<? extends CobwebArrowEntity> type,
            World world
    ) {
        super(type, world);
    }

    public CobwebArrowEntity(World world, LivingEntity owner) {
        super(world, owner, ItemStack.EMPTY, null);
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);

        if (getWorld().isClient()) {
            return;
        }

        BlockPos pos = hit.getBlockPos().offset(hit.getSide());

        if (getWorld().getBlockState(pos).isAir()) {
            getWorld().setBlockState(
                    pos,
                    Blocks.COBWEB.getDefaultState()
            );
        }
    }
}