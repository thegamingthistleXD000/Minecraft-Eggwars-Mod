package org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals;

import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;

public class ResourceCrystal implements CrystalComponent {

    // seconds per resource
    float iron_rate;
    float diamond_rate;
    float gold_rate;
    float emerald_rate;

    // internal timers (ticks)
    private int ironTimer = 0;
    private int goldTimer = 0;
    private int diamondTimer = 0;
    private int emeraldTimer = 0;

    public ResourceCrystal(float iron, float gold, float diamond, float emerald) {
        this.iron_rate = iron;
        this.gold_rate = gold;
        this.diamond_rate = diamond;
        this.emerald_rate = emerald;
    }

    public void setIron_rate(float iron_rate) {this.iron_rate = iron_rate;}
    public void setGold_rate(float gold_rate) {this.gold_rate = gold_rate;}
    public void setDiamond_rate(float diamond_rate) {this.diamond_rate = diamond_rate;}
    public void setEmerald_rate(float emerald_rate) {this.emerald_rate = emerald_rate;}

    public void spawnResource(ControlCrystal crystal, net.minecraft.item.Item res) {
        var world = crystal.getEndCrystal().getWorld();

        if (world.isClient()) return;

        var entity = crystal.getEndCrystal();

        net.minecraft.entity.ItemEntity drop = new net.minecraft.entity.ItemEntity(
                world,
                entity.getX(),
                entity.getY() + 1.2,
                entity.getZ(),
                new net.minecraft.item.ItemStack(res)
        );

        // tag so we know this item came from THIS crystal
//        drop.getPersistentData().putUuid(
//                "CrystalOwner",
//                entity.getUuid()
//        );

        drop.setNeverDespawn();
        world.spawnEntity(drop);
    }

    @Override
    public void onBreak(ControlCrystal crystal) {
        var world = crystal.getEndCrystal().getWorld();
        var pos = crystal.getEndCrystal().getBlockPos();

        net.minecraft.util.math.Box box = new net.minecraft.util.math.Box
                (pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
                        pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

//        var items = world.getEntitiesByClass(
//                net.minecraft.entity.ItemEntity.class,
//                box,
//                item -> item.getPersistentData().containsUuid("CrystalOwner")
//                        && item.getPersistentData().getUuid("CrystalOwner")
//                        .equals(crystal.getEndCrystal().getUuid())
//        );
//
//        for (var item : items) {
//            item.discard();
//        }
    }

    @Override
    public void tick(ControlCrystal crystal) {
        ironTimer++;
        goldTimer++;
        diamondTimer++;
        emeraldTimer++;

        // convert seconds -> ticks (20 ticks = 1 second)
        if (iron_rate > 0 && ironTimer >= (int) (iron_rate * 20)) {
            ironTimer = 0;
            spawnResource(crystal, net.minecraft.item.Items.IRON_INGOT);
        }

        if (gold_rate > 0 && goldTimer >= (int) (gold_rate * 20)) {
            goldTimer = 0;
            spawnResource(crystal, net.minecraft.item.Items.GOLD_INGOT);
        }

        if (diamond_rate > 0 && diamondTimer >= (int) (diamond_rate * 20)) {
            diamondTimer = 0;
            spawnResource(crystal, net.minecraft.item.Items.DIAMOND);
        }

        if (emerald_rate > 0 && emeraldTimer >= (int) (emerald_rate * 20)) {
            emeraldTimer = 0;
            spawnResource(crystal, net.minecraft.item.Items.EMERALD);
        }
    }
}
