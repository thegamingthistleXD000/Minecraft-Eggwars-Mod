
package org.eggwars_game.mixin.ModifierMixins;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.PearlBarrier.PearlBarrier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectilePearlBarrierBlockMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void eggwars$checkPearlBarrier(CallbackInfo ci) {
        ProjectileEntity projectile = (ProjectileEntity) (Object) this;

        if (!(projectile.getWorld() instanceof ServerWorld))
            return;

        if (!(projectile instanceof EnderPearlEntity)
                && !(projectile instanceof PersistentProjectileEntity))
            return;

       EggwarsMatch match = Eggwars.getMatch(projectile);
        if (match == null)
            return;



        Vec3d start = projectile.getPos();
        Vec3d velocity = projectile.getVelocity();

        if (velocity.lengthSquared() < 1.0E-10)
            return;

        Vec3d end = start.add(velocity);

        for (PearlBarrier barrier : match.barrierManager.getModifiers()) {
            Vec3d hitPos = barrier.blocksProjectile(projectile);

            if (hitPos == null)
                continue;

            Vec3d direction = projectile.getVelocity().normalize();

            projectile.setPosition(
                    hitPos.subtract(direction.multiply(0.05))
            );

            barrier.deflectProjectile(projectile);
            return;
        }
    }

}
