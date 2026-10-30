package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;










public class FastTravelState {


    public static void updateFastTravel(EggwarsPlayer player) {

        boolean fastTravel = false;

        EggwarsMatch match = player.getMatch();
        ServerPlayerEntity serverPlayer = player.getPlayer();

        for (ControlCrystal from : match.getCrystals()) {
            for (ControlCrystal to : match.getCrystalLinks(from)) {

                if (isInCrystalRay(serverPlayer, from, to)) {
                    fastTravel = true;
                    break;
                }
            }

            if (fastTravel) break;
        }


        serverPlayer.setNoGravity(fastTravel);
        serverPlayer.setSwimming(fastTravel);

        if (fastTravel) {
            serverPlayer.getAbilities().setWalkSpeed(0.2f);
        } else {
            serverPlayer.getAbilities().setWalkSpeed(0.1f);
        }
    }

    public static boolean isInCrystalRay(ServerPlayerEntity player, ControlCrystal from, ControlCrystal to) {
        Vec3d start = from.getPosition().toCenterPos();
        Vec3d end = to.getPosition().toCenterPos();

        Vec3d ray = end.subtract(start);
        double length = ray.length();

        if (length == 0) return false;

        Vec3d direction = ray.normalize();
        Vec3d relative = player.getPos().subtract(start);

        double distance = relative.dotProduct(direction);

        if (distance < 0 || distance > length) return false;

        Vec3d closest = start.add(direction.multiply(distance));

        return player.getPos().squaredDistanceTo(closest) <= 0.25;
    }

}
