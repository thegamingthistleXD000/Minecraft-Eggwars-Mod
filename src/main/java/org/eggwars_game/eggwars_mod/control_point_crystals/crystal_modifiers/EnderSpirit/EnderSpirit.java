package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.EnderSpirit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;

import java.util.*;
import java.util.stream.Collectors;

public class EnderSpirit extends Modifier {

    @Override
    public void register(EggwarsMatch match, BlockPos pos) {
        match.modManager.addModifier(() -> this, pos);
        match.spiritManager.addModifier(() -> this, pos);
    }

    public EnderSpirit(ControlCrystal crystal, EggwarsPlayer player) {
        super(crystal.getPosition(), modifiers.mods.EnderSpirit, player);
        super.controlCrystal =  crystal;

    }

    enum Mode{Heal, Beam}
    Mode mode = Mode.Heal;
    int maxTargets = 2;

    float strength = 2;
    private float range;

    ServerWorld world = (ServerWorld) super.controlCrystal.getEndCrystal().getWorld();
    private final Map<EggwarsEntity, Integer> beamTicks = new HashMap<>();


    public void heal(EggwarsEntity eggtarget) {

        LivingEntity livetarget = checkLivingEntity(eggtarget);
        if (livetarget == null) return;
        if (!mode.equals(Mode.Heal)){return;}
        livetarget.heal(strength);
    }

    public void beam(EggwarsEntity eggtarget) {

        LivingEntity livetarget = checkLivingEntity(eggtarget);
        if (livetarget == null) return;
        if (!mode.equals(Mode.Heal)){return;}
        livetarget.heal(strength);
    }

    public LivingEntity checkLivingEntity (EggwarsEntity eggentity){
        Entity entity = eggentity.getEntity();
        return (entity instanceof LivingEntity) ? (LivingEntity) entity : null;


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

        List<EggwarsEntity> targets = world.getEntitiesByClass(
                        LivingEntity.class,
                        box,
                        entity -> validTarget(
                                match.getEggwarsEntity(entity)
                        )
                ).stream()
                .map(match::getEggwarsEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        targets.sort(Comparator.comparingDouble(
                e -> e.getEntity().squaredDistanceTo(position.toCenterPos())
        ));

        return targets.subList(0, Math.min(maxTargets, targets.size()));
    }


    private boolean validTarget(EggwarsEntity entity) {

        EggwarsTeam targetTeam = entity.getTeam();

        return (mode == Mode.Heal) == (targetTeam == team);
    }

    @Override
    public void tick(){
        List<EggwarsEntity> targets = findTargets();
        EggwarsEntity target1 = targets.get(0);
        EggwarsEntity target2 = targets.get(1);

        for (EggwarsEntity target : targets)
            tickBeam(target);

        beamTicks.keySet().removeIf(target -> !targets.contains(target));
    }


    public void tickBeam(EggwarsEntity target) {
        int ticks = beamTicks.getOrDefault(target, 0) + 1;

        if (ticks >= beamTicks.get(target)) {
            if (mode == Mode.Heal)
                heal(target);
            else
                beam(target);

            ticks = 0;
        }

        beamTicks.put(target, ticks);
    }


}
