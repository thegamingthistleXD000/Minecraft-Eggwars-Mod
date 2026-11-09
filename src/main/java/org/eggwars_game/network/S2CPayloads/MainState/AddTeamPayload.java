package org.eggwars_game.network.S2CPayloads.MainState;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;



public record AddTeamPayload(
        UUID id,
        String name,
        String colour
) implements CustomPayload {

    public static final Id<AddTeamPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "add_team"));

    public static final PacketCodec<RegistryByteBuf, AddTeamPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING,
                    payload -> payload.id().toString(),
                    PacketCodecs.STRING,
                    AddTeamPayload::name,
                    PacketCodecs.STRING,
                    AddTeamPayload::colour,
                    (id, name, colour) ->
                            new AddTeamPayload(UUID.fromString(id), name, colour)
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}