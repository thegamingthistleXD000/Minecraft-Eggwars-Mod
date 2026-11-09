package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items.ArrowItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities.CobwebArrowEntity;

public class CobwebArrowItem extends ModArrowItem {

    public CobwebArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public CobwebArrowEntity createArrow(
            World world,
            ItemStack stack,
            LivingEntity shooter,
            ItemStack weapon
    ) {
        return new CobwebArrowEntity(world, shooter);
    }
}