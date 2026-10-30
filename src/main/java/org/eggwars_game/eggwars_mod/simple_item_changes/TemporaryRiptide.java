package org.eggwars_game.eggwars_mod.simple_item_changes;

import net.minecraft.item.TridentItem;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TemporaryRiptide {
    private static final Map<UUID, Long> EXPIRY = new HashMap<>();

    public static void activate(ServerPlayerEntity player) {
        EXPIRY.put(player.getUuid(),
                player.getServerWorld().getTime() + 5000);
    }


    public static boolean isActive(ServerPlayerEntity player) {
        Long expiry = EXPIRY.get(player.getUuid());

        if (expiry == null)
            return false;

        if (player.getServerWorld().getTime() >= expiry) {
            EXPIRY.remove(player.getUuid());
            return false;
        }

        return true;
    }

    public static void consume(ServerPlayerEntity player) {
        EXPIRY.remove(player.getUuid());
    }
}