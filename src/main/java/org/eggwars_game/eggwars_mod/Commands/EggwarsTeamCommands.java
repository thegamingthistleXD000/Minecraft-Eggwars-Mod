
package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.EggwarsEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EggwarsTeamCommands {

    static Map<EggwarsEntity, EggwarsMatch> context_matches = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("eggwars")
                        .then(literal("team")

                                // /eggwars team create <name>
                                // /eggwars team create ctx <name>
                                .then(literal("create")
                                        .then(argument("name", StringArgumentType.word())
                                                .executes(ctx -> create(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        false
                                                ))
                                        )
                                        .then(literal("ctx")
                                                .then(argument("name", StringArgumentType.word())
                                                        .executes(ctx -> create(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "name"),
                                                                true
                                                        ))
                                                )
                                        )
                                )

                                // /eggwars team remove <team>
                                // /eggwars team remove ctx <team>
                                .then(literal("remove")
                                        .then(argument("team", StringArgumentType.word())
                                                .suggests(EggwarsTeamCommands::suggestTeamNames)
                                                .executes(ctx -> remove(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "team"),
                                                        false
                                                ))
                                        )
                                        .then(literal("ctx")
                                                .then(argument("team", StringArgumentType.word())
                                                        .suggests(EggwarsTeamCommands::suggestTeamNames)
                                                        .executes(ctx -> remove(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "team"),
                                                                true
                                                        ))
                                                )
                                        )
                                )

                                // /eggwars team join <team> [targets]
                                // /eggwars team join ctx <team> [targets]
                                // Defaults to self when targets are omitted.
                                .then(literal("join")
                                        .then(argument("team", StringArgumentType.word())
                                                .suggests(EggwarsTeamCommands::suggestTeamNames)
                                                .executes(ctx -> join(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "team"),
                                                        null,
                                                        false
                                                ))
                                                .then(argument("targets", EntityArgumentType.entities())
                                                        .executes(ctx -> join(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "team"),
                                                                EntityArgumentType.getEntities(ctx, "targets"),
                                                                false
                                                        ))
                                                )
                                        )
                                        .then(literal("ctx")
                                                .then(argument("team", StringArgumentType.word())
                                                        .suggests(EggwarsTeamCommands::suggestTeamNames)
                                                        .executes(ctx -> join(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "team"),
                                                                null,
                                                                true
                                                        ))
                                                        .then(argument("targets", EntityArgumentType.entities())
                                                                .executes(ctx -> join(
                                                                        ctx.getSource(),
                                                                        StringArgumentType.getString(ctx, "team"),
                                                                        EntityArgumentType.getEntities(ctx, "targets"),
                                                                        true
                                                                ))
                                                        )
                                                )
                                        )
                                )

                                // /eggwars team leave [targets]
                                // /eggwars team leave ctx [targets]
                                // Defaults to self when targets are omitted.
                                .then(literal("leave")
                                        .executes(ctx -> leave(
                                                ctx.getSource(), null, false
                                        ))
                                        .then(argument("targets", EntityArgumentType.entities())
                                                .executes(ctx -> leave(
                                                        ctx.getSource(),
                                                        EntityArgumentType.getEntities(ctx, "targets"),
                                                        false
                                                ))
                                        )
                                        .then(literal("ctx")
                                                .executes(ctx -> leave(
                                                        ctx.getSource(), null, true
                                                ))
                                                .then(argument("targets", EntityArgumentType.entities())
                                                        .executes(ctx -> leave(
                                                                ctx.getSource(),
                                                                EntityArgumentType.getEntities(ctx, "targets"),
                                                                true
                                                        ))
                                                )
                                        )
                                )

                                // /eggwars team list
                                // /eggwars team list ctx
                                .then(literal("list")
                                        .executes(ctx -> listTeams(
                                                ctx.getSource(), false
                                        ))
                                        .then(literal("ctx")
                                                .executes(ctx -> listTeams(
                                                        ctx.getSource(), true
                                                ))
                                        )
                                )

                                // /eggwars team <team> list [targets]
                                // /eggwars team <team> list ctx [targets]
                                // Defaults to self when targets are omitted.
                                .then(argument("team", StringArgumentType.word())
                                        .suggests(EggwarsTeamCommands::suggestTeamNames)
                                        .then(literal("list")
                                                .executes(ctx -> listMembers(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "team"),
                                                        null,
                                                        false
                                                ))
                                                .then(argument("targets", EntityArgumentType.entities())
                                                        .executes(ctx -> listMembers(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "team"),
                                                                EntityArgumentType.getEntities(ctx, "targets"),
                                                                false
                                                        ))
                                                )
                                                .then(literal("ctx")
                                                        .executes(ctx -> listMembers(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "team"),
                                                                null,
                                                                true
                                                        ))
                                                        .then(argument("targets", EntityArgumentType.entities())
                                                                .executes(ctx -> listMembers(
                                                                        ctx.getSource(),
                                                                        StringArgumentType.getString(ctx, "team"),
                                                                        EntityArgumentType.getEntities(ctx, "targets"),
                                                                        true
                                                                ))
                                                        )
                                                )
                                        )
                                )

                                // /eggwars team [target]
                                // Defaults to self when target is omitted.
                                .executes(ctx -> showEntityTeam(
                                        ctx.getSource(),
                                        ctx.getSource().getEntity()
                                ))
                                .then(argument("target", EntityArgumentType.entity())
                                        .executes(ctx -> showEntityTeam(
                                                ctx.getSource(),
                                                EntityArgumentType.getEntity(ctx, "target")
                                        ))
                                )
                        )
        );
    }

    // MATCH RESOLUTION

    private static EggwarsMatch getMatch(
            ServerCommandSource source,
            boolean useContext
    ) {
        Entity sourceEntity = source.getEntity();

        if (sourceEntity == null) {
            return null;
        }

        if (!useContext) {
            return Eggwars.getMatch(sourceEntity);
        }

        EggwarsMatch match = Eggwars.getMatch(sourceEntity);

        EggwarsEntity eggwarsEntity = match == null
                ? null
                : match.getEggwarsEntity(sourceEntity);

        return eggwarsEntity == null
                ? null
                : getMatchContext(eggwarsEntity);
    }

    static EggwarsMatch getMatchContext(EggwarsEntity eggwarsEntity) {
        return context_matches.get(eggwarsEntity);
    }

    private static EggwarsMatch requireMatch(
            ServerCommandSource source,
            boolean useContext
    ) {
        EggwarsMatch match = getMatch(source, useContext);

        if (match == null) {
            source.sendError(Text.literal(
                    useContext
                            ? "You have no selected context match."
                            : "You are not in a match."
            ));
        }

        return match;
    }

    private static EggwarsTeam findTeam(
            EggwarsMatch match,
            String name
    ) {
        return match.getTeams().stream()
                .filter(team -> team.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // CREATE

    private static int create(
            ServerCommandSource source,
            String name,
            boolean useContext
    ) {
        EggwarsMatch match = requireMatch(source, useContext);

        if (match == null) {
            return 0;
        }

        if (findTeam(match, name) != null) {
            source.sendError(Text.literal(
                    "Team '" + name + "' already exists in this match."
            ));
            return 0;
        }

        match.createTeam(
                name,
                Formatting.WHITE
        );

        source.sendFeedback(
                () -> Text.literal("Created team '" + name + "' in match '" + match.getName() + "'."),
                false
        );

        return 1;
    }

    // REMOVE

    private static int remove(
            ServerCommandSource source,
            String name,
            boolean useContext
    ) {
        EggwarsMatch match = requireMatch(source, useContext);

        if (match == null) {
            return 0;
        }

        EggwarsTeam team = findTeam(match, name);

        if (team == null) {
            source.sendError(Text.literal(
                    "Team '" + name + "' does not exist in this match."
            ));
            return 0;
        }

        match.removeTeam(team);

        source.sendFeedback(
                () -> Text.literal("Removed team '" + name + "' from match '" + match.getName() + "'."),
                false
        );

        return 1;
    }

    // JOIN

    private static int join(
            ServerCommandSource source,
            String teamName,
            Collection<? extends Entity> targets,
            boolean useContext
    ) {
        EggwarsMatch match = requireMatch(source, useContext);

        if (match == null) {
            return 0;
        }

        EggwarsTeam team = findTeam(match, teamName);

        if (team == null) {
            source.sendError(Text.literal(
                    "Team '" + teamName + "' does not exist in this match."
            ));
            return 0;
        }

        if (targets == null) {
            Entity self = source.getEntity();

            if (self == null) {
                source.sendError(Text.literal(
                        "Specify one or more entities."
                ));
                return 0;
            }

            targets = java.util.List.of(self);
        }

        int count = 0;

        for (Entity entity : targets) {
            EggwarsMatch actualMatch = Eggwars.getMatch(entity);

            if (actualMatch != match) {
                source.sendError(Text.literal(
                        entity.getName().getString()
                                + " is not in the selected match."
                ));
                continue;
            }

            EggwarsEntity wrapped = match.getEggwarsEntity(entity);

            if (wrapped == null) {
                continue;
            }

            match.joinTeam(wrapped, team);
            count++;
        }

        final int joined = count;

        source.sendFeedback(
                () -> Text.literal(
                        "Added " + joined + " entity/entities to team '"
                                + teamName + "'."
                ),
                false
        );

        return count;
    }

    // LEAVE

    private static int leave(
            ServerCommandSource source,
            Collection<? extends Entity> targets,
            boolean useContext
    ) {
        EggwarsMatch contextMatch = useContext
                ? requireMatch(source, true)
                : null;

        if (useContext && contextMatch == null) {
            return 0;
        }

        if (targets == null) {
            Entity self = source.getEntity();

            if (self == null) {
                source.sendError(Text.literal(
                        "Specify one or more entities."
                ));
                return 0;
            }

            targets = java.util.List.of(self);
        }

        int count = 0;

        for (Entity entity : targets) {
            EggwarsMatch actualMatch = Eggwars.getMatch(entity);

            if (actualMatch == null) {
                continue;
            }

            if (useContext && actualMatch != contextMatch) {
                source.sendError(Text.literal(
                        entity.getName().getString()
                                + " is not in the selected context match."
                ));
                continue;
            }

            EggwarsEntity wrapped = actualMatch.getEggwarsEntity(entity);

            if (wrapped == null) {
                continue;
            }

            actualMatch.leaveTeam(wrapped);
            count++;
        }

        final int left = count;

        source.sendFeedback(
                () -> Text.literal(
                        "Removed " + left + " entity/entities from their teams."
                ),
                false
        );

        return count;
    }

    // LIST TEAMS

    private static int listTeams(
            ServerCommandSource source,
            boolean useContext
    ) {
        EggwarsMatch match = requireMatch(source, useContext);

        if (match == null) {
            return 0;
        }

        if (match.getTeams().isEmpty()) {
            source.sendFeedback(
                    () -> Text.literal("This match has no teams."),
                    false
            );
            return 0;
        }

        source.sendFeedback(
                () -> Text.literal("Teams in this match:"),
                false
        );

        for (EggwarsTeam team : match.getTeams()) {
            source.sendFeedback(
                    () -> Text.literal("- " + team.getName()),
                    false
            );
        }

        return match.getTeams().size();
    }

    // LIST MEMBERS OF A TEAM, OPTIONALLY FILTERED BY SELECTOR

    private static int listMembers(
            ServerCommandSource source,
            String teamName,
            Collection<? extends Entity> targets,
            boolean useContext
    ) {
        EggwarsMatch match = requireMatch(source, useContext);

        if (match == null) {
            return 0;
        }

        EggwarsTeam team = findTeam(match, teamName);

        if (team == null) {
            source.sendError(Text.literal(
                    "Team '" + teamName + "' does not exist in this match."
            ));
            return 0;
        }

        int count = 0;

        for (EggwarsEntity member : team.getMembers()) {
            Entity entity = member.getEntity();

            if (targets != null && !targets.contains(entity)) {
                continue;
            }

            source.sendFeedback(
                    () -> Text.literal("- " + entity.getName().getString()),
                    false
            );

            count++;
        }

        if (count == 0) {
            source.sendFeedback(
                    () -> Text.literal("No matching team members."),
                    false
            );
        }

        return count;
    }

    public static void setMatchContext(EggwarsEntity wrapper, EggwarsMatch match) {
        context_matches.put(wrapper, match);
    }

    private static CompletableFuture<Suggestions> suggestTeamNames(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder
    ) {
        String remaining = builder.getRemainingLowerCase();

        // Replace getTeams() with your actual team collection getter.
        EggwarsMatch match = Eggwars.getMatch(context.getSource().getEntity());

        match.getTeams().stream()
                .map(EggwarsTeam::getName)
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(remaining))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }


    private static int showEntityTeam(
            ServerCommandSource source,
            Entity entity
    ) {
        if (entity == null) {
            source.sendError(Text.literal(
                    "This command requires an in-game entity."
            ));
            return 0;
        }

        EggwarsMatch match = Eggwars.getMatch(entity);

        if (match == null) {
            source.sendFeedback(
                    () -> Text.literal(entity.getName().getString()
                            + " is not in a match."),
                    false
            );
            return 1;
        }

        EggwarsEntity wrapper = match.getEggwarsEntity(entity);

        if (wrapper == null) {
            source.sendError(Text.literal(
                    "Could not find the Eggwars entity for "
                            + entity.getName().getString() + "."
            ));
            return 0;
        }

        // Replace getTeam() / getName() if your wrapper or team uses different getters.
        EggwarsTeam team = wrapper.getTeam();

        if (team == null) {
            source.sendFeedback(
                    () -> Text.literal(entity.getName().getString()
                            + " is in a match but has no team."),
                    false
            );
            return 1;
        }

        source.sendFeedback(
                () -> Text.literal(entity.getName().getString()
                        + " is on team '" + team.getName() + "'."),
                false
        );

        return 1;
    }
}
