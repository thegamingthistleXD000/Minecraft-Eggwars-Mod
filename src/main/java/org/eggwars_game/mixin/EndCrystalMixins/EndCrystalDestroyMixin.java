package org.eggwars_game.mixin.EndCrystalMixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EndCrystalEntity.class)
public class EndCrystalDestroyMixin {

    @Inject(method = "crystalDestroyed", at = @At("HEAD"), cancellable = true)
    private void eggwars$preventCrystalDamage(
            DamageSource source,
            CallbackInfo ci
    ) {
        EndCrystalEntity crystal = (EndCrystalEntity) (Object) this;

        EggwarsMatch match = Eggwars.getMatch(crystal);
        if (match == null) return;
        ControlCrystal cc = match.getControlCrystal(crystal);
        if (cc == null) return;


        match.removeControlCrystal(match.getControlCrystal(crystal));
    }
}