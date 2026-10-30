package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;

import java.util.Objects;
import java.util.UUID;

public abstract class Modifier {
    protected final UUID modifierID;
    protected ControlCrystal controlCrystal;
    protected BlockPos position;
    protected int Level;
    protected final EggwarsPlayer player;
    protected final EggwarsMatch match;
    protected EggwarsTeam team;
    protected ServerWorld world;


    modifiers.mods mod;

    private boolean active = true;


    public EggwarsTeam getTeam() {
        return team;
    }

    public Modifier(BlockPos position, modifiers.mods m, EggwarsPlayer player) {
        modifierID = UUID.randomUUID();
        this.position = position;
        Level = 1;
        mod = m;
        this.team = Objects.requireNonNull(Eggwars.getEggwarsPlayer(player.getPlayer())).getTeam();
        this.player = player;
        this.match = player.getMatch();
        world = player.getPlayer().getServerWorld();
        System.out.println("Modifier created");
    }

    public abstract void register(EggwarsMatch match, BlockPos pos);

    public UUID getModifierID() {
        return modifierID;
    }


    public void invertActive() {
        active = !active;
    }

    public BlockPos getPosition() {
        return position;
    }

    public ControlCrystal getControlCrystal() {
        return controlCrystal;
    }


    public abstract void tick();

    public void setControlCrystal(ControlCrystal controlCrystal) {

        this.controlCrystal = controlCrystal;
    }

}
