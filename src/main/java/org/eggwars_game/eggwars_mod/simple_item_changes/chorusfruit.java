
//if player looking downwards <-45deg while eating, chorus fruit teleports to lower level block, if looking upward >45deg, it teleports to higher level block, if in between, vanilla behaviour.
package org.eggwars_game.eggwars_mod.simple_item_changes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypedActionResult;

import net.minecraft.util.math.BlockPos;

public class chorusfruit implements ModInitializer {

    @Override
    public void onInitialize() {
        
        UseItemCallback.EVENT.register((player, world, hand) -> {

            ItemStack stack = player.getStackInHand(hand);
            if (!stack.isOf(Items.CHORUS_FRUIT)) return TypedActionResult.pass(stack);

            if (!(player instanceof ServerPlayerEntity serverPlayer)) return TypedActionResult.pass(stack);

            // We handle the teleport ourselves and consume the fruit, then stop vanilla from teleporting again
            applyChorusTeleport(serverPlayer, (ServerWorld) world);

            // Consume the fruit (basic decrement; vanilla hunger handled via normal eat cycle is skipped)
            if (!serverPlayer.isCreative()) {
                stack.decrement(1);
            }

            return TypedActionResult.success(stack, world.isClient());
        });                 
    }

    // ============================================================================
    // MAIN LOGIC: Custom Teleport Based on Look Angle
    // ============================================================================

    private void applyChorusTeleport(ServerPlayerEntity player, ServerWorld world) {

        float pitch = player.getPitch();  // Up/down angle

        if (pitch < -45f) {
            teleportDown(player, world);
            return;
        }

        if (pitch > 45f) {
            teleportUp(player, world);
            return;
        }

        // Normal vanilla teleport
        player.teleport(
                world,
                player.getX() + (world.random.nextInt(16) - 8),
                player.getY(),
                player.getZ() + (world.random.nextInt(16) - 8),
                player.getYaw(),
                player.getPitch()
        );
    }

    // ============================================================================
    // TELEPORT DOWNWARD — find nearest safe floor below
    // ============================================================================
    private void teleportDown(ServerPlayerEntity player, ServerWorld world) {

        BlockPos pos = player.getBlockPos();

        for (int y = pos.getY() - 1; y > world.getBottomY() + 5; y--) {

            BlockPos check = new BlockPos(pos.getX(), y, pos.getZ());

            if (isSafeLanding(world, check)) {
                performTeleport(player, world, check.up());
                return;
            }
        }

        // Fallback → vanilla random teleport
        randomTeleport(player, world);
    }

    // ============================================================================
    // TELEPORT UPWARD — find nearest safe platform above
    // ============================================================================
    private void teleportUp(ServerPlayerEntity player, ServerWorld world) {

        BlockPos pos = player.getBlockPos();

        for (int y = pos.getY() + 1; y < world.getTopY() - 5; y++) {

            BlockPos check = new BlockPos(pos.getX(), y, pos.getZ());

            if (isSafeLanding(world, check)) {
                performTeleport(player, world, check.up());
                return;
            }
        }

        // Fallback → vanilla random teleport
        randomTeleport(player, world);
    }

    // ============================================================================
    // SAFETY CHECK — solid ground + 2 air blocks above
    // ============================================================================
    private boolean isSafeLanding(ServerWorld world, BlockPos pos) {

        if (!world.getBlockState(pos).isSolidBlock(world, pos)) return false;

        BlockPos head = pos.up();
        BlockPos upper = pos.up(2);

        return world.isAir(head) && world.isAir(upper);
    }

    // ============================================================================
    // TELEPORT PLAYER
    // ============================================================================
    private void performTeleport(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {

        player.teleport(
            world,
            pos.getX() + 0.5,
            pos.getY(),
            pos.getZ() + 0.5,
            player.getYaw(),
            player.getPitch()
        );
    }

    // ============================================================================
    // FALLBACK — VANILLA RANDOM TELEPORT
    // ============================================================================
    private void randomTeleport(ServerPlayerEntity player, ServerWorld world) {

        double x = player.getX() + (world.random.nextInt(16) - 8);
        double z = player.getZ() + (world.random.nextInt(16) - 8);

        player.teleport(
            world,
            x,
            player.getY(),
            z,
            player.getYaw(),
            player.getPitch()
        );
    }
}
