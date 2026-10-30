package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.PearlBarrier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.jetbrains.annotations.NotNull;


public class PearlBarrier extends Modifier {

    @Override
    public void register(EggwarsMatch match, BlockPos pos) {
        match.modManager.addModifier(() -> this, pos);
        match.barrierManager.addModifier(() -> this, pos);
    }

    public PearlBarrier(ControlCrystal crystal, EggwarsPlayer player) {
        super(crystal.getPosition(), modifiers.mods.PearlBarrier, player);
        super.controlCrystal =  crystal;
    }



    public void deflectProjectile(ProjectileEntity projectile) {
        Entity owner = projectile.getOwner();

        Vec3d velocity = projectile.getVelocity();
        if (velocity.lengthSquared() == 0) return;

        projectile.setVelocity(velocity.negate());
        projectile.velocityModified = true;
    }

    public Vec3d blocksProjectile(ProjectileEntity projectile) {
        if (!(projectile instanceof EnderPearlEntity)
                && !(projectile instanceof PersistentProjectileEntity))
            return null;

        Entity owner = projectile.getOwner();

        if (owner instanceof LivingEntity living
                && getTeam() != null
                && getTeam().equals(living.getScoreboardTeam()))
            return null;

        Vec3d end = projectile.getPos();
        Vec3d start = end.subtract(projectile.getVelocity());

        BlockPos pos = controlCrystal.getPosition();
        Box box = new Box(pos).expand(5.0);

        Box[] walls = getWalls(box);

        Vec3d closestHit = null;
        double closestDistance = Double.MAX_VALUE;

        for (Box wall : walls) {
            var hit = wall.raycast(start, end);

            if (hit.isEmpty())
                continue;

            Vec3d hitPos = hit.get();
            double distance = start.squaredDistanceTo(hitPos);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestHit = hitPos;
            }
        }

        return closestHit;
    }

    private static Box @NotNull [] getWalls (Box box) {
        double t = 0.001;

        Box[] walls = {
                new Box(box.minX, box.minY, box.minZ,
                        box.maxX, box.maxY, box.minZ + t),
                new Box(box.minX, box.minY, box.maxZ - t,
                        box.maxX, box.maxY, box.maxZ),
                new Box(box.minX, box.minY, box.minZ,
                        box.minX + t, box.maxY, box.maxZ),
                new Box(box.maxX - t, box.minY, box.minZ,
                        box.maxX, box.maxY, box.maxZ),
                new Box(box.minX, box.minY, box.minZ,
                        box.maxX, box.minY + t, box.maxZ),
                new Box(box.minX, box.maxY - t, box.minZ,
                        box.maxX, box.maxY, box.maxZ)
        };
        return walls;
    }

    @Override
    public void tick() {

    }
}


