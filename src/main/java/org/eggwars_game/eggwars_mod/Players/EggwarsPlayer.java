package org.eggwars_game.eggwars_mod.Players;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.SpawnCrystal;

import java.util.ArrayList;

public class EggwarsPlayer extends EggwarsEntity {

    int extra_lives = 1;
    private final ServerPlayerEntity player;

    GameMode gamemode = GameMode.SURVIVAL;

    public EggwarsPlayer(ServerPlayerEntity player, EggwarsMatch match, EggwarsTeam team) {
        super(player,match, team);
        this.player = player;
    }


    void Respawn (ControlCrystal crystal){
        if (!findSpawnCrystals().contains(crystal)){
            return;
        }
        player.getServer().getPlayerManager().respawnPlayer(player, false, Entity.RemovalReason.KILLED);

        BlockPos SpawnPos = crystal.getEndCrystal().getBlockPos();
        player.teleport(SpawnPos.getX(), SpawnPos.getY(), SpawnPos.getZ(), false);
    }

    void Respawn (){
        if  (!findSpawnCrystals().isEmpty()){
            return;
        }
        if (extra_lives <= 0){
            return;
        }


    }



    public GameMode getgamemode (){
        return gamemode;
    }
    public void setgamemode (GameMode gamemode){
        this.gamemode = gamemode;
    }

    public ServerPlayerEntity getPlayer(){return player;}

    private ArrayList<ControlCrystal> findSpawnCrystals (){
        ArrayList<ControlCrystal> spawnCrystals = new ArrayList<ControlCrystal>();

        for (ControlCrystal crystal : getMatch().getCrystals()) {
            if (crystal.hasComponent(SpawnCrystal.class)) {
                spawnCrystals.add(crystal);
            }
        }
        return spawnCrystals;
    }

}
