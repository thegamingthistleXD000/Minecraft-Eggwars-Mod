package org.eggwars_game.eggwars_mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlZone;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.SpecialSpot;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.ChorusWarp.ChorusWarp;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect.CrystalConnect;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.EnderSpirit.EnderSpirit;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.ForceField.ForceField;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.ModifierManager;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.PearlBarrier.PearlBarrier;


import java.util.*;

public class EggwarsMatch {

    private final String name;

    public String getName (){
        return name;
    }
    ArrayList<ControlCrystal> crystals = new ArrayList<>();
    ArrayList<EggwarsPlayer> players = new  ArrayList<>();
    Map<BlockPos, Set<SpecialSpot>> spots = new HashMap<>();
    ArrayList<EggwarsTeam> teams = new ArrayList<>();
    Map<UUID, EggwarsEntity> entities = new HashMap<>();

    EggwarsTeam defaultTeam;

    boolean active = true;

    private final Box bounds;

    public EggwarsMatch(String name, BlockPos corner1, BlockPos corner2) {
        this.name = name;
        this.bounds = new Box(corner1.toCenterPos(), corner2.toCenterPos()).expand(1);
        defaultTeam = this.createTeam(Teams.WHITE);
    }

    public void tick(){
        modManager.tick();
    }
    public boolean contains(BlockPos pos) {
        return bounds.contains(Vec3d.ofCenter(pos));
    }

    public Box getBounds() {
        return bounds;
    }

