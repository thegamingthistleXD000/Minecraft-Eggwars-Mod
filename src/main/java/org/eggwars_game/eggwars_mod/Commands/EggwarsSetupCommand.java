package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class EggwarsSetupCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
                literal("eggwars")
                        .then(literal("setup")
                                .executes(ctx -> setup(ctx.getSource()))
                        )
        );
    }

    private static int setup(ServerCommandSource source) {

        var commandManager = source.getServer().getCommandManager();

        commandManager.executeWithPrefix(
                source,
                "eggwars match start test"
        );

        commandManager.executeWithPrefix(
                source,
                "eggwars match join test"
        );

        commandManager.executeWithPrefix(
                source,
                "eggwars team create hi"
        );

        commandManager.executeWithPrefix(
                source,
                "eggwars team join hi"
        );

        source.sendFeedback(
                () -> Text.literal("EggWars setup completed."),
                false
        );

        return 1;
    }
}