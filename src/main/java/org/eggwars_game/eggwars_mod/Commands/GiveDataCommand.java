package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;

public class GiveDataCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess
    ) {
        dispatcher.register(
                CommandManager.literal("eggwars")
                        .then(CommandManager.literal("data")
                                .then(CommandManager.literal("give")
                                        .then(CommandManager.argument(
                                                        "player",
                                                        EntityArgumentType.player()
                                                )
                                                .then(CommandManager.argument(
                                                                "item",
                                                                ItemStackArgumentType.itemStack(registryAccess)
                                                        )
                                                        .then(CommandManager.argument(
                                                                        "key",
                                                                        StringArgumentType.word()
                                                                )
                                                                .then(CommandManager.argument(
                                                                                "value",
                                                                                StringArgumentType.string()
                                                                        )
                                                                        .executes(context -> {
                                                                            ServerPlayerEntity player =
                                                                                    EntityArgumentType.getPlayer(
                                                                                            context, "player"
                                                                                    );

                                                                            ItemStack stack =
                                                                                    ItemStackArgumentType
                                                                                            .getItemStackArgument(
                                                                                                    context, "item"
                                                                                            )
                                                                                            .createStack(1, false);

                                                                            String key =
                                                                                    StringArgumentType.getString(
                                                                                            context, "key"
                                                                                    );

                                                                            String value =
                                                                                    StringArgumentType.getString(
                                                                                            context, "value"
                                                                                    );

                                                                            NBTUtils.set(stack, key, value);
                                                                            player.getInventory().insertStack(stack);

                                                                            return 1;
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }
}