    public EggwarsPlayer getEggwarsPlayer(ServerPlayerEntity player) {
        return players.stream()
                .filter(p -> p.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    public void addPlayer(EggwarsPlayer player) {
        if (players.contains(player)) return;

        players.add(player);
    }

    public EggwarsPlayer createPlayer(ServerPlayerEntity player, EggwarsTeam team) {
        EggwarsPlayer eggwarsPlayer = new EggwarsPlayer(player, this, team);
        addPlayer(eggwarsPlayer);
        return eggwarsPlayer;
    }

    public EggwarsPlayer createPlayer(ServerPlayerEntity player) {
        EggwarsPlayer eggwarsPlayer = new EggwarsPlayer(player, this, defaultTeam);
        addPlayer(eggwarsPlayer);
        return eggwarsPlayer;
    }

    public boolean removePlayer(EggwarsPlayer player) {
        return players.remove(player) ;


    }


    public EggwarsTeam createTeam(Teams teamType) {
        EggwarsTeam team = new EggwarsTeam(this, teamType);
        addTeam(team);
        return team;
    }

    public EggwarsTeam createTeam(String name, Formatting color) {
        EggwarsTeam team = new EggwarsTeam(this, Text.of(name), color);
        addTeam(team);
        return team;
    }

    public void addTeam(EggwarsTeam team) {teams.add(team);}
    public void removeTeam(EggwarsTeam team) {teams.remove(team);}




    public final ModifierManager<Modifier> modManager = new ModifierManager<>();
    public final ModifierManager<ChorusWarp> warpManager = new ModifierManager<>();
    public final ModifierManager<ForceField> fieldManager = new ModifierManager<>();
    public final ModifierManager<CrystalConnect> connectManager = new ModifierManager<>();
    public final ModifierManager<PearlBarrier> barrierManager = new ModifierManager<>();
    public final ModifierManager<EnderSpirit> spiritManager = new ModifierManager<>();

    public ChorusWarp createChorusWarp(BlockPos pos, EggwarsPlayer player) {
        ServerPlayerEntity serverPlayer = player.getPlayer();
        serverPlayer.sendMessage(Text.literal("ChorusWarp created"), true);

        ChorusWarp modifier = new ChorusWarp(pos, player);
        modManager.addModifier(() -> modifier, pos);
        warpManager.addModifier(() -> modifier, pos);
        System.out.println(warpManager.getModifiers());
        return modifier;
    }


    public ForceField createForceField(BlockPos pos, EggwarsPlayer player, Direction facing) {
        ServerPlayerEntity serverPlayer = player.getPlayer();
        serverPlayer.sendMessage(Text.literal("ForceField created"), true);
        ForceField modifier = new ForceField(pos, player, facing);
        modManager.addModifier(() -> modifier, pos);
        fieldManager.addModifier(() -> modifier, pos);
        return modifier;
    }

    public CrystalConnect createCrystalConnect(
            EggwarsPlayer player,
            ControlCrystal ccA, ControlCrystal ccB
    ) {
        ServerPlayerEntity serverPlayer = player.getPlayer();
        serverPlayer.sendMessage(Text.literal("CrystalConnect created"), true);
        CrystalConnect modifier = new CrystalConnect(player, ccA, ccB);
        modManager.addModifier(() -> modifier, ccB.getPosition());
        connectManager.addModifier(() -> modifier, ccB.getPosition());
        return modifier;
    }

    public PearlBarrier createPearlBarrier(
            ControlCrystal crystal, EggwarsPlayer player
    ) {
        ServerPlayerEntity serverPlayer = player.getPlayer();
        serverPlayer.sendMessage(Text.literal("PearlBarrier created"), true);
        PearlBarrier modifier = new PearlBarrier(crystal, player);
        modManager.addModifier(() -> modifier, crystal.getPosition());
        barrierManager.addModifier(() -> modifier, crystal.getPosition());
        return modifier;
    }

    public EnderSpirit createEnderSpirit(
            ControlCrystal crystal, EggwarsPlayer player
    ) {
        ServerPlayerEntity serverPlayer = player.getPlayer();
        serverPlayer.sendMessage(Text.literal("EnderSpirit created"), true);
        EnderSpirit modifier = new EnderSpirit(crystal, player);
        modManager.addModifier(() -> modifier, crystal.getPosition());
        spiritManager.addModifier(() -> modifier, crystal.getPosition());
        return modifier;
    }

    public void removeChorusWarp(BlockPos pos) {

        System.out.println(warpManager.getModifiers());
        for (ChorusWarp modifier : warpManager.getModifiers()) {
            if (pos.equals(warpManager.getPosition(modifier))) {
                warpManager.removeModifier(modifier);
                modManager.removeModifier(modifier);
                System.out.println("removed warp at: " + pos);
                return;
            }
        }
    }

    public void removeForceField(BlockPos pos) {
        for (ForceField modifier : fieldManager.getModifiers()) {
            if (pos.equals(fieldManager.getPosition(modifier))) {
                fieldManager.removeModifier(modifier);
                modManager.removeModifier(modifier);
                return;
            }
        }
    }

    public void removeCrystalConnect(BlockPos pos) {
        for (CrystalConnect modifier : connectManager.getModifiers()) {
            if (pos.equals(connectManager.getPosition(modifier))) {
                connectManager.removeModifier(modifier);
                modManager.removeModifier(modifier);
                return;
            }
        }
    }

    public void removePearlBarrier(BlockPos pos) {
        for (PearlBarrier modifier : barrierManager.getModifiers()) {
            if (pos.equals(barrierManager.getPosition(modifier))) {
                barrierManager.removeModifier(modifier);
                modManager.removeModifier(modifier);
                return;
            }
        }
    }

    public void removeEnderSpirit(EnderSpirit modifier) {
        spiritManager.removeModifier(modifier);
        modManager.removeModifier(modifier);
    }

    public EggwarsEntity addEntity(Entity entity, EggwarsTeam team) {
        EggwarsEntity eggwarsEntity;

        if (entity instanceof ServerPlayerEntity player) {
            eggwarsEntity = new EggwarsPlayer(player, this, team);
            
        } else {
            eggwarsEntity = new EggwarsEntity(entity, this, team);
        }

        entities.put(entity.getUuid(), eggwarsEntity);

        if (eggwarsEntity instanceof EggwarsPlayer player) {
            addPlayer(player);
        }

        entity.sendMessage(Text.literal("Added you to match"));

        return eggwarsEntity;
    }

    public static void addSpawnedEntity(Entity entity, Entity source) {
        EggwarsMatch match = Eggwars.getMatch(source);

        if (match == null) {
            return;
        }

        EggwarsEntity eggwarsSource = match.getEggwarsEntity(source);

        if (eggwarsSource == null) {
            return;
        }

        match.addEntity(entity, eggwarsSource.getTeam());
        source.sendMessage(Text.literal("Eggwars entity spawned!"));

    }


    public boolean removeEntity(EggwarsEntity entity) {
        for (UUID id : entities.keySet()){
            if (id.equals(entity.getEntity().getUuid())){
                 entities.remove(id);
                 return true;
            }
        }
        return false;
    }


    public void removeEntity(Entity entity) {
        entities.remove(entity.getUuid());
    }

    public boolean containsEntity(EggwarsEntity entity) {
        return entities.containsValue(entity);
    }

    public EggwarsEntity getEggwarsEntity(Entity entity) {
        return entities.values().stream()
                    .filter(p -> p.getEntity().equals(entity))
                    .findFirst()
                    .orElse(null);
    }




    public  void addControlCrystal(ControlCrystal crystal) {
        crystals.add(crystal);}

    public  ControlCrystal addControlCrystal (EndCrystalEntity endcrys, EggwarsTeam team){

        System.out.println("adding control crystal");
        ControlCrystal cc = new ControlCrystal(endcrys, this, team);
        addEntity(endcrys, team);
        addControlCrystal(cc);
        makeControlZone(ControlZone.zonetype.cone, team, 10, 20, cc.getPosition().subtract(new Vec3i(0, 20, 0)));

        return cc;


    }

    public  void removeControlCrystal(ControlCrystal crystal) {
        System.out.println("removed control crystal");
        crystals.remove(crystal);}



    public ControlCrystal getControlCrystal(EndCrystalEntity crystal) {
        EggwarsEntity entity = entities.get(crystal.getUuid());
        return entity instanceof ControlCrystal control
                ? control
                : null;
    }


    public  Map<ControlCrystal, Set<ControlCrystal>> linkedcrystals = new HashMap<ControlCrystal, Set<ControlCrystal>>();

    public  ArrayList<ControlZone> zones = new ArrayList<ControlZone>();

    public  void addCrystalLink(ControlCrystal from, ControlCrystal to) {
        linkedcrystals.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }
    public  void removeCrystalLink(ControlCrystal from, ControlCrystal to) {
        Set<ControlCrystal> links = linkedcrystals.get(from);
        if (links == null) return;

        links.remove(to);
        if (links.isEmpty()) linkedcrystals.remove(from);
    }
    public  Set<ControlCrystal> getCrystalLinks(ControlCrystal crystal) {
        return linkedcrystals.getOrDefault(crystal, Collections.emptySet());
    }
    public  boolean hasCrystalLink(ControlCrystal from, ControlCrystal to) {
        Set<ControlCrystal> links = linkedcrystals.get(from);
        return links != null && links.contains(to);
    }
    public  void removeCrystalLinks(ControlCrystal crystal) {
        linkedcrystals.remove(crystal);

        for (Set<ControlCrystal> links : linkedcrystals.values())
            links.remove(crystal);
    }
    public  Set<ControlCrystal> getIncomingCrystalLinks(ControlCrystal crystal) {
        Set<ControlCrystal> incoming = new HashSet<>();

        for (Map.Entry<ControlCrystal, Set<ControlCrystal>> entry : linkedcrystals.entrySet())
            if (entry.getValue().contains(crystal))
                incoming.add(entry.getKey());

        return incoming;
    }

    public  ControlZone makeControlZone(ControlZone.zonetype t, EggwarsTeam team) {
        
        ControlZone zone = new ControlZone(t, team);
        zones.add(zone);
        return zone;
    }
    public  ControlZone makeControlZone(ControlZone.zonetype t, EggwarsTeam team, float r, float h, BlockPos o) {

        ControlZone zone = new ControlZone(t, team, r, h, o);
        zones.add(zone);
        return zone;
    }
    public  ControlZone makeControlZone(ControlZone.zonetype t, EggwarsTeam team, float r, BlockPos o) {
        ControlZone zone = new ControlZone(t, team, r, o);
        zones.add(zone);
        return zone;
    }
    public  ControlZone makeControlZone(ControlZone.zonetype t, EggwarsTeam team, BlockPos c1, BlockPos c2) {

        ControlZone zone = new ControlZone(t, team, c1, c2);
        zones.add(zone);
        return zone;
    }
    public  void removeControlZone(ControlZone zone) {
        zones.remove(zone);

    }

    public  List<ControlZone> getCrystalLoops(ControlCrystal from, ControlCrystal to) {
        List<ControlZone> zones = new ArrayList<>();
        List<ControlCrystal> path = new ArrayList<>();
        Set<ControlCrystal> visited = new HashSet<>();

        path.add(from);
        visited.add(from);

        findCrystalLoops(to, from, path, visited, zones);

        return zones;
    }
    private  void findCrystalLoops(
            ControlCrystal current,
            ControlCrystal target,
            List<ControlCrystal> path,
            Set<ControlCrystal> visited,
            List<ControlZone> zones
    ) {
        if (current == target) {
            ArrayList<BlockPos> vertices = new ArrayList<>();

            for (ControlCrystal crystal : path)
                vertices.add(crystal.getPosition());

            ControlZone zone = new ControlZone(ControlZone.zonetype.cylinder, current.getTeam());
            zone.cylvertices = vertices;
            zone.flattenYvertices();

            zones.add(zone);
            return;
        }

        for (ControlCrystal next : getCrystalLinks(current)) {
            if (visited.contains(next)) continue;

            visited.add(next);
            path.add(next);

            findCrystalLoops(next, target, path, visited, zones);

            path.removeLast();
            visited.remove(next);
        }
    }

    public   ArrayList<ControlZone> getControlZones() {return zones;}




    public  void addSpot (BlockPos pos, SpecialSpot spotType){
        spots.computeIfAbsent(pos, p -> new HashSet<>()).add(spotType);
    }
    public  void removeSpot(BlockPos pos, SpecialSpot spot) {
        Set<SpecialSpot> set = spots.get(pos);

        if (set == null) {return;}
        set.remove(spot);

        if (set.isEmpty()) {
            spots.remove(pos);
        }
    }
    public  Set<BlockPos> getSpotPositions(SpecialSpot spot) {

        Set<BlockPos> positions = new HashSet<>();

        for (Map.Entry<BlockPos, Set<SpecialSpot>> entry : spots.entrySet()) {

            if (entry.getValue().contains(spot)) {
                positions.add(entry.getKey());
            }
        }

        return positions;
    }
    public  Set<SpecialSpot> getSpots(BlockPos pos) {
        return spots.getOrDefault(pos, Collections.emptySet());
    }



    public  ControlCrystal nearestCrystal (BlockPos pos, Team team){
        ControlCrystal c = null;
        double dist = 0;
        for (ControlCrystal cc : crystals){
            if (!cc.getTeam().equals(team)) {continue;}
            double new_dist = pos.getSquaredDistance(cc.getEndCrystal().getBlockPos());
            if (new_dist >= dist){continue;}
            c = cc;
            dist = new_dist;
        }

        return c;
    }


    public  boolean isInTeamControlZone(BlockPos pos, EggwarsTeam team) {
        if (team == null) return false;
        for (ControlZone zone : zones) {
            if (!zone.getTeam().equals(team)) continue;
            ControlZone.zonetype type = zone.getType();
            if (type == ControlZone.zonetype.cylinder)
                if (zone.containsBlockCyl(pos))
                    return true;

            if (type == ControlZone.zonetype.cone)
                if  (zone.containsBlockCone(pos))
                    return true;
        }

        return false;
    }

    public void joinTeam(EggwarsEntity entity, EggwarsTeam team) {
        if (!containsEntity(entity)) return;
        EggwarsTeam prevTeam = entity.getTeam();
        if (prevTeam != null)  {
            prevTeam.removeMember(entity);
            if (prevTeam == team) return;
        }

        entity.setTeam(team);
    }

    public void leaveTeam(EggwarsEntity entity) {
        if (!containsEntity(entity)) return;
        entity.getTeam().removeMember(entity);
        entity.setTeam(null);
    }

    public void defaultTeam(EggwarsEntity entity) {
        if (!containsEntity(entity)) return;
        entity.setTeam(defaultTeam);
    }

    public ArrayList<EggwarsTeam> getTeams() {
        return teams;
    }

    public ArrayList<ControlCrystal> getCrystals() {
        return crystals;
    }
}
