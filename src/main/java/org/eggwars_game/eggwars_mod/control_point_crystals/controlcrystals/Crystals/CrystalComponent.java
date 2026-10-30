package org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals;

import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;

public interface CrystalComponent {
    default void tick(ControlCrystal crystal) {
    }

    default void onBreak(ControlCrystal crystal) {
    }

}
