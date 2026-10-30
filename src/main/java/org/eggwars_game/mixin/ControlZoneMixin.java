package org.eggwars_game.mixin;

import net.minecraft.entity.Entity;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlZone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;


@Mixin(Entity.class)
public class ControlZoneMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void eggwars$removeControlZones(
            Entity.RemovalReason reason,
            CallbackInfo ci
    ) {
        if (!reason.shouldDestroy())
            return;

        Entity entity = (Entity) (Object) this;

        EggwarsMatch match = Eggwars.getMatch(entity);
        if (match == null) return;
        EggwarsEntity eggwarsEntity = match.getEggwarsEntity(entity);



        for (ControlZone zone : new ArrayList<>(match.getControlZones())) {
            if (zone.hasSource(eggwarsEntity))
                match.removeControlZone(zone);
        }
    }
}

