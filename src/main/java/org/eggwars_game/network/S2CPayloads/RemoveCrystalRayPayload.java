package org.eggwars_game.network.S2CPayloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record RemoveCrystalRayPayload(UUID connectionId)
        implements CustomPayload {

    public static final Id<RemoveCrystalRayPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "remove_crystal_ray"));

    public static final PacketCodec<RegistryByteBuf, RemoveCrystalRayPayload> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    RemoveCrystalRayPayload::connectionId,
                    RemoveCrystalRayPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}