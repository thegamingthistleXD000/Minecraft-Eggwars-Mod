package org.eggwars_game.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.effect.StatusEffectInstance;

import org.eggwars_game.eggwars_mod.simple_item_changes.TemporaryRiptide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin {

    @Inject(method = "applySplashPotion", at = @At("HEAD"))
    private void eggwars$activateRiptide(
            Iterable<StatusEffectInstance> effects,
            Entity entity,
            CallbackInfo ci
    ) {
        if (!(entity instanceof ServerPlayerEntity player))
            return;

        if (!(player.getWorld() instanceof ServerWorld))
            return;

        PotionEntity potion = (PotionEntity) (Object) this;

        if (potion.getOwner() != player)
            return;

        TemporaryRiptide.activate(player);
    }
}