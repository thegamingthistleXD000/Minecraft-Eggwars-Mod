package org.eggwars_game.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.eggwars_game.eggwars_mod.Utils.EggwarsEntityData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
    public class EntityDataMixin implements EggwarsEntityData {

        private final NbtCompound eggwars$data = new NbtCompound();

        @Override
        public NbtCompound eggwars$getData() {
            return eggwars$data;
        }
    }