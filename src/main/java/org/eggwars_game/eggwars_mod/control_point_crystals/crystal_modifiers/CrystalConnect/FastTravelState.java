package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect;

import net.minecraft.entity.player.PlayerEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;


public class FastTravelState {


    public static void updateFastTravel(EggwarsPlayer player) {

        EggwarsMatch match = player.getMatch();
        if (match == null) return;

        EggwarsTeam team = player.getTeam();
        if (team == null) return;

        PlayerEntity serverPlayer = player.getPlayer();

        boolean fastTravel = false;

        for (CrystalConnect connect : match.connectManager.getModifiers()) {
            if (connect.isInCrystalRay(player)) {
                fastTravel = true;
                break;
            }

        }

        serverPlayer.setNoGravity(fastTravel);

        player.setFastTravelling(fastTravel);
    }



}
