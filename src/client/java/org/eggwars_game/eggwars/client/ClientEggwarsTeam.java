package org.eggwars_game.eggwars.client;

import lombok.Getter;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class ClientEggwarsTeam {

    final ClientEggwarsMatch match;
    Formatting colour;
    String name;
    @Getter final UUID id;


    public ClientEggwarsTeam(ClientEggwarsMatch match, Formatting colour, String name, UUID uuid) {
        this.match = match;
        this.colour = colour;
        this.name = name;
        this.id = uuid;
    }


}
