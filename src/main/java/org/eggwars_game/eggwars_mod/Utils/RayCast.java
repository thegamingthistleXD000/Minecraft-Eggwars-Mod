package org.eggwars_game.eggwars_mod.Utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;


public class RayCast {
    
    private static final float ray_width = 2;

    public static Map<BlockPos, BlockState> getRayObstructions(
            World world,
            BlockPos a,
            BlockPos b,
            int width
    ) {
        Map<BlockPos, BlockState> obstructions = new HashMap<>();

        Vec3d start = a.toCenterPos();
        Vec3d end = b.toCenterPos();
        Vec3d ray = end.subtract(start);

        int x = a.getX();
        int y = a.getY();
        int z = a.getZ();

        int endX = b.getX();
        int endY = b.getY();
        int endZ = b.getZ();

        int stepX = Integer.compare(endX, x);
        int stepY = Integer.compare(endY, y);
        int stepZ = Integer.compare(endZ, z);

        double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(ray.x);
        double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(ray.y);
        double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(ray.z);

        double tMaxX = stepX == 0 ? Double.POSITIVE_INFINITY :
                (stepX > 0 ? x + 1 - start.x : start.x - x) * tDeltaX;

        double tMaxY = stepY == 0 ? Double.POSITIVE_INFINITY :
                (stepY > 0 ? y + 1 - start.y : start.y - y) * tDeltaY;

        double tMaxZ = stepZ == 0 ? Double.POSITIVE_INFINITY :
                (stepZ > 0 ? z + 1 - start.z : start.z - z) * tDeltaZ;

        int radius = Math.max(0, (width - 1) / 2);

        while (true) {
            for (int ox = -radius; ox <= radius; ox++) {
                for (int oy = -radius; oy <= radius; oy++) {
                    for (int oz = -radius; oz <= radius; oz++) {

                        BlockPos pos = new BlockPos(x + ox, y + oy, z + oz);

                        if (pos.equals(a) || pos.equals(b)) continue;

                        BlockState state = world.getBlockState(pos);

                        if (!state.isAir()) {
                            obstructions.put(pos.toImmutable(), state);
                        }
                    }
                }
            }

            if (x == endX && y == endY && z == endZ) break;

            double next = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));

            if (tMaxX <= next + 1.0E-10) {
                x += stepX;
                tMaxX += tDeltaX;
            }

            if (tMaxY <= next + 1.0E-10) {
                y += stepY;
                tMaxY += tDeltaY;
            }

            if (tMaxZ <= next + 1.0E-10) {
                z += stepZ;
                tMaxZ += tDeltaZ;
            }
        }

        return obstructions;
    }

    private static boolean rayIntersectsBox(
            Vec3d start, Vec3d end, Box box, double halfWidth) {

        Box expanded = new Box(
                box.minX - halfWidth,
                box.minY - halfWidth,
                box.minZ - halfWidth,
                box.maxX + halfWidth,
                box.maxY + halfWidth,
                box.maxZ + halfWidth
        );

        return expanded.raycast(start, end).isPresent();
    }
}


