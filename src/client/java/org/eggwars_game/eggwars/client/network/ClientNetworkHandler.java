package org.eggwars_game.eggwars.client.network;

import net.minecraft.util.Formatting;
import org.eggwars_game.eggwars.client.ClientEggwarsPlayer;
import org.eggwars_game.eggwars.client.ClientEggwarsTeam;
import org.eggwars_game.eggwars.client.EggwarsClient;
import org.eggwars_game.eggwars.client.ClientCrystalRay;
import org.eggwars_game.network.S2CPayloads.AddCrystalRayPayload;
import org.eggwars_game.network.S2CPayloads.MainState.AddMatchPayload;
import org.eggwars_game.network.S2CPayloads.MainState.AddTeamPayload;
import org.eggwars_game.network.S2CPayloads.MainState.RemoveTeamPayload;
import org.eggwars_game.network.S2CPayloads.MainState.SetPlayerTeamPayload;
import org.eggwars_game.network.S2CPayloads.RemoveCrystalRayPayload;

public class ClientNetworkHandler {

    private ClientNetworkHandler() {}

    public static void handleAddMatch(AddMatchPayload payload) {

        EggwarsClient.setMatch(
                payload.name(),
                payload.matchId()
        );

        System.out.println(
                "Received match: " + payload.name()
                        + " | " + payload.matchId()
        );
    }

    public static void handleRemoveFromMatch() {
        EggwarsClient.deletePlayer();

        System.out.println("Removed local player from match");
    }

    public static void handleAddTeam(AddTeamPayload payload) {


        if (EggwarsClient.getMatch() == null) return;

        Formatting colour = Formatting.byName(payload.colour());

        if (colour == null) {
            System.err.println("Invalid team colour: " + payload.colour());
            return;
        }

        ClientEggwarsTeam team = new ClientEggwarsTeam(
                EggwarsClient.getMatch(),
                colour,
                payload.name(),
                payload.id()
        );

        EggwarsClient.getMatch().addTeam(team);

        System.out.println("Received team: " + payload.name());
    }

    public static void handleRemoveTeam(RemoveTeamPayload payload) {


        if (EggwarsClient.getMatch() == null) return;

        EggwarsClient.getMatch().removeTeam(payload.id());

        System.out.println("Removed team: " + payload.id());
    }

    public static void handleSetPlayerTeam(SetPlayerTeamPayload payload) {


        if (EggwarsClient.getMatch() == null) return;

        ClientEggwarsTeam team = null;

        for (ClientEggwarsTeam clientTeam : EggwarsClient.getMatch().getTeams()) {
            if(clientTeam.getId() == payload.teamId()){
                team = clientTeam;
                break;
            }
        }

        if (team == null) {
            System.err.println("Unknown team: " + payload.teamId());
            return;
        }

        ClientEggwarsPlayer player = EggwarsClient.getPlayer();

        if (player != null) {
            player.setTeam(team);
        }
    }

    public static void handleAddCrystalRay(AddCrystalRayPayload payload) {

        if (EggwarsClient.getMatch() == null) return;

        EggwarsClient.getMatch().addCrystalRay(
                new ClientCrystalRay(
                        payload.connectionId(),
                        payload.from(),
                        payload.to(),
                        payload.color()
                )
        );
    }

    public static void handleRemoveCrystalRay(RemoveCrystalRayPayload payload) {

        if (EggwarsClient.getMatch() == null) return;

        EggwarsClient.getMatch().removeCrystalRay(payload.connectionId());
    }
}



