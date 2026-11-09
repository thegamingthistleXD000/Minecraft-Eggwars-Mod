package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect;


import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.Utils.RayCast;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;


public class CrystalConnect extends Modifier {

    @Override
    public void register(EggwarsMatch match) {
        super.register(match);
        match.connectManager.addModifier(() -> this, getPosition());
    }

    private boolean pathisclear = true;
    public CrystalConnect(EggwarsPlayer player, ControlCrystal ccA, ControlCrystal ccB) {
        super(ccB.getPosition(), modifiers.mods.CrystalConnect, player);
        linkedCrystalreciever = ccA;
        linkedCrystalsender= ccB;
        super.controlCrystal =  ccB;

    }


    @Override
    public void tick() {
        checkObstruction(world);

    }

    public enum Mode {Fast_Travel, Control_Zone}
    @Setter @Getter private Mode mode;

    private final ControlCrystal linkedCrystalsender;
    private final ControlCrystal linkedCrystalreciever;

    public boolean checkObstruction(World world) {
        return pathisclear = RayCast.getRayObstructions(
                world,
                linkedCrystalreciever.getPosition(),
                linkedCrystalsender.getPosition(),
                1
        ).isEmpty();
    }


    public ControlCrystal getFrom(){
        return linkedCrystalsender;
    }

    public ControlCrystal getTo(){
        return linkedCrystalreciever;
    }


    public boolean isInCrystalRay(
            EggwarsPlayer player
    ) {

        PlayerEntity serverplayer = player.getPlayer();

        Vec3d start = linkedCrystalsender.getPosition().toCenterPos();
        Vec3d end = linkedCrystalreciever.getPosition().toCenterPos();

        Vec3d ray = end.subtract(start);
        double length = ray.length();

        if (length == 0) return false;

        Vec3d direction = ray.normalize();

        // Player collision box, truncated at neck height.
        Box box = serverplayer.getBoundingBox();

        double neckHeight = box.minY + serverplayer.getHeight() * 0.85;

        box = new Box(
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                neckHeight,
                box.maxZ
        );

        // Find the closest point on the ray segment to the player's box.
        Vec3d closest = box.getCenter();

        for (int i = 0; i < 5; i++) {
            double distance = closest.subtract(start).dotProduct(direction);
            distance = MathHelper.clamp(distance, 0, length);

            closest = start.add(direction.multiply(distance));

            closest = new Vec3d(
                    MathHelper.clamp(closest.x, box.minX, box.maxX),
                    MathHelper.clamp(closest.y, box.minY, box.maxY),
                    MathHelper.clamp(closest.z, box.minZ, box.maxZ)
            );
        }

        return closest.squaredDistanceTo(
                start.add(direction.multiply(
                        MathHelper.clamp(
                                closest.subtract(start).dotProduct(direction),
                                0,
                                length
                        )
                ))
        ) <= 1;
    }


}
