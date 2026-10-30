package org.eggwars_game.eggwars_mod;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;

public class EggwarsEntity {
    private final EggwarsMatch match;
    protected final Entity entity;

    private EggwarsTeam team;

    public EggwarsEntity(Entity entity, EggwarsMatch match, EggwarsTeam team) {
        this.match = match;
        this.entity = entity;
        this.team = team;

    }

    public EggwarsMatch getMatch() {
        return match;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isPlayer() {
        return entity instanceof net.minecraft.server.network.ServerPlayerEntity;
    }

    public EggwarsPlayer asPlayer() {
        if (!isPlayer()) return null;
        return match.getEggwarsPlayer((ServerPlayerEntity) entity);
    }

    public boolean isAlive() {
        return entity.isAlive();
    }


    void GoToCrystal(ControlCrystal crystal){

    }

    public EggwarsTeam getTeam (){return team;}
    public void setTeam(EggwarsTeam team){
        this.team = team;
        team.addMember(this);
    }


}