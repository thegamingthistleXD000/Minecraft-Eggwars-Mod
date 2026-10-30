package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;

import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.eggwars_game.eggwars_mod.Commands.EggwarsTeamCommands.getMatchContext;
import static org.eggwars_game.eggwars_mod.Commands.EggwarsTeamCommands.setMatchContext;

public class EggwarsMatchCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("eggwars")
                        .then(literal("match")

                                // /eggwars match start <name>
                                .then(literal("start")
                                        .then(argument("name", StringArgumentType.word())
                                                .executes(ctx -> start(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name")
                                                ))
                                        )
                                )

                                // /eggwars match stop <name>
                                // /eggwars match stop ctx
                                .then(literal("stop")
                                        .then(argument("name", StringArgumentType.word())
                                                .suggests(EggwarsMatchCommands::suggestMatchNames)
                                                .executes(ctx -> stop(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        false
                                                ))
                                        )
                                        .then(literal("ctx")
                                                .executes(ctx -> stop(
                                                        ctx.getSource(),
                                                        null,
                                                        true
                                                ))
                                        )
                                )

                                // /eggwars match list
                                .then(literal("list")
                                        .executes(ctx -> list(ctx.getSource()))
                                )

                                // /eggwars match context <name>
                                .then(literal("context")
                                        .then(argument("name", StringArgumentType.word())
                                                .suggests(EggwarsMatchCommands::suggestMatchNames)
                                                .executes(ctx -> selectContext(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name")
                                                ))
                                        )
                                )

                                // /eggwars match join <name> [targets]
                                // Defaults to self if targets are omitted.
                                .then(literal("join")
                                        .then(argument("name", StringArgumentType.word())
                                                .suggests(EggwarsMatchCommands::suggestMatchNames)
                                                .executes(ctx -> join(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        null
                                                ))
                                                .then(argument("targets", EntityArgumentType.entities())
                                                        .executes(ctx -> join(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "name"),
                                                                EntityArgumentType.getEntities(ctx, "targets")
                                                        ))
                                                )
                                        )
                                )

                                // /eggwars match leave [targets]
                                // Defaults to self if targets are omitted.
                                .then(literal("leave")
                                        .executes(ctx -> leave(ctx.getSource(), null))
                                        .then(argument("targets", EntityArgumentType.entities())
                                                .executes(ctx -> leave(
                                                        ctx.getSource(),
                                                        EntityArgumentType.getEntities(ctx, "targets")
                                                ))
                                        )
                                )

                                // /eggwars match [target]
                                // Defaults to self if target is omitted.
                                .executes(ctx -> showEntityMatch(
                                        ctx.getSource(),
                                        ctx.getSource().getEntity()
                                ))
                                .then(argument("target", EntityArgumentType.entity())
                                        .executes(ctx -> showEntityMatch(
                                                ctx.getSource(),
                                                EntityArgumentType.getEntity(ctx, "target")
                                        ))
                                )

                                // /eggwars match context-of [target]
                                // Defaults to self if target is omitted.
                                .then(literal("context-of")
                                        .executes(ctx -> showEntityContext(
                                                ctx.getSource(),
                                                ctx.getSource().getEntity()
                                        ))
                                        .then(argument("target", EntityArgumentType.entity())
                                                .executes(ctx -> showEntityContext(
                                                        ctx.getSource(),
                                                        EntityArgumentType.getEntity(ctx, "target")
                                                ))
                                        )
                                )
                        )
        );
    }

    private static int start(ServerCommandSource source, String name) {
        Entity entity = source.getEntity();

        if (entity == null) {
            source.sendError(Text.literal(
                "You must be a player to start a match at your position."
            ));
            return 0;
        }

        EggwarsMatch match = Eggwars.startMatch(name, entity.getBlockPos());

        source.sendFeedback(
            () -> Text.literal("Started a new Eggwars match."),
            false
        );

        return 1;
    }

    private static int stop(
            ServerCommandSource source,
            String name,
            boolean useContext
    ) {
        EggwarsMatch match;

        if (useContext) {
            EggwarsEntity wrapper = getExecutorWrapper(source);
            match = wrapper == null
                    ? null
                    : getMatchContext(wrapper);

            if (match == null) {
                source.sendError(Text.literal(
                        "You have no selected context match."
                ));
                return 0;
            }
        } else {
            match = Eggwars.getMatches().entrySet().stream()
                    .filter(entry -> entry.getValue() != null
                            && entry.getValue().equalsIgnoreCase(name))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (match == null) {
                source.sendError(Text.literal(
                        "Match '" + name + "' does not exist."
                ));
                return 0;
            }
        }

        String matchName = Eggwars.getMatches().getOrDefault(match, "Unknown");

        Eggwars.removeMatch(match);
        Eggwars.getMatches().remove(match);

        source.sendFeedback(
                () -> Text.literal("Removed match '" + matchName + "'."),
                false
        );

        return 1;
    }

    private static int list(ServerCommandSource source) {
        Map<EggwarsMatch, String> matches = Eggwars.getMatches();

        if (matches.isEmpty()) {
            source.sendFeedback(
                () -> Text.literal("There are no active matches."),
                false
            );
            return 0;
        }

        source.sendFeedback(
            () -> Text.literal("Active Eggwars matches:"),
            false
        );

        int index = 1;

        for (Map.Entry<EggwarsMatch, String> entry : matches.entrySet()) {
            final int currentIndex = index++;
            final String name = entry.getValue();

            source.sendFeedback(
                    () -> Text.literal(currentIndex + "." + name),
                    false
            );
        }

        return matches.size();
    }

    private static int selectContext(
            ServerCommandSource source,
            String matchName
    ) {
        Entity entity = source.getEntity();

        if (entity == null) {
            source.sendError(Text.literal(
                    "You must be an entity to select a context match."
            ));
            return 0;
        }

        EggwarsMatch actualMatch = Eggwars.getMatch(entity);

        EggwarsEntity wrapper = actualMatch.getEggwarsEntity(entity);

        if (wrapper == null) {
            source.sendError(Text.literal(
                    "Your Eggwars entity wrapper was not found."
            ));
            return 0;
        }

        EggwarsMatch selectedMatch = Eggwars.getMatches().entrySet().stream()
                .filter(entry -> entry.getValue() != null
                        && entry.getValue().equalsIgnoreCase(matchName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (selectedMatch == null) {
            source.sendError(Text.literal(
                    "Match '" + matchName + "' does not exist."
            ));
            return 0;
        }

        setMatchContext(wrapper, selectedMatch);

        source.sendFeedback(
                () -> Text.literal(
                        "Selected match '" + matchName + "' as your context."
                ),
                false
        );

        return 1;
    }

    private static EggwarsEntity getExecutorWrapper(
            ServerCommandSource source
    ) {
        Entity entity = source.getEntity();

        if (entity == null) {
            return null;
        }

        EggwarsMatch actualMatch = Eggwars.getMatch(entity);

        if (actualMatch == null) {
            return null;
        }

        return actualMatch.getEggwarsEntity(entity);
    }


    private static int showEntityMatch(
            ServerCommandSource source,
            Entity target
    ) {
        EggwarsMatch match = Eggwars.getMatch(target);

        if (match == null) {
            source.sendFeedback(
                    () -> Text.literal(
                            target.getName().getString() + " is not in a match."
                    ),
                    false
            );
            return 0;
        }

        String name = Eggwars.getMatches().get(match);

        source.sendFeedback(
                () -> Text.literal(
                        target.getName().getString() + " is in match '" + name + "'."
                ),
                false
        );

        return 1;
    }

    private static int showEntityContext(
            ServerCommandSource source,
            Entity target
    ) {
        EggwarsMatch actualMatch = Eggwars.getMatch(target);

        EggwarsEntity wrapper = actualMatch == null
                ? null
                : actualMatch.getEggwarsEntity(target);

        EggwarsMatch contextMatch = wrapper == null
                ? null
                : EggwarsTeamCommands.getMatchContext(wrapper);

        if (contextMatch == null) {
            source.sendFeedback(
                    () -> Text.literal(
                            target.getName().getString() + " has no context match selected."
                    ),
                    false
            );
            return 0;
        }

        String name = Eggwars.getMatches().get(contextMatch);

        source.sendFeedback(
                () -> Text.literal(
                        target.getName().getString()
                                + "'s context match is '" + name + "'."
                ),
                false
        );

        return 1;
    }
    private static int join(
            ServerCommandSource source,
            String matchName,
            Collection<? extends Entity> targets
    ) {
        EggwarsMatch match = Eggwars.getMatches().entrySet().stream()
                .filter(entry -> entry.getValue() != null
                        && entry.getValue().equalsIgnoreCase(matchName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (match == null) {
            source.sendError(Text.literal(
                    "Match '" + matchName + "' does not exist."
            ));
            return 0;
        }

        if (targets == null) {
            Entity self = source.getEntity();

            if (self == null) {
                source.sendError(Text.literal(
                        "Specify targets when executing from the console."
                ));
                return 0;
            }

            targets = List.of(self);
        }

        int count = 0;

        for (Entity entity : targets) {
            EggwarsMatch currentMatch = Eggwars.getMatch(entity);

            if (currentMatch == match) {
                continue;
            }

            if (currentMatch != null) {
                EggwarsEntity wrapper = currentMatch.getEggwarsEntity(entity);

                if (wrapper != null) {
                    Eggwars.removeFromMatch(wrapper);
                }
            }

            Eggwars.addToMatch(entity, match);
            count++;
        }

        final int joined = count;

        source.sendFeedback(
                () -> Text.literal(
                        "Added " + joined + " entities to match '" + matchName + "'."
                ),
                false
        );

        return count;
    }

    private static int leave(
            ServerCommandSource source,
            Collection<? extends Entity> targets
    ) {
        if (targets == null) {
            Entity self = source.getEntity();

            if (self == null) {
                source.sendError(Text.literal(
                        "Specify targets when executing from the console."
                ));
                return 0;
            }

            targets = List.of(self);
        }

        int count = 0;

        for (Entity entity : targets) {
            EggwarsMatch match = Eggwars.getMatch(entity);

            if (match == null) {
                continue;
            }

            match.removeEntity(entity);
            count++;
        }

        final int left = count;

        source.sendFeedback(
                () -> Text.literal(
                        "Removed " + left + " entities from their matches."
                ),
                false
        );

        return count;
    }

    private static CompletableFuture<Suggestions> suggestMatchNames(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder
    ) {
        Eggwars.getMatches().values().stream()
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase()
                        .startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
