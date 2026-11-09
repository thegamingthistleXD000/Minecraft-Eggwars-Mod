package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items.ArrowItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ModArrowItem extends ArrowItem {

    public ModArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(
            World world,
            ItemStack stack,
            LivingEntity shooter,
            ItemStack weapon
    ) {
        return new ArrowEntity(world, shooter, stack, weapon);
    }
}