package org.eggwars_game.eggwars_mod;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EggwarsTeam {
    private final EggwarsMatch match;
    private final UUID id =  UUID.randomUUID();
    private final Text displayName;
    private final Formatting color;

    private final Set<EggwarsEntity> members = new HashSet<>();

    public EggwarsTeam(
            EggwarsMatch match,
            Text displayName,
            Formatting color
    ) {
        this.match = match;
        this.displayName = displayName;
        this.color = color;
    }

    public EggwarsTeam(
            EggwarsMatch match,
            Teams team
    ) {
        this.match = match;
        this.displayName = Text.of(team.name());
        this.color = switch (team) {
            case Teams.WHITE -> Formatting.WHITE;
            case Teams.RED -> Formatting.RED;
            case Teams.BLUE -> Formatting.BLUE;
            case Teams.GREEN ->  Formatting.GREEN;
            case Teams.YELLOW -> Formatting.YELLOW;
            default -> Formatting.WHITE;
        };

    }

    public EggwarsMatch getMatch() {
        return match;
    }

    public UUID getId() {
        return id;
    }

    public Set<EggwarsEntity> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean contains(EggwarsEntity entity) {
        return members.contains(entity);

    }

    public boolean addMember(EggwarsEntity entity) {
        if (entity.getMatch() != getMatch()){
            return false;
        }
        return  members.add(entity);
    }

    public boolean removeMember(EggwarsEntity entity) {
        return members.remove(entity);
    }

    public String getName() {
        return displayName.getString();
    }

}
