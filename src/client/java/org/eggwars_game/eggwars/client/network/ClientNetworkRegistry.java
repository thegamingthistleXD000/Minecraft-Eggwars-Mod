package org.eggwars_game.eggwars.client.network;

public class ClientNetworkRegistry {

    private ClientNetworkRegistry() {}

    public static void register() {
        ClientNetworkReceiver.register();
    }
}