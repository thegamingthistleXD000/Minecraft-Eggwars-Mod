package org.eggwars_game.network.S2CPayloads.MainState;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record SetPlayerTeamPayload(
        UUID playerId,
        UUID teamId
) implements CustomPayload {

    public static final Id<SetPlayerTeamPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "set_player_team"));

    public static final PacketCodec<RegistryByteBuf, SetPlayerTeamPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING,
                    payload -> payload.playerId().toString(),
                    PacketCodecs.STRING,
                    payload -> payload.teamId().toString(),
                    (playerId, teamId) -> new SetPlayerTeamPayload(
                            UUID.fromString(playerId),
                            UUID.fromString(teamId)
                    )
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}