package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.control_point_crystals.SpecialSpot;

public class SetCrystalSpotCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
                CommandManager.literal("eggwars")
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument(
                                                "pos",
                                                BlockPosArgumentType.blockPos()
                                        )
                                        .then(CommandManager.literal("crystalspot")
                                                .then(CommandManager.argument(
                                                                "type",
                                                                StringArgumentType.word()
                                                        )
                                                        .suggests((context, builder) -> {
                                                            builder.suggest("control");
                                                            builder.suggest("iron_gen");
                                                            builder.suggest("gold_gen");
                                                            builder.suggest("dia_gen");
                                                            builder.suggest("em_gen");
                                                            builder.suggest("spawn");
                                                            builder.suggest("home");
                                                            builder.suggest("game");
                                                            builder.suggest("illegal");
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(context -> {
                                                            ServerCommandSource source =
                                                                    context.getSource();

                                                            BlockPos pos =
                                                                    BlockPosArgumentType.getBlockPos(
                                                                            context, "pos"
                                                                    );

                                                            String type =
                                                                    StringArgumentType.getString(
                                                                            context, "type"
                                                                    );

                                                            SpecialSpot spot = parseSpot(type);

                                                            if (spot == null) {
                                                                source.sendError(
                                                                        Text.literal(
                                                                                "Unknown crystal spot: " + type
                                                                        )
                                                                );
                                                                return 0;
                                                            }

                                                            var player = source.getPlayer();
                                                            EggwarsMatch match =
                                                                    Eggwars.getMatch((Entity) player);

                                                            if (match == null) {
                                                                source.sendError(
                                                                        Text.literal("You are not in a match.")
                                                                );
                                                                return 0;
                                                            }

                                                            match.addSpot(pos, spot);

                                                            source.sendFeedback(
                                                                    () -> Text.literal(
                                                                            "Set crystal spot " + spot + " at " + pos
                                                                    ),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
        );
    }

    private static SpecialSpot parseSpot(String type) {
        return switch (type.toLowerCase()) {
            case "control" -> SpecialSpot.Control;
            case "iron_gen" -> SpecialSpot.Iron;
            case "gold_gen" -> SpecialSpot.Gold;
            case "dia_gen" -> SpecialSpot.Diamond;
            case "em_gen" -> SpecialSpot.Emerald;
            case "spawn" -> SpecialSpot.Spawn;
            case "home" -> SpecialSpot.Home;
            case "game" -> SpecialSpot.Game;
            case "illegal" -> SpecialSpot.Illegal;
            default -> null;
        };
    }
}