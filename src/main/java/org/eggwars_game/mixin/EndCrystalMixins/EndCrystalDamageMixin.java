package org.eggwars_game.mixin.EndCrystalMixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.IndestructibleCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public class EndCrystalDamageMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void eggwars$preventCrystalDamage(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        EndCrystalEntity crystal = (EndCrystalEntity) (Object) this;

        EggwarsMatch match = Eggwars.getMatch(crystal);
        if (match == null) return;

        ControlCrystal cc = match.getControlCrystal(crystal);
        if (cc == null) return;

        Entity attacker = source.getAttacker();

        if (attacker != null
                && attacker.getCommandTags().contains("eggwars_god")) {
            return;
        }

        if (attacker != null
                && (cc.getTeam() == Eggwars.getMatch(attacker).getEggwarsEntity(attacker).getTeam()
                || cc.hasComponent(IndestructibleCrystal.class))) {
            cir.setReturnValue(false);
        }
    }
}