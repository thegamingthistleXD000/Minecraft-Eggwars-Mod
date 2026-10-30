package org.eggwars_game.mixin.PlayerMixins;

import net.minecraft.server.network.ServerPlayerEntity;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.CrystalConnect.FastTravelPlayer;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(ServerPlayerEntity.class)
    public class FastTravelMixin implements FastTravelPlayer {

        private boolean fastTraveling;

        @Override
        public boolean eggwars$isFastTraveling() {
            return fastTraveling;
        }

        @Override
        public void eggwars$setFastTraveling(boolean value) {
            fastTraveling = value;
        }
    }