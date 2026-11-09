package org.eggwars_game.eggwars_mod;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.Commands.Eggwars_Commands;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks.ModBlocks;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities.ModEntities;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items.ModItems;
import org.eggwars_game.network.NetworkRegistry;
import org.eggwars_game.network.ServerNetworkSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class Eggwars implements ModInitializer {
    public static final String MOD_ID = "eggwars";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EggwarsMatch defaultMatch = new EggwarsMatch("default match", new BlockPos(-5,-5,-5), new BlockPos(5,5,5));
    @Getter public static final Map<EggwarsMatch, String> matches = new HashMap<>();


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        Eggwars_Commands.register(dispatcher, registryAccess));

        ModItems.registerModItems();

        Eggwars.LOGGER.info(
                "Cobweb arrow tagged: {}",
                new ItemStack(ModItems.COBWEB_ARROW).isIn(ItemTags.ARROWS)
        );

        Eggwars.LOGGER.info(
                "Sponge arrow tagged: {}",
                new ItemStack(ModItems.SPONGE_ARROW).isIn(ItemTags.ARROWS)
        );

        ModBlocks.registerModBlocks();
        ModEntities.registerModEntities();
        EggwarsTick.register();

        NetworkRegistry.register();

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

        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerNetworkSender.sendAddToMatch(serverPlayerEntity, match);

        }
    }

    public static boolean removeFromMatch(EggwarsEntity entity){

        EggwarsMatch match = entity.getMatch();
        if (match == null) return false;

        if (entity instanceof EggwarsPlayer eggwarsPlayer) {
            ServerNetworkSender.sendRemoveFromMatch(eggwarsPlayer);
        }
        return match.removeEntity(entity);


    }

    public static EggwarsPlayer getEggwarsPlayer(PlayerEntity player) {
        EggwarsMatch match = getMatch(player);
        return match == null ? null : match.getEggwarsPlayer(player);
    }

}






