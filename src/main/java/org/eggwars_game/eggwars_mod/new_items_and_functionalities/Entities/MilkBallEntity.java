package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.Eggwars;

import java.util.ArrayList;

public class MilkBallEntity extends SnowballEntity {

    private static final int DURATION_REDUCTION = 100; // 5 seconds

    public MilkBallEntity(EntityType<? extends MilkBallEntity> type, World world) {
        super(type, world);
    }

    public MilkBallEntity(World world, LivingEntity owner) {
        super(world, owner);
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);

        if (getWorld().isClient()) return;

        if (!(hit.getEntity() instanceof LivingEntity target)) return;
        if (!(getOwner() instanceof LivingEntity owner)) return;

        boolean sameTeam = areTeammates(owner, target);

        for (StatusEffectInstance effect :
                new ArrayList<>(target.getStatusEffects())) {

            boolean beneficial = effect.getEffectType().value().isBeneficial();

            if ((sameTeam && !beneficial) || (!sameTeam && beneficial)) {
                int newDuration = effect.getDuration() - DURATION_REDUCTION;

                if (newDuration <= 0) {
                    target.removeStatusEffect(effect.getEffectType());
                } else {
                    target.addStatusEffect(new StatusEffectInstance(
                            effect.getEffectType(),
                            newDuration,
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.shouldShowParticles(),
                            effect.shouldShowIcon()
                    ));
                }
            }
        }
    }

    private boolean areTeammates(LivingEntity owner, LivingEntity target) {
        if (owner == target) return true;

        if (owner.getWorld().isClient()) return false;

        var ownerMatch = Eggwars.getMatch(owner);
        var targetMatch = Eggwars.getMatch(target);

        if (ownerMatch == null || ownerMatch != targetMatch) return false;

        var ownerWrapper = ownerMatch.getEggwarsEntity(owner);
        var targetWrapper = targetMatch.getEggwarsEntity(target);

        if (ownerWrapper == null || targetWrapper == null) return false;

        return ownerWrapper.getTeam() != null
                && ownerWrapper.getTeam() == targetWrapper.getTeam();
    }
}