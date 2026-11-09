package org.eggwars_game.eggwars.client;

import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import org.eggwars_game.eggwars.client.Renderers.CrystalRayRenderer;
import org.eggwars_game.eggwars.client.network.ClientNetworkRegistry;

import java.util.UUID;

public class EggwarsClient implements ClientModInitializer {





    @Getter static ClientEggwarsPlayer player;
    @Getter static ClientEggwarsMatch match;

    @Override
    public void onInitializeClient() {
        ClientNetworkRegistry.register();
        CrystalRayRenderer.register();

    }

    public static void setPlayer (){
        player = new ClientEggwarsPlayer(match);
    }

    public static ClientEggwarsMatch setMatch(String name, UUID id) {
        match = new  ClientEggwarsMatch(name, id);
        return match;
    }



    public static void changeTeam(ClientEggwarsTeam newTeam){
        player.setTeam(newTeam);
    }



    public static void deletePlayer(){
        player = null;
        match = null;
    }

}
