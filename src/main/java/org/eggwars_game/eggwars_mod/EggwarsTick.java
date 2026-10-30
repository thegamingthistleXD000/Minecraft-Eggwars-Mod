package org.eggwars_game.eggwars_mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class EggwarsTick {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (EggwarsMatch match : Eggwars.getMatches().keySet()) {
                match.tick();
            }
        });
    }
}