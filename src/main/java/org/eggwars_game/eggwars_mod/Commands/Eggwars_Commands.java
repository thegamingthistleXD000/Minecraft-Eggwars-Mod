package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import org.eggwars_game.eggwars_mod.EggwarsMatch;

public class Eggwars_Commands {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess
    ) {
        EggwarsMatchCommands.register(dispatcher);
        SetCrystalSpotCommand.register(dispatcher);
        GiveDataCommand.register(dispatcher, registryAccess);
        CheckItemDataCommand.register(dispatcher);
        CheckEntityDataCommand.register(dispatcher);
        GiveModifierCommand.register(dispatcher);
        EggwarsTeamCommands.register(dispatcher);
    }
}