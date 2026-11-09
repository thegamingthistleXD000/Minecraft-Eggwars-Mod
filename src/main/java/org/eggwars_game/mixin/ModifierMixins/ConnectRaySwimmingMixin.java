
package org.eggwars_game.mixin.ModifierMixins;

import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ConnectRaySwimmingMixin {

    @Inject(
        method = "travel",
        at = @At("HEAD"),
        cancellable = true
    )
    private void eggwars$virtualMovement(
            Vec3d movementInput,
            CallbackInfo ci
    ) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        EggwarsPlayer eggwarsPlayer = Eggwars.getEggwarsPlayer(player);
        if (eggwarsPlayer == null) {
            return;
        }

        if (!eggwarsPlayer.isFastTravelling()) {
            return;
        }

        ci.cancel();

        // Forward direction follows the complete look direction,
        // including pitch.
        Vec3d forward = player.getRotationVector();

        // Strafe direction follows horizontal yaw.
        double yaw = Math.toRadians(player.getYaw());

        Vec3d right = new Vec3d(
            Math.cos(yaw),
            0.0,
            Math.sin(yaw)
        );

        // Combine WASD and vertical input.
        Vec3d direction = forward.multiply(movementInput.z)
            .add(right.multiply(movementInput.x))
            .add(0.0, movementInput.y, 0.0);

        // Prevent diagonal movement from being faster.
        if (direction.lengthSquared() > 1.0) {
            direction = direction.normalize();
        }

        // Movement parameters.
        double acceleration = player.isSprinting() ? 0.03 : 0.02;
        double drag = 0.80;

        // Apply acceleration and damping.
        Vec3d velocity = player.getVelocity()
            .multiply(drag)
            .add(direction.multiply(acceleration));

        player.setVelocity(velocity);

        // Perform movement without vanilla fluid or gravity physics.
        player.move(MovementType.SELF, velocity);
    }
}
