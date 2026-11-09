package org.eggwars_game.network.S2CPayloads.MainState;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RemoveFromMatchPayload() implements CustomPayload {

    public static final Id<RemoveFromMatchPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "remove_from_match"));

    public static final PacketCodec<RegistryByteBuf, RemoveFromMatchPayload> CODEC =
            PacketCodec.unit(new RemoveFromMatchPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}