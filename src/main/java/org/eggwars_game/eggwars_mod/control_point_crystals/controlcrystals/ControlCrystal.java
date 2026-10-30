package org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.CrystalComponent;

import java.util.ArrayList;
import java.util.List;



public class ControlCrystal extends EggwarsEntity {

    private final EndCrystalEntity endCrystal;
    private final List<CrystalComponent> components = new ArrayList<>();

    public ControlCrystal(EndCrystalEntity endCrystal, EggwarsMatch  match, EggwarsTeam team) {
        super(endCrystal, match, team);
        this.endCrystal = endCrystal;
    }

    public void addComponent(CrystalComponent component) {
        components.add(component);
    }

    public boolean hasComponent(Class<? extends CrystalComponent> type) {
        for (CrystalComponent c : components) {
            if (type.isInstance(c)) {
                return true;
            }
        }
        return false;
    }

    public <T extends CrystalComponent> T getComponent(Class<T> type) {

        for (CrystalComponent c : components) {

            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }

        return null;
    }

    public BlockPos getPosition (){
        return endCrystal.getBlockPos();
    }

    public void tick() {
        for (CrystalComponent c : components) {
            c.tick(this);
        }
    }

    public void breakCrystal() {
        for (CrystalComponent c : components) {
            c.onBreak(this);
        }
    }

    public EndCrystalEntity getEndCrystal() {
        return endCrystal;
    }

}


