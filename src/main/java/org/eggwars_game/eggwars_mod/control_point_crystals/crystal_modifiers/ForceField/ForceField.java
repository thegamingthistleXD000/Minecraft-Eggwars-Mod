package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.ForceField;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;

import net.minecraft.entity.Entity;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;

import java.util.List;
import java.util.Objects;

public class ForceField extends Modifier
{

    @Override
    public void tick() {
        for (EggwarsEntity e : getForceEntitiesInRange()){
            if (mode == Mode.Pull || mode == Mode.Repel){
                addForce(e);
            }
            if (mode == Mode.Mining_Fatigue || mode == Mode.Ally_Buffs){
                addEffect(e);
            }
        }
    }

    @Override
    public void register(EggwarsMatch match, BlockPos pos) {
        match.modManager.addModifier(() -> this, pos);
        match.fieldManager.addModifier(() -> this, pos);
    }

    public enum Mode {Repel, Pull, Mining_Fatigue, Ally_Buffs}
    private Mode mode;
    Direction facing;

    public ForceField(BlockPos position, EggwarsPlayer player, Direction facing)
    {
        super(position, modifiers.mods.ForceField, player);
        mode = Mode.Repel;
        this.facing = facing;
    }

    private float Force_Strength = 5;
    private float Effect_Strength;

    private final float softening = 2f;
    private final float factor = 1;

    private float range = 20;
    private float radius = 10;

    void addForce(EggwarsEntity eggtarget) {
        Entity target = eggtarget.getEntity();
        System.out.println("addForce called for " + target.getName().getString());

        System.out.println("Field team: " + getTeam());
        System.out.println("Target team: " + target.getScoreboardTeam());

        if (eggtarget.getTeam() == getTeam()) return;

        Vec3d direction = Vec3d.of(facing.getVector()).normalize();
        Vec3d origin = Vec3d.ofCenter(position);

        double distance = target.getPos().distanceTo(origin);
        double force = Force_Strength * factor
                / (distance * distance + softening);

        if (mode == Mode.Repel) {
            target.addVelocity(direction.multiply(force));
        } else if (mode == Mode.Pull) {
            target.addVelocity(direction.multiply(-force));
        }
    }

    void addEffect(EggwarsEntity eggtarget) {

        Entity target = eggtarget.getEntity();

        if (!(target instanceof LivingEntity living)) {
            return;
        }

        Vec3d center = Vec3d.ofCenter(position);

        if (target.squaredDistanceTo(center) > range * range) {
            return;
        }

        Vec3d targetPos = target.getPos();

        Vec3d toCenter = center.subtract(targetPos);

        double distance = toCenter.length();

        Effect_Strength *= (float) (distance/range);

        if (mode == Mode.Mining_Fatigue) {

            if (eggtarget.getTeam() == getTeam()) return;

            living.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.MINING_FATIGUE,
                            100,
                            (int) Effect_Strength - 1,
                            false,
                            false,
                            true
                    )
            );
        }

        else if (mode == Mode.Ally_Buffs) {

            if (eggtarget.getTeam() != getTeam()) return;

            living.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.SPEED,
                            100,
                            (int) Effect_Strength - 1,
                            false,
                            false,
                            true
                    )
            );

            living.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.STRENGTH,
                            100,
                            (int) Effect_Strength - 1,
                            false,
                            false,
                            true
                    )
            );
        }
    }

    public ForceField.Mode getMode() {return mode;}
    public void setMode(ForceField.Mode m) {mode = m;}

    public List<EggwarsEntity> getForceEntitiesInRange() {

        Vec3d direction = Vec3d.of(facing.getVector()).normalize();
        Vec3d origin = Vec3d.ofCenter(position);

        double r = radius;
        double length = range;

        Box box = new Box(
                Math.min(origin.x, origin.x + direction.x * length) - r,
                Math.min(origin.y, origin.y + direction.y * length) - r,
                Math.min(origin.z, origin.z + direction.z * length) - r,
                Math.max(origin.x, origin.x + direction.x * length) + r,
                Math.max(origin.y, origin.y + direction.y * length) + r,
                Math.max(origin.z, origin.z + direction.z * length) + r
        );
        return world.getEntitiesByClass(
                        Entity.class,
                        box,
                        entity -> {
                            Vec3d displacement = entity.getPos().subtract(origin);
                            double along = displacement.dotProduct(direction);

                            if (along < 0 || along > length) return false;

                            double radialSquared =
                                    displacement.lengthSquared() - along * along;

                            return radialSquared <= r * r;
                        }
                ).stream()
                .map(match::getEggwarsEntity)
                .filter(Objects::nonNull)
                .toList();
    }



}
