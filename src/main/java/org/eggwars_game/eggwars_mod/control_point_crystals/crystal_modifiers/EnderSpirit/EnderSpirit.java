package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.EnderSpirit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;

import java.util.*;

public class EnderSpirit extends Modifier {

    public enum Mode {
        Heal,
        Beam
    }

    private Mode mode = Mode.Heal;

    private final int maxTargets = 2;

    private final float strength = 2.0f;

    private final float range = 15.0f;

    private final int healInterval = 20;
    private final int beamInterval = 20;

    private final Map<EggwarsEntity, Integer> beamTicks = new HashMap<>();

    private ServerWorld world;

    public EnderSpirit(ControlCrystal crystal, EggwarsPlayer player) {
        super(crystal.getPosition(), modifiers.mods.EnderSpirit, player);

        this.controlCrystal = crystal;
        this.world = (ServerWorld) crystal.getEndCrystal().getWorld();
    }

    @Override
    public void register(EggwarsMatch match) {
        super.register(match);
        match.spiritManager.addModifier(() -> this, getPosition());
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        beamTicks.clear();
    }

    public Mode getMode() {
        return mode;
    }

    public void heal(EggwarsEntity eggtarget) {
        if (mode != Mode.Heal) return;

        LivingEntity target = checkLivingEntity(eggtarget);

        if (target == null || !target.isAlive()) return;

        target.sendMessage(Text.literal("healing u"));
        target.heal(strength);
    }

    public void beam(EggwarsEntity eggtarget) {
        if (mode != Mode.Beam) return;

        LivingEntity target = checkLivingEntity(eggtarget);

        if (target == null || !target.isAlive()) return;

        // Temporary basic beam damage.
        target.sendMessage(Text.literal("damaging u"));
        target.damage(world.getDamageSources().magic(), strength);
    }

    public LivingEntity checkLivingEntity(EggwarsEntity eggentity) {
        if (eggentity == null) return null;

        Entity entity = eggentity.getEntity();

        return entity instanceof LivingEntity living
                ? living
                : null;
    }

    private List<EggwarsEntity> findTargets() {

        Box box = new Box(
                position.getX() - range,
                position.getY() - range,
                position.getZ() - range,
                position.getX() + range,
                position.getY() + range,
                position.getZ() + range
        );

        List<EggwarsEntity> targets = new ArrayList<>(
                world.getEntitiesByClass(
                                LivingEntity.class,
                                box,
                                entity -> {
                                    EggwarsEntity wrapped = match.getEggwarsEntity(entity);
                                    return wrapped != null && validTarget(wrapped);
                                }
                        ).stream()
                        .map(match::getEggwarsEntity)
                        .filter(Objects::nonNull)
                        .toList()
        );

        targets.sort(Comparator.comparingDouble(
                e -> e.getEntity().squaredDistanceTo(position.toCenterPos())
        ));

        int limit = mode == Mode.Heal ? maxTargets : 1;

        return targets.subList(0, Math.min(limit, targets.size()));
    }

    private boolean validTarget(EggwarsEntity entity) {
        EggwarsTeam targetTeam = entity.getTeam();
        return (mode == Mode.Heal) == (targetTeam == team);
    }

    @Override
    public void tick() {

        if (world == null || world.isClient()) return;

        List<EggwarsEntity> targets = findTargets();

        beamTicks.keySet().removeIf(target -> !targets.contains(target));

        for (EggwarsEntity target : targets) {
            tickTarget(target);
        }
    }

    private void tickTarget(EggwarsEntity target) {

        int interval = mode == Mode.Heal
                ? healInterval
                : beamInterval;

        int ticks = beamTicks.getOrDefault(target, 0) + 1;

        if (ticks >= interval) {

            if (mode == Mode.Heal) {
                heal(target);

            } else {
                beam(target);
            }

            ticks = 0;
        }

        beamTicks.put(target, ticks);
    }
}