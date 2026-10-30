package org.eggwars_game.eggwars_mod.dragon_ai;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles post-fight dragon egg hatching if enough neutral crystals heal it.
 *
 * Rule:
 * - If the dragon is dead, a dragon egg block is on the ground, and
 *   >5 neutral normal crystals (non emgen/diagen/spawn) exist,
 *   those crystals target the egg with healing beams.
 * - If this persists for 5 seconds (100 ticks), the egg hatches and
 *   a new dragon respawns at the egg position.
 */
public class dragon_respawning implements ModInitializer {

    private static final int REQUIRED_CRYSTALS = 6; // ">5" means 6 or more
    private static final int HATCH_TIME_TICKS = 100; // 5s

    private static class HatchState {
        BlockPos eggPos;
        int ticks;
    }

    private static final Map<ServerWorld, HatchState> STATES = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::tick);
        System.out.println("[EggwarsMod] Dragon respawn watcher loaded.");
    }

    private void tick(ServerWorld world) {
        // Only proceed if no dragon is alive
        if (world.getEntitiesByClass(EnderDragonEntity.class, fullWorldBox(), d -> true).size() > 0) {
            STATES.remove(world);
            return;
        }

        // Find a dragon egg block in the world (block form, on ground)
        BlockPos eggPos = findEggBlock(world);
        if (eggPos == null) {
            STATES.remove(world);
            return;
        }

        int connectedBeams = 0;
        for (EndCrystalEntity crystal : world.getEntitiesByClass(EndCrystalEntity.class, fullWorldBox(), c -> true)) {
            if (isNeutralNormalCrystal(crystal) && hasLineOfSight(world, crystal, eggPos)) {
                connectedBeams++;
                crystal.setBeamTarget(eggPos);
            }
        }

        if (connectedBeams < REQUIRED_CRYSTALS) {
            STATES.remove(world);
            return;
        }

        HatchState state = STATES.computeIfAbsent(world, w -> {
            HatchState s = new HatchState();
            s.eggPos = eggPos;
            s.ticks = 0;
            return s;
        });

        // If egg moved, reset
        if (!state.eggPos.equals(eggPos)) {
            state.eggPos = eggPos;
            state.ticks = 0;
        }

        state.ticks++;

        if (state.ticks >= HATCH_TIME_TICKS) {
            hatchDragon(world, eggPos);
            STATES.remove(world);
        }
    }

    private static BlockPos findEggBlock(ServerWorld world) {
        // Search a reasonable area: iterate loaded chunks via block entities is expensive,
        // but dragon egg is a block without block entity. We'll scan nearby crystals' area.
        // For simplicity and safety: iterate all crystals and check the block under/around them for eggs.
        for (EndCrystalEntity crystal : world.getEntitiesByClass(EndCrystalEntity.class, fullWorldBox(), c -> true)) {
            BlockPos pos = crystal.getBlockPos();
            for (BlockPos check : BlockPos.iterate(pos.add(-8, -2, -8), pos.add(8, 2, 8))) {
                if (world.getBlockState(check).isOf(Blocks.DRAGON_EGG)) {
                    return check.toImmutable();
                }
            }
        }
        return null;
    }

    private static boolean isNeutralNormalCrystal(EndCrystalEntity crystal) {
        NbtCompound tag = new NbtCompound();
        crystal.writeNbt(tag);

        // Must be a normal crystal (no emgen/diagen/spawn)
        if (tag.getBoolean("emerald_gen") || tag.getBoolean("diamond_gen") || tag.getBoolean("spawn_crystal")) {
            return false;
        }

        // Neutral: no team or team == "default" (or empty)
        String team = tag.getString("eggwarsCrystalTeam");
        boolean neutralTeam = team == null || team.isEmpty() || "default".equals(team);

        // Can include both control crystals (neutral) and non-control crystals
        return neutralTeam;
    }

    // Line-of-sight check that allows beams through bedrock, obsidian, end stone, and air.
    private static boolean hasLineOfSight(ServerWorld world, EndCrystalEntity crystal, BlockPos eggPos) {
        // beam starts at crystal top-ish
        double sx = crystal.getX();
        double sy = crystal.getY() + 1.0;
        double sz = crystal.getZ();

        double tx = eggPos.getX() + 0.5;
        double ty = eggPos.getY() + 0.5;
        double tz = eggPos.getZ() + 0.5;

        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;

        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 0.01) return true;

        double step = 0.5;
        int steps = (int)Math.ceil(len / step);

        double nx = dx / len * step;
        double ny = dy / len * step;
        double nz = dz / len * step;

        double cx = sx;
        double cy = sy;
        double cz = sz;

        for (int i = 0; i < steps; i++) {
            cx += nx;
            cy += ny;
            cz += nz;

            BlockPos pos = BlockPos.ofFloored(cx, cy, cz);
            var state = world.getBlockState(pos);

            if (state.isAir()) continue;

            var block = state.getBlock();
            if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.END_STONE) {
                continue; // allowed to pass through
            }

            // Any other non-air blocks block the beam
            return false;
        }

        return true;
    }

    private static void hatchDragon(ServerWorld world, BlockPos eggPos) {
        // Remove the egg block
        world.setBlockState(eggPos, Blocks.AIR.getDefaultState());

        EnderDragonEntity dragon = new EnderDragonEntity(EntityType.ENDER_DRAGON, world);
        dragon.refreshPositionAndAngles(eggPos.getX() + 0.5, eggPos.getY() + 1.0, eggPos.getZ() + 0.5, 0, 0);
        world.spawnEntity(dragon);

        world.getPlayers().forEach(p ->
                p.sendMessage(net.minecraft.text.Text.literal("§5The dragon egg hatched! The Ender Dragon returns."), false));
    }

    private static Box fullWorldBox() {
        return new Box(-30000000, worldMinY(), -30000000, 30000000, 30000000, 30000000);
    }

    private static double worldMinY() {
        // Vanilla world min can vary; use a safe low bound
        return -64;
    }
}
