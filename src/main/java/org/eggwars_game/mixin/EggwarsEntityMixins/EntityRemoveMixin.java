package org.eggwars_game.mixin.EggwarsEntityMixins;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityRemoveMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void eggwars$removeTrackedEntity(
            Entity.RemovalReason reason, CallbackInfo ci
    ) {
        Entity entity = (Entity) (Object) this;

        if (!(entity.getWorld() instanceof ServerWorld)) return;

        EggwarsMatch match = Eggwars.getMatch(entity);
        if (match == null) return;

        match.removeEntity(entity);
    }
}