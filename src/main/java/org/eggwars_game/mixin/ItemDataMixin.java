package org.eggwars_game.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.eggwars_game.eggwars_mod.Utils.EggwarsStackData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemDataMixin implements EggwarsStackData {

    private final NbtCompound eggwars$data = new NbtCompound();

    @Override
    public NbtCompound eggwars$getData() {
        return eggwars$data;
    }
}






