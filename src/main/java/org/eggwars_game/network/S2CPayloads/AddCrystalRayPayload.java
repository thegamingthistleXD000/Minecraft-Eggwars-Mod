package org.eggwars_game.network.S2CPayloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record AddCrystalRayPayload(

        UUID connectionId,
        BlockPos from,
        BlockPos to,
        int color
) implements CustomPayload {

    public static final Id<AddCrystalRayPayload> ID =
            new Id<>(Identifier.of("eggwars_mod", "add_crystal_ray"));

    public static final PacketCodec<RegistryByteBuf, AddCrystalRayPayload> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    AddCrystalRayPayload::connectionId,
                    BlockPos.PACKET_CODEC,
                    AddCrystalRayPayload::from,
                    BlockPos.PACKET_CODEC,
                    AddCrystalRayPayload::to,
                    PacketCodecs.INTEGER,
                    AddCrystalRayPayload::color,
                    AddCrystalRayPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}