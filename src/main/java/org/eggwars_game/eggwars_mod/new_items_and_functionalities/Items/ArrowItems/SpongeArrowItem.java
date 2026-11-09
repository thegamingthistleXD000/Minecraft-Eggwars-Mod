package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items.ArrowItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities.SpongeArrowEntity;

public class SpongeArrowItem extends ModArrowItem {

    public SpongeArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public SpongeArrowEntity createArrow(
            World world,
            ItemStack stack,
            LivingEntity shooter,
            ItemStack weapon
    ) {
        return new SpongeArrowEntity(world, shooter);
    }
}