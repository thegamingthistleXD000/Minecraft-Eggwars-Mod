package org.eggwars_game.eggwars.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.eggwars_game.network.S2CPayloads.AddCrystalRayPayload;
import org.eggwars_game.network.S2CPayloads.MainState.*;
import org.eggwars_game.network.S2CPayloads.RemoveCrystalRayPayload;

public class ClientNetworkReceiver {
    
    private ClientNetworkReceiver() {}

    public static void register() {

        ClientPlayNetworking.registerGlobalReceiver(
                AddMatchPayload.ID,
                (payload, context) -> {
                    context.client().execute(() ->
                            ClientNetworkHandler.handleAddMatch(payload)
                    );
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                RemoveFromMatchPayload.ID,
                (payload, context) ->
                        context.client().execute(
                                ClientNetworkHandler::handleRemoveFromMatch
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                AddTeamPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                ClientNetworkHandler.handleAddTeam(payload)
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                RemoveTeamPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                ClientNetworkHandler.handleRemoveTeam(payload)
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                SetPlayerTeamPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                ClientNetworkHandler.handleSetPlayerTeam(payload)
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                AddCrystalRayPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                ClientNetworkHandler.handleAddCrystalRay(payload)
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                RemoveCrystalRayPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                ClientNetworkHandler.handleRemoveCrystalRay(payload)
                        )
        );
    }
}