package org.eggwars_game.eggwars_mod.Players;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.SpawnCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect.FastTravelState;

import java.util.ArrayList;
import java.util.Objects;

public class EggwarsPlayer extends EggwarsEntity {

    int extra_lives = 1;
    @Getter private final PlayerEntity player;

    @Setter @Getter boolean fastTravelling = false;

    public EggwarsPlayer(ServerPlayerEntity player, EggwarsMatch match, EggwarsTeam team) {
        super(player,match, team);
        this.player = player;
    }


    public ServerPlayerEntity getServerPlayer() {
        return player instanceof ServerPlayerEntity serverPlayer
                ? serverPlayer
                : null;
    }
    

    void Respawn (ControlCrystal crystal){
        if (!findSpawnCrystals().contains(crystal)){
            return;
        }
        Objects.requireNonNull(player.getServer()).getPlayerManager().respawnPlayer((ServerPlayerEntity) player, false, Entity.RemovalReason.KILLED);

        BlockPos spawnPos = crystal.getEndCrystal().getBlockPos();
        player.teleport(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), false);
    }

    void Respawn (){
        if  (!findSpawnCrystals().isEmpty()){
            return;
        }
        if (extra_lives <= 0){
            return;
        }


    }


    private ArrayList<ControlCrystal> findSpawnCrystals (){
        ArrayList<ControlCrystal> spawnCrystals = new ArrayList<ControlCrystal>();

        for (ControlCrystal crystal : getMatch().getCrystals()) {
            if (crystal.hasComponent(SpawnCrystal.class)) {
                spawnCrystals.add(crystal);
            }
        }
        return spawnCrystals;
    }

    public void tick() {

        FastTravelState.updateFastTravel(this);

    }

}
