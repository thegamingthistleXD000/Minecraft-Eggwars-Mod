package org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals;


















import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsTeam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;









public class ControlZone {

    private EggwarsTeam team;
    public ArrayList<BlockPos> cylvertices;
    public BlockPos[] cornerverts =  new BlockPos[2];
    private Set<EggwarsEntity> sources = new HashSet<EggwarsEntity>();

    public Set<EggwarsEntity> getSources() {
        return sources;
    }

    public void setSources(Set<EggwarsEntity> sources) {
        this.sources = sources;
    }

    public enum zonetype {cylinder, cone, sphere, cuboid}
    zonetype type;

    public ControlZone (zonetype t, EggwarsTeam team) {
        this.type = t;
        this.team =  team;
    }

    public ControlZone (zonetype t, EggwarsTeam team, float r, float h, BlockPos o) {
        if (t != zonetype.cone) throw new IllegalArgumentException("ControlZone type must be cone");
        this.type = t;
        this.team =  team;
        this.radius = r;
        this.height = h;
        this.origin = o;
    }

    public ControlZone (zonetype t, EggwarsTeam team, float r, BlockPos o) {
        if (t != zonetype.sphere) throw new IllegalArgumentException("ControlZone type must be sphere");
        this.type = t;
        this.team =  team;
        this.radius = r;
        this.origin = o;
    }

    public ControlZone  (zonetype t, EggwarsTeam team, BlockPos c1, BlockPos c2) {
        if (t != zonetype.cuboid) throw new IllegalArgumentException("ControlZone type must be cuboid");
        this.type = t;
        this.team =  team;
        this.cornerverts[0] = c1;
        this.cornerverts[1] = c2;
        float length = Math.abs(c1.getX() - c2.getX());
        float breadth = Math.abs(c1.getZ() - c2.getZ());
        float height = Math.abs(c1.getY() - c2.getY());
        sides[0] = length; sides[1] = breadth; sides[2] =  height;

    }


    float radius;
    BlockPos origin;
    float height;
    float[] sides = new float [3];

    public EggwarsTeam getTeam (){
        return team;
    }

    public void flattenYvertices (){
        int highestY = cylvertices.stream()
                .mapToInt(BlockPos::getY)
                .max()
                .orElseThrow();

        for (int i = 0; i < cylvertices.size(); i++) {
            BlockPos pos = cylvertices.get(i);

            cylvertices.set(
                    i,
                    new BlockPos(pos.getX(), highestY, pos.getZ())
            );
        }
    }

    public boolean containsBlockCyl(BlockPos pos) {
        if (type != zonetype.cylinder) {return false;}
        if (!cylvertices.isEmpty()) {return false;}
        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;

        boolean inside = false;

        for (int i = 0, j = cylvertices.size() - 1;
             i < cylvertices.size();
             j = i++) {

            double xi = cylvertices.get(i).getX();
            double zi = cylvertices.get(i).getZ();

            double xj = cylvertices.get(j).getX();
            double zj = cylvertices.get(j).getZ();

            // Does the edge cross the horizontal ray from (x,z)?
            if ((zi > z) != (zj > z)) {

                double intersectionX =
                        xi + (z - zi)
                                * (xj - xi)
                                / (zj - zi);

                if (x < intersectionX) {
                    inside = !inside;
                }
            }
        }

        return inside;
    }

    public boolean containsBlockCone(BlockPos pos) {
        if  (type != zonetype.cone) {return false;}
        
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        double dy = y - (origin.getY() + 0.5);

        // Outside vertical range
        if (dy < 0 || dy > height) {
            return false;
        }

        // Radius at this height
        double radiusAtY = radius * ( 1 - dy / height);

        double dx = x - (origin.getX() + 0.5);
        double dz = z - (origin.getZ() + 0.5);

        double distanceSquared = dx * dx + dz * dz;

        return distanceSquared <= radiusAtY * radiusAtY;
    }

    public zonetype getType (){
        return type;
    }


    public void addSource(EggwarsEntity entity) {
        sources.add(entity);
    }

    public void removeSource(EggwarsEntity entity) {
        sources.remove(entity);
    }

    public boolean hasSource(EggwarsEntity entity) {
        return sources.contains(entity);
    }





}
