package org.eggwars_game.eggwars_mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.Commands.Eggwars_Commands;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Eggwars implements ModInitializer {
    public static final String MOD_ID = "eggwars";

    public static final EggwarsMatch defaultMatch = new EggwarsMatch("default match", new BlockPos(-5,-5,-5), new BlockPos(5,5,5));
    public static final Map<EggwarsMatch, String> matches = new HashMap<>();


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        Eggwars_Commands.register(dispatcher, registryAccess));

        ModItems.registerModItems();
        EggwarsTick.register();

    }




    public static EggwarsMatch getMatch(ServerPlayerEntity player) {
        return matches.keySet().stream()
                .filter(match -> match.getEggwarsPlayer(player) != null)
                .findFirst()
                .orElse(null);
    }

    public static EggwarsMatch getMatch(Entity entity) {
        return matches.keySet().stream()
                .filter(match -> match.getEggwarsEntity(entity) != null)
                .findFirst()
                .orElse(null);
    }

    public static EggwarsMatch getMatch(BlockPos pos) {
        for (EggwarsMatch match : matches.keySet()) {
            if (match.getBounds().contains(
                    Vec3d.ofCenter(pos)
            )) {
                return match;
            }
        }

        return null;
    }





    public static Map<EggwarsMatch, String> getMatches() {
        return matches;
    }
    public static EggwarsMatch startMatch(String name, BlockPos center) {
        EggwarsMatch match = new EggwarsMatch(name, center.add(100,100,100), center.add(-100,-100,-100));
        matches.put(match, name);
        return match;
    }
    public static void removeMatch(EggwarsMatch match) {
        matches.remove(match);
    }

    public static void addToMatch(Entity entity, EggwarsMatch match){
        entity.sendMessage(Text.literal("added to match : "+match.getName()));
        match.addEntity(entity, null);
    }

    public static boolean removeFromMatch(EggwarsEntity entity){

        EggwarsMatch match = entity.getMatch();
        if (match == null) return false;
        return match.removeEntity(entity);

    }

    public static EggwarsPlayer getEggwarsPlayer(ServerPlayerEntity player) {
        EggwarsMatch match = getMatch(player);
        return match == null ? null : match.getEggwarsPlayer(player);
    }

}






