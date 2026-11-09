package org.eggwars_game.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.network.S2CPayloads.AddCrystalRayPayload;
import org.eggwars_game.network.S2CPayloads.MainState.*;
import org.eggwars_game.network.S2CPayloads.RemoveCrystalRayPayload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ServerNetworkSender {

    private ServerNetworkSender() {}



    public static void sendAddToMatch(
            ServerPlayerEntity player,
            EggwarsMatch match
    ) {
        ServerPlayNetworking.send(player, new AddMatchPayload(match.getMatchID(), match.getName()));
        sendAllTeams(player, match);
    }

    public static void sendRemoveFromMatch(EggwarsPlayer eggwarsPlayer) {

        ServerPlayerEntity player = eggwarsPlayer.getServerPlayer();
        ServerPlayNetworking.send(
                player,
                new RemoveFromMatchPayload()
        );
    }

    public static void sendAllTeams(
            ServerPlayerEntity player,
            EggwarsMatch match
    ) {
        for (EggwarsTeam team : match.getTeams()) {
            ServerPlayNetworking.send(
                    player,
                    new AddTeamPayload(
                            team.getId(),
                            team.getDisplayName().getString(),
                            team.getColor().getName()
                    )
            );
        }
    }

    public static void sendAddTeam(
            EggwarsMatch match,
            EggwarsTeam team
    ) {
        AddTeamPayload payload = new AddTeamPayload(
                team.getId(),
                team.getDisplayName().getString(),
                team.getColor().getName()
        );

        for (EggwarsPlayer eggwarsPlayer : match.getPlayers()) {
            ServerPlayerEntity player = eggwarsPlayer.getServerPlayer();
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendRemoveTeam(
            EggwarsMatch match,
            EggwarsTeam team
    ) {
        RemoveTeamPayload payload = new RemoveTeamPayload(team.getId());

        for (EggwarsPlayer eggwarsPlayer : match.getPlayers()) {
            ServerPlayerEntity player = eggwarsPlayer.getServerPlayer();
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendPlayerTeam(
            Collection<ServerPlayerEntity> players,
            UUID playerId,
            UUID teamId
    ) {
        SetPlayerTeamPayload payload =
                new SetPlayerTeamPayload(playerId, teamId);

        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendAddCrystalRay(
            ArrayList<EggwarsPlayer> eggwarsPlayers,
            UUID connectionId,
            BlockPos from,
            BlockPos to,
            Formatting teamColor
    ) {
        int color = teamColor.getColorValue() != null
                ? teamColor.getColorValue()
                : 0xFFFFFF;

        AddCrystalRayPayload payload = new AddCrystalRayPayload(
                connectionId,
                from,
                to,
                color
        );

        for (EggwarsPlayer eggwarsPlayer : eggwarsPlayers) {
            ServerPlayerEntity player = eggwarsPlayer.getServerPlayer();
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendRemoveCrystalRay(
            Collection<ServerPlayerEntity> players,
            UUID connectionId
    ) {
        RemoveCrystalRayPayload payload =
                new RemoveCrystalRayPayload(connectionId);

        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}