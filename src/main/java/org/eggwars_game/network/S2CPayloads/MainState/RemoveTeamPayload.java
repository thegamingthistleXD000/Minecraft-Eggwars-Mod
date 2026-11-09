package org.eggwars_game.network.S2CPayloads.MainState;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record RemoveTeamPayload(UUID id) implements CustomPayload {

    public static final Id<RemoveTeamPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "remove_team"));

    public static final PacketCodec<RegistryByteBuf, RemoveTeamPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING,
                    payload -> payload.id().toString(),
                    (id) -> new RemoveTeamPayload(UUID.fromString(id))
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}