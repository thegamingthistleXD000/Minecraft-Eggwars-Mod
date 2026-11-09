package org.eggwars_game.eggwars.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ClientEggwarsEntity {




    @Getter protected final ClientEggwarsMatch match;
    @Getter @Setter protected ClientEggwarsTeam team;


    public ClientEggwarsEntity(ClientEggwarsMatch match) {
        this.match = match;
    }

    ArrayList<ClientEggwarsEntity> TeamMates  = new ArrayList<ClientEggwarsEntity>();


}
