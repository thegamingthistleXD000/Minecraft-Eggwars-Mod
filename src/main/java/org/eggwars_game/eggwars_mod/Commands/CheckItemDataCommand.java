package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;

public class CheckItemDataCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
                CommandManager.literal("eggwars")
                        .then(CommandManager.literal("data")
                                .then(CommandManager.literal("check")
                                        .then(CommandManager.literal("item")
                                                .then(CommandManager.argument(
                                                                "entity",
                                                                EntityArgumentType.entity()
                                                        )
                                                        .then(CommandManager.argument(
                                                                        "slot",
                                                                        StringArgumentType.word()
                                                                )
                                                                .then(CommandManager.argument(
                                                                                "key",
                                                                                StringArgumentType.word()
                                                                        )
                                                                        .executes(context -> checkItem(context, false))
                                                                        .then(CommandManager.argument(
                                                                                        "value",
                                                                                        StringArgumentType.string()
                                                                                )
                                                                                .executes(context -> checkItem(context, true))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int checkItem(
            com.mojang.brigadier.context.CommandContext<ServerCommandSource> context,
            boolean checkValue
    ) throws com.mojang.brigadier.exceptions.CommandSyntaxException {

        Entity entity = EntityArgumentType.getEntity(context, "entity");

        if (!(entity instanceof ServerPlayerEntity player))
            return sendResult(context.getSource(), false);

        String slot = StringArgumentType.getString(context, "slot");

        ItemStack stack = switch (slot) {
            case "mainhand" -> player.getMainHandStack();
            case "offhand" -> player.getOffHandStack();
            default -> null;
        };

        if (stack == null || stack.isEmpty())
            return sendResult(context.getSource(), false);

        String key = StringArgumentType.getString(context, "key");

        if (!NBTUtils.has(stack, key))
            return sendResult(context.getSource(), false);

        if (checkValue) {
            String value = StringArgumentType.getString(context, "value");

            if (!value.equals(NBTUtils.getString(stack, key)))
                return sendResult(context.getSource(), false);
        }

        return sendResult(context.getSource(), true);
    }

    private static int sendResult(ServerCommandSource source, boolean result) {
        source.sendFeedback(() -> Text.literal(String.valueOf(result)), false);
        return result ? 1 : 0;
    }
}