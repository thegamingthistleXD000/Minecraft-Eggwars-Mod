package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities.MilkBallEntity;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities.ModEntities;


public class MilkBallItem extends modItem implements ProjectileItem {

    public MilkBallItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createEntity(
            World world,
            Position pos,
            ItemStack stack,
            net.minecraft.util.math.Direction direction
    ) {
        return new MilkBallEntity(
        ModEntities.MILK_BALL,
        world);
    }

    public void shoot(World world, LivingEntity user, ItemStack stack) {
        if (world.isClient()) return;

        MilkBallEntity projectile = new MilkBallEntity(
                ModEntities.MILK_BALL,
                world
        );

        projectile.setItem(stack);
        projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);

        world.spawnEntity(projectile);
    }
}