package org.eggwars_game.mixin.ModifierMixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks.ChorusWarpBlock;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks.ForceFieldBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class ModifierBlockBreakMixin {

    @Inject(
            method = "onStateReplaced",
            at = @At("HEAD")
    )
    private void eggwars$removeModifier(
            World world, BlockPos pos, BlockState state, boolean moved, CallbackInfo ci
    ) {
        if (world.isClient()) return;

        BlockState oldState = (BlockState) (Object) this;
        BlockState newState = world.getBlockState(pos);

        // Ignore state changes where the block itself hasn't changed.
        if (oldState.isOf(newState.getBlock())) return;


        EggwarsMatch match = Eggwars.getMatch(pos);
        if (match == null) return;

        Block block = oldState.getBlock();

        if (block instanceof ChorusWarpBlock) {
            System.out.println("warp broken");
            match.removeChorusWarp(pos);
        } else if (block instanceof ForceFieldBlock) {
            match.removeForceField(pos);
        }
    }
}