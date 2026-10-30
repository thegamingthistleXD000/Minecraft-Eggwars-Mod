package org.eggwars_game.eggwars_mod.Utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface EggwarsStackData {

    NbtCompound eggwars$getData();

    static NbtCompound get(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (component == null)
            return new NbtCompound();

        return component.copyNbt();
    }
}