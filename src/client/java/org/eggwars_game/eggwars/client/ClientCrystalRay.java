package org.eggwars_game.eggwars.client;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record ClientCrystalRay(
        UUID connectionId,
        BlockPos from,
        BlockPos to,
        int color
) {}