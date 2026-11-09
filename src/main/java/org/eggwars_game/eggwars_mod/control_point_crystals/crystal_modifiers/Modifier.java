package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;

import java.util.Objects;
import java.util.UUID;

public abstract class Modifier {
    @Getter protected final UUID modifierID;
    @Setter @Getter protected ControlCrystal controlCrystal;
    @Getter protected BlockPos position;
    protected int level;
    @Getter protected final EggwarsPlayer player;
    protected final EggwarsMatch match;
    @Getter protected EggwarsTeam team;
    protected World world;

    @Getter
    modifiers.mods mod;
    
    private boolean active = true;


    protected Modifier(BlockPos position, modifiers.mods m, EggwarsPlayer player) {
        modifierID = UUID.randomUUID();
        this.position = position;
        level = 1;
        mod = m;
        this.team = Objects.requireNonNull(Eggwars.getEggwarsPlayer(player.getPlayer())).getTeam();
        this.player = player;
        this.match = player.getMatch();
        world = player.getPlayer().getWorld();
    }

    public void register(EggwarsMatch match){
        match.modManager.addModifier(() -> this, getPosition());

    }


    public void invertActive() {
        active = !active;
    }


    public abstract void tick();

}
