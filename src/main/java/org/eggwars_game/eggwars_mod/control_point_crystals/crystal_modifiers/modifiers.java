package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers;

import java.util.Map;

public record modifiers() {

    public static enum mods {ChorusWarp, ForceField, CrystalConnect, PearlBarrier, EnderSpirit, None}
    public static final Map<mods, String> linknames =  Map.of(mods.ChorusWarp, "ChorusWarp", mods.ForceField,  "ForceField", mods.CrystalConnect, "CrystalConnect", mods.PearlBarrier, "PearlBarrier");


}
