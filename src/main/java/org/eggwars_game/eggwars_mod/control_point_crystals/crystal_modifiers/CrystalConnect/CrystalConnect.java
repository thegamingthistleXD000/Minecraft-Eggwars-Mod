package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect;


import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.Utils.RayCast;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;






public class CrystalConnect extends Modifier {

    @Override
    public void register(EggwarsMatch match, BlockPos pos) {
        match.modManager.addModifier(() -> this, pos);
        match.connectManager.addModifier(() -> this, pos);
    }

    private boolean active = true;
    public CrystalConnect(EggwarsPlayer player, ControlCrystal ccA, ControlCrystal ccB) {
        super(ccB.getPosition(), modifiers.mods.CrystalConnect, player);
        linkedCrystalreciever = ccA;
        linkedCrystalsender= ccB;
        super.controlCrystal =  ccB;

    }


    @Override
    public void tick() {
        checkObstruction(world);

    }

    public enum Mode {Fast_Travel, Control_Zone}
    private Mode mode;

    public CrystalConnect.Mode getMode() {return mode;}
    public void setMode(CrystalConnect.Mode m) {mode = m;}

    private final ControlCrystal linkedCrystalsender;
    private final ControlCrystal linkedCrystalreciever;

    public boolean checkObstruction (ServerWorld world) {
        return active = !RayCast.getRayObstructions(world, linkedCrystalreciever.getPosition(), linkedCrystalsender.getPosition(), 1).isEmpty();
    }




}
