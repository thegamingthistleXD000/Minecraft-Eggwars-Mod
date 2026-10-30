package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;

public class CheckEntityDataCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
                CommandManager.literal("eggwars")
                        .then(CommandManager.literal("data")
                                .then(CommandManager.literal("check")
                                        .then(CommandManager.literal("entity")
                                                .then(CommandManager.argument(
                                                                "entity",
                                                                EntityArgumentType.entity()
                                                        )
                                                        .then(CommandManager.argument(
                                                                        "key",
                                                                        StringArgumentType.word()
                                                                )
                                                                .executes(context -> checkEntity(context, false))
                                                                .then(CommandManager.argument(
                                                                                "value",
                                                                                StringArgumentType.string()
                                                                        )
                                                                        .executes(context -> checkEntity(context, true))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int checkEntity(
            CommandContext<ServerCommandSource> context,
            boolean checkValue
    ) throws CommandSyntaxException {

        Entity entity = EntityArgumentType.getEntity(context, "entity");
        String key = StringArgumentType.getString(context, "key");

        if (!NBTUtils.has(entity, key))
            return sendResult(context.getSource(), false);

        if (checkValue) {
            String value = StringArgumentType.getString(context, "value");

            if (!value.equals(NBTUtils.getString(entity, key)))
                return sendResult(context.getSource(), false);
        }

        return sendResult(context.getSource(), true);
    }

    private static int sendResult(ServerCommandSource source, boolean result) {
        source.sendFeedback(() -> Text.literal(String.valueOf(result)), false);
        return result ? 1 : 0;
    }
}