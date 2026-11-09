package org.eggwars_game.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.eggwars_game.network.S2CPayloads.AddCrystalRayPayload;
import org.eggwars_game.network.S2CPayloads.MainState.*;
import org.eggwars_game.network.S2CPayloads.RemoveCrystalRayPayload;

public final class NetworkRegistry {

    private NetworkRegistry() {}

    public static void registerC2S() {
        // Register client-to-server payloads here.
    }


    public static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(
                AddMatchPayload.ID,
                AddMatchPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                RemoveFromMatchPayload.ID,
                RemoveFromMatchPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                AddTeamPayload.ID,
                AddTeamPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                RemoveTeamPayload.ID,
                RemoveTeamPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                SetPlayerTeamPayload.ID,
                SetPlayerTeamPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                AddCrystalRayPayload.ID,
                AddCrystalRayPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                RemoveCrystalRayPayload.ID,
                RemoveCrystalRayPayload.CODEC
        );
    }

    public static void register() {
        registerC2S();
        registerS2C();
    }
}
