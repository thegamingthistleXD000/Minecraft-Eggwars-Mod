package org.eggwars_game.eggwars.client;

import lombok.Getter;

import java.util.ArrayList;
import java.util.UUID;

public class ClientEggwarsMatch {
    @Getter private final String name;
    @Getter private final UUID id;


    @Getter private ArrayList<ClientEggwarsTeam> teams;

    public ClientEggwarsMatch(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

    public void addTeam(ClientEggwarsTeam team) {
        teams.add(team);
    }
    public void removeTeam(ClientEggwarsTeam team) {
        teams.remove(team);
    }
    public void removeTeam(UUID uuid) {
        teams.removeIf(team -> team.getId().equals(uuid));
    }

    @Getter
    private final ArrayList<ClientCrystalRay> crystalRays = new ArrayList<>();

    public void addCrystalRay(ClientCrystalRay ray) {
        crystalRays.add(ray);
    }

    public void removeCrystalRay(UUID uuid) {
        crystalRays.removeIf(ray -> ray.connectionId().equals(uuid));

    }
}
