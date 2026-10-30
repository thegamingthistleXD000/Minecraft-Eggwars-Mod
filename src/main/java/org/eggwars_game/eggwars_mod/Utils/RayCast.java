package org.eggwars_game.eggwars_mod.Utils;

import com.jcraft.jorbis.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;










public class RayCast {
    
    private static final float ray_width = 2;

    public static Map<BlockPos, BlockState> getRayObstructions(
            ServerWorld world, BlockPos a, BlockPos b, int width) {

        Map<BlockPos, BlockState> obstructions = new HashMap<>();

        Vec3d start = a.toCenterPos();
        Vec3d end = b.toCenterPos();

        int x = a.getX();
        int y = a.getY();
        int z = a.getZ();

        int endX = b.getX();
        int endY = b.getY();
        int endZ = b.getZ();

        int dx = Integer.compare(endX, x);
        int dy = Integer.compare(endY, y);
        int dz = Integer.compare(endZ, z);

        while (true) {

            int radius = (width - 1) / 2;

            for (int ox = -radius; ox <= radius; ox++) {
                for (int oy = -radius; oy <= radius; oy++) {
                    for (int oz = -radius; oz <= radius; oz++) {

                        BlockPos pos = new BlockPos(
                                x + ox,
                                y + oy,
                                z + oz
                        );

                        BlockState state = world.getBlockState(pos);

                        if (!state.isAir())
                            obstructions.put(pos, state);
                    }
                }
            }

            if (x == endX && y == endY && z == endZ)
                break;

            Vec3d current = new Vec3d(x + 0.5, y + 0.5, z + 0.5);

            double tx = dx == 0 ? Double.MAX_VALUE :
                    Math.abs((end.x - current.x) / (end.x - start.x));

            double ty = dy == 0 ? Double.MAX_VALUE :
                    Math.abs((end.y - current.y) / (end.y - start.y));

            double tz = dz == 0 ? Double.MAX_VALUE :
                    Math.abs((end.z - current.z) / (end.z - start.z));

            if (tx < ty && tx < tz)
                x += dx;
            else if (ty < tz)
                y += dy;
            else
                z += dz;
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


