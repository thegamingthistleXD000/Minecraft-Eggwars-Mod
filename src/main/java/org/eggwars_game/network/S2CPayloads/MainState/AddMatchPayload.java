package org.eggwars_game.network.S2CPayloads.MainState;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;


import java.util.UUID;

public record AddMatchPayload(UUID matchId, String name)
        implements CustomPayload {

    public static final Id<AddMatchPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "add_match"));

    public static final PacketCodec<RegistryByteBuf, AddMatchPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING,
                    payload -> payload.matchId().toString(),
                    PacketCodecs.STRING,
                    AddMatchPayload::name,
                    (id, name) -> new AddMatchPayload(UUID.fromString(id), name)
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}