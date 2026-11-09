package org.eggwars_game.eggwars.client.Renderers;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars.client.ClientCrystalRay;
import org.eggwars_game.eggwars.client.EggwarsClient;
import org.joml.Vector3f;


public class CrystalRayRenderer {

    private static final double RADIUS = 0.22;
    private static final int SEGMENTS = 240;
    private static final double TURNS = 12.0;

    private CrystalRayRenderer() {}

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client.world == null || client.player == null) return;

            if (EggwarsClient.getMatch() == null) return;

            MatrixStack matrices = context.matrixStack();
            if (matrices == null || context.consumers() == null) return;

            Vec3d cameraPos = context.camera().getPos();

            matrices.push();
            matrices.translate(
                    -cameraPos.x,
                    -cameraPos.y,
                    -cameraPos.z
            );

            VertexConsumer vertices =
                    context.consumers().getBuffer(RenderLayer.getLines());

            long worldTime = client.world.getTime();

            for (ClientCrystalRay ray :
                    EggwarsClient.getMatch().getCrystalRays()) {

                renderSpiral(
                        vertices,
                        matrices,
                        ray,
                        worldTime
                );
            }

            matrices.pop();
        });
    }

    private static void renderSpiral(
            VertexConsumer vertices,
            MatrixStack matrices,
            ClientCrystalRay ray,
            long worldTime
    ) {
        Vec3d start = ray.from().toCenterPos();
        Vec3d end = ray.to().toCenterPos();

        Vec3d direction = end.subtract(start);

        double length = direction.length();

        if (length < 0.001) return;

        direction = direction.normalize();

        Vec3d reference = Math.abs(direction.y) < 0.9
                ? new Vec3d(0, 1, 0)
                : new Vec3d(1, 0, 0);

        Vec3d u = direction.crossProduct(reference).normalize();
        Vec3d v = direction.crossProduct(u).normalize();

        double phase = worldTime * 0.12;

        MatrixStack.Entry entry = matrices.peek();

        Vector3f normal = new Vector3f(0, 1, 0);

        int color = ray.color();

        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;

        Vec3d previous = null;

        for (int i = 0; i <= SEGMENTS; i++) {

            double t = (double) i / SEGMENTS;

            double angle = t * Math.PI * 2 * TURNS + phase;

            Vec3d center = start.add(direction.multiply(length * t));

            Vec3d offset = u.multiply(Math.cos(angle) * RADIUS)
                    .add(v.multiply(Math.sin(angle) * RADIUS));

            Vec3d point = center.add(offset);

            if (previous != null) {

                vertices.vertex(
                        entry.getPositionMatrix(),
                        (float) previous.x,
                        (float) previous.y,
                        (float) previous.z
                ).color(red, green, blue, 255)
                 .normal(normal.x, normal.y, normal.z);

                vertices.vertex(
                        entry.getPositionMatrix(),
                        (float) point.x,
                        (float) point.y,
                        (float) point.z
                ).color(red, green, blue, 255)
                 .normal(normal.x, normal.y, normal.z);
            }

            previous = point;
        }
    }
}