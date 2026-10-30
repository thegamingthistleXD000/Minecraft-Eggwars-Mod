package org.eggwars_game.eggwars_mod.Utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class NBTUtils {

    public static NbtCompound get(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        return data == null ? null : data.copyNbt();
    }

    public static boolean has(ItemStack stack, String tag) {
        NbtCompound nbt = get(stack);
        return nbt != null && nbt.contains(tag);
    }

    public static String getString(ItemStack stack, String tag) {
        NbtCompound nbt = get(stack);
        return nbt == null ? null : nbt.getString(tag);
    }

    public static boolean getBoolean(ItemStack stack, String tag) {
        NbtCompound nbt = get(stack);
        return nbt != null && nbt.getBoolean(tag);
    }

    public static boolean hasValue(ItemStack stack, String tag, String value) {
        NbtCompound nbt = get(stack);
        return nbt != null
                && nbt.contains(tag)
                && nbt.getString(tag).equals(value);
    }


    public static void set(ItemStack stack, String tag, String value) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putString(tag, value);
        });
    }

    public static void set(ItemStack stack, String tag, boolean value) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putBoolean(tag, value);
        });
    }

    public static void set(Entity entity, String tag, String value) {
        ((EggwarsEntityData) entity).eggwars$getData().putString(tag, value);
    }

    public static void set(Entity entity, String tag, boolean value) {
        ((EggwarsEntityData) entity).eggwars$getData().putBoolean(tag, value);
    }

    public static boolean has(Entity entity, String tag) {
        return ((EggwarsEntityData) entity).eggwars$getData().contains(tag);
    }

    public static String getString(Entity entity, String tag) {
        return ((EggwarsEntityData) entity).eggwars$getData().getString(tag);
    }

    public static boolean getBoolean(Entity entity, String tag) {
        return ((EggwarsEntityData) entity).eggwars$getData().getBoolean(tag);
    }

    public static void remove(Entity entity, String tag) {
        ((EggwarsEntityData) entity).eggwars$getData().remove(tag);
    }

    public static void remove(ItemStack stack, String tag) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.remove(tag);
        });
    }
    
}