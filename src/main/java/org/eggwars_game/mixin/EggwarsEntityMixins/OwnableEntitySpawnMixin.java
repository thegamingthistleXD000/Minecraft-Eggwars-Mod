package org.eggwars_game.mixin.EggwarsEntityMixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class OwnableEntitySpawnMixin {

    @Inject(method = "spawnEntity", at = @At("RETURN"))
    private void eggwars$trackSpawnedEntity(
            Entity entity, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!(entity.getWorld() instanceof ServerWorld)) return;

        if (!(entity instanceof Ownable)) return;

        Entity owner = ((Ownable) entity).getOwner();
        if (!(owner instanceof ServerPlayerEntity player)) return;

        EggwarsMatch match = Eggwars.getMatch(player);
        if (match == null) return;

        EggwarsPlayer eggwarsPlayer = match.getEggwarsPlayer(player);
        EggwarsEntity eggwarsEntity = match.getEggwarsEntity(entity);
        if (match.containsEntity(eggwarsEntity)) return;


        match.addEntity(entity, eggwarsPlayer.getTeam());
    }
